package com.example.ui.screens.milestones

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.MainViewModel
import com.example.ui.components.AcademicTopAppBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DangerRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SuccessEmerald
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MilestonesScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val allMilestones by viewModel.allMilestones.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    // Notification Permission Handling for Android 13+
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
    }

    val now = System.currentTimeMillis()
    // Sorted chronologically
    val sortedMilestones = remember(allMilestones) {
        allMilestones.sortedBy { it.targetTimestamp }
    }

    Scaffold(
        topBar = {
            AcademicTopAppBar(
                title = "Milestone Calendar & 1-Day Alerts",
                onBack = onBack,
                onOpenDrawer = onOpenDrawer,
                actions = {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("appbar_add_milestone_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Milestone", tint = PrimaryIndigo)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryIndigo,
                modifier = Modifier.testTag("add_milestone_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Milestone", tint = Color.White)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("milestones_scroll"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Notification Permission Banner if not yet granted on Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AccentGold.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = AccentGold)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Enable 1-Day Prior Notifications",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Grant notification permission so the 24-hour advance alert can pop up.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Allow", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Engine Explanation Banner
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = PrimaryIndigo.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = PrimaryIndigo,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Proactive 1-Day Prior Alert Engine",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryIndigo
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Add events tomorrow or months ahead using the calendar picker. System alarms automatically schedule pop-up alerts exactly 1 day (24 hours) prior to every exam and submission.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Milestones Counter / Quick Summary
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SCHEDULED DEADLINES & EXAMS (${sortedMilestones.size})",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedButton(
                        onClick = { showAddDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("btn_quick_add_milestone")
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Add Calendar Event", fontSize = 11.sp)
                    }
                }
            }

            if (sortedMilestones.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.EventAvailable,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No academic deadlines or exams scheduled yet.",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap the button below to pick a calendar date from tomorrow to months ahead.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { showAddDialog = true },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Add First Calendar Milestone")
                            }
                        }
                    }
                }
            } else {
                items(sortedMilestones, key = { it.id }) { milestone ->
                    val diff = milestone.targetTimestamp - now
                    val hoursRemaining = (diff / (1000 * 60 * 60))
                    val daysRemaining = diff / (1000 * 60 * 60 * 24)
                    val isPast = diff < 0
                    val isWithin24h = !isPast && hoursRemaining in 0..24

                    // Calculate the 1-day prior alert timestamp (target - 24 hours)
                    val alertTimestamp = milestone.targetTimestamp - (24 * 60 * 60 * 1000L)
                    val alertPassed = now >= alertTimestamp

                    val eventDateFormat = SimpleDateFormat("EEEE, dd MMM yyyy • hh:mm a", Locale.getDefault())
                    val alertDateFormat = SimpleDateFormat("EEE, dd MMM yyyy • hh:mm a", Locale.getDefault())

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("milestone_card_${milestone.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isPast -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                isWithin24h -> DangerRose.copy(alpha = 0.08f)
                                else -> MaterialTheme.colorScheme.surface
                            }
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header Row: Title & Days Remaining Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = milestone.eventTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPast) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            modifier = Modifier.size(13.dp),
                                            tint = PrimaryIndigo
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = eventDateFormat.format(Date(milestone.targetTimestamp)),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = PrimaryIndigo
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when {
                                        isPast -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                        isWithin24h -> DangerRose
                                        daysRemaining <= 7 -> AccentGold
                                        else -> PrimaryIndigo
                                    }
                                ) {
                                    Text(
                                        text = when {
                                            isPast -> "Past Due"
                                            isWithin24h -> "Within 24h! 🚨"
                                            daysRemaining == 1L -> "Tomorrow! 🔔"
                                            daysRemaining in 2L..30L -> "In $daysRemaining days"
                                            else -> {
                                                val months = daysRemaining / 30
                                                val remainderDays = daysRemaining % 30
                                                if (remainderDays > 0) "In $months mos, $remainderDays d" else "In $months mos"
                                            }
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            if (milestone.eventDescription.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = milestone.eventDescription,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 1-Day Prior Alert Scheduling Tile
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isWithin24h) DangerRose.copy(alpha = 0.12f) else SecondaryCyan.copy(alpha = 0.12f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp),
                                        tint = if (isWithin24h) DangerRose else SecondaryCyan
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = if (alertPassed) {
                                                "1-Day Advance Alert Triggered on ${alertDateFormat.format(Date(alertTimestamp))}"
                                            } else {
                                                "Pop-up Alert Scheduled: ${alertDateFormat.format(Date(alertTimestamp))} (1 day before)"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isWithin24h) DangerRose else SecondaryCyan
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Footer: Profile Badge & Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusBadge(status = milestone.collegeProfileId)

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedButton(
                                        onClick = { viewModel.triggerTestAlert(milestone) },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.testTag("trigger_alert_btn_${milestone.id}")
                                    ) {
                                        Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Test Alert", fontSize = 11.sp)
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    IconButton(
                                        onClick = { viewModel.deleteMilestone(milestone.id) },
                                        modifier = Modifier.size(32.dp).testTag("delete_milestone_${milestone.id}")
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = DangerRose.copy(alpha = 0.7f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- ADD MILESTONE WITH FULL CALENDAR DATE PICKER ---
    if (showAddDialog) {
        AddMilestoneCalendarDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, desc, targetTimestamp, profile ->
                viewModel.addMilestone(title, desc, targetTimestamp, profile)
                showAddDialog = false
            }
        )
    }
}

/**
 * Dialog allowing user to pick any calendar date from tomorrow to months ahead,
 * with quick preset shortcuts (+1d, +3d, +1w, +1mo, +3mo, +6mo) and automatic
 * 1-day prior alert preview.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMilestoneCalendarDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, desc: String, targetTimestamp: Long, profile: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var profile by remember { mutableStateOf("COLLEGE_1") }

    // Calendar state initialized to tomorrow at 09:00 AM
    val calendar = remember {
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    var selectedCalendar by remember { mutableStateOf(calendar) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    // Time Selection (hour and minute)
    var selectedHour by remember { mutableIntStateOf(9) }
    var selectedMinute by remember { mutableIntStateOf(0) }

    val eventDateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
    val alertDateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy 'at' hh:mm a", Locale.getDefault())

    val targetMillis = remember(selectedCalendar, selectedHour, selectedMinute) {
        val c = selectedCalendar.clone() as Calendar
        c.set(Calendar.HOUR_OF_DAY, selectedHour)
        c.set(Calendar.MINUTE, selectedMinute)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        c.timeInMillis
    }

    val alertMillis = targetMillis - (24 * 60 * 60 * 1000L)
    val now = System.currentTimeMillis()
    val daysAhead = ((targetMillis - now) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EventNote, contentDescription = null, tint = PrimaryIndigo)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Milestone & 1-Day Alert", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_milestone_dialog_column"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Event Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Event / Exam / Submission Title *") },
                    placeholder = { Text("e.g. End Semester Exam, Capstone Submission") },
                    modifier = Modifier.fillMaxWidth().testTag("add_milestone_title"),
                    singleLine = true
                )

                // Notes / Venue
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Notes / Venue / Syllabus Covered") },
                    placeholder = { Text("e.g. Hall B, Units 1 to 5") },
                    modifier = Modifier.fillMaxWidth().testTag("add_milestone_desc"),
                    maxLines = 2
                )

                // Calendar Date Picker Button / Card
                Text(
                    text = "EVENT CALENDAR DATE:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Card(
                    onClick = { showDatePickerDialog = true },
                    modifier = Modifier.fillMaxWidth().testTag("btn_open_calendar_picker"),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = PrimaryIndigo)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = eventDateFormat.format(selectedCalendar.time),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (daysAhead == 1L) "Tomorrow (+1 day)" else "In $daysAhead days (~${daysAhead / 30} months ahead) • Tap to change",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(Icons.Default.EditCalendar, contentDescription = "Pick Date", tint = PrimaryIndigo)
                    }
                }

                // Quick Date Preset Shortcuts (Tomorrow to Months Ahead)
                Column {
                    Text(
                        text = "Quick Presets (Tomorrow to Months Ahead):",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val presets = listOf(
                            "Tomorrow" to 1,
                            "+3 Days" to 3,
                            "+1 Week" to 7,
                            "+2 Weeks" to 14,
                            "+1 Month" to 30,
                            "+2 Months" to 60,
                            "+3 Months" to 90,
                            "+6 Months" to 180
                        )
                        presets.forEach { (label, days) ->
                            SuggestionChip(
                                onClick = {
                                    val newCal = Calendar.getInstance().apply {
                                        add(Calendar.DAY_OF_YEAR, days)
                                    }
                                    selectedCalendar = newCal
                                },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                // Time Preset Selector (Default 09:00 AM)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Time of Event:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(
                            "09:00 AM" to (9 to 0),
                            "10:00 AM" to (10 to 0),
                            "02:00 PM" to (14 to 0),
                            "05:00 PM" to (17 to 0),
                            "11:59 PM" to (23 to 59)
                        ).forEach { (timeLabel, pair) ->
                            val isSelected = selectedHour == pair.first && selectedMinute == pair.second
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedHour = pair.first
                                    selectedMinute = pair.second
                                },
                                label = { Text(timeLabel, fontSize = 10.sp) }
                            )
                        }
                    }
                }

                // Proactive 1-Day Prior Alert Guarantee Preview
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SuccessEmerald.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AlarmOn, contentDescription = null, tint = SuccessEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AUTOMATIC 1-DAY PRIOR ALERT",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = SuccessEmerald
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Pop-up alert will fire on ${alertDateFormat.format(Date(alertMillis))} (exactly 24 hours before the event).",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Academic Profile Tag
                Text(
                    text = "ACADEMIC PROFILE:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("COLLEGE_1", "COLLEGE_2", "GATE", "ESE", "PROJECTS").forEach { p ->
                        FilterChip(
                            selected = profile == p,
                            onClick = { profile = p },
                            label = { Text(p, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title.trim(), desc.trim(), targetMillis, profile)
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("confirm_add_milestone")
            ) {
                Text("Schedule Milestone")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    // Material 3 Date Picker Dialog
    if (showDatePickerDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedCalendar.timeInMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val epoch = datePickerState.selectedDateMillis
                        if (epoch != null) {
                            val newCal = Calendar.getInstance().apply {
                                timeInMillis = epoch
                            }
                            selectedCalendar = newCal
                        }
                        showDatePickerDialog = false
                    }
                ) {
                    Text("Select Date")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = "Select Milestone Date (Tomorrow to Months Ahead)",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    }
}
