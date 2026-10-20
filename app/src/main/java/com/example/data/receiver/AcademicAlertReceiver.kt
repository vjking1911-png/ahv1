package com.example.data.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class AcademicAlertReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra("EVENT_TITLE") ?: "Academic Milestone Approaching!"
        val description = intent?.getStringExtra("EVENT_DESCRIPTION") ?: "You have a deadline in less than 24 hours."

        showNotification(context, title, description)
    }

    companion object {
        const val CHANNEL_ID = "academic_hub_alerts"
        const val CHANNEL_NAME = "Academic Hub Milestones & Deadlines"

        fun scheduleMilestoneAlert(context: Context, milestone: com.example.data.model.AcademicMilestoneEntity) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? android.app.AlarmManager ?: return
            val intent = Intent(context, AcademicAlertReceiver::class.java).apply {
                putExtra("EVENT_TITLE", "⚠️ Tomorrow: ${milestone.eventTitle}")
                putExtra("EVENT_DESCRIPTION", "Academic Milestone deadline in 24 hours: ${milestone.eventDescription.ifBlank { milestone.collegeProfileId }}")
                putExtra("MILESTONE_ID", milestone.id)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                milestone.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Alert pops up exactly 1 day (24 hours) before the event date
            val oneDayBefore = milestone.targetTimestamp - (24 * 60 * 60 * 1000L)
            val now = System.currentTimeMillis()
            val triggerTime = when {
                oneDayBefore > now -> oneDayBefore
                milestone.targetTimestamp > now -> now + 5000L // If event is less than 24h away, notify shortly
                else -> return // Already past
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } else {
                    alarmManager.set(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            } catch (e: Exception) {
                // Ignore security or permission restrictions gracefully
            }
        }

        fun cancelMilestoneAlert(context: Context, milestoneId: String) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? android.app.AlarmManager ?: return
            val intent = Intent(context, AcademicAlertReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                milestoneId.hashCode(),
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
            }
        }

        fun showNotification(context: Context, title: String, description: String) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    this.description = "Alerts triggered 24 hours prior to academic exam and project deadlines."
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }

            val launchIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(NotificationCompat.BigTextStyle().bigText(description))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
        }
    }
}
