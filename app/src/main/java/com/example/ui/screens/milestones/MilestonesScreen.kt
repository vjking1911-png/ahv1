package com.example.ui.screens.milestones

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.AcademicTopAppBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DangerRose
import com.example.ui.theme.PrimaryIndigo
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MilestonesScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val allMilestones by viewModel.allMilestones.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val now = System.currentTimeMillis()

    Scaffold(
        topBar = {
            AcademicTopAppBar(
                title = "Semester Deadlines & Alert Engine",
                onBack = onBack,
                onOpenDrawer = onOpenDrawer
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryIndigo,
                modifier = Modifier.testTag("add_milestone_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Deadline", tint = Color.White)
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
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryIndigo.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = PrimaryIndigo)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Proactive 24-Hour Alert Engine",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryIndigo
                            )
                            Text(
                                text = "System alarms schedule broadcast alerts 24 hours prior to exams and project submission cutoffs.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (allMilestones.isEmpty()) {
                item {
                    Text(
                        text = "No upcoming semester deadlines or exam dates added yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(allMilestones, key = { it.id }) { milestone ->
                    val diff = milestone.targetTimestamp - now
                    val hoursRemaining = (diff / (1000 * 60 * 60)).coerceAtLeast(0)
                    val daysRemaining = hoursRemaining / 24

                    val isUrgent = hoursRemaining <= 24

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("milestone_card_${milestone.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUrgent) DangerRose.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = milestone.eventTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    val dateStr = SimpleDateFormat("EEE, dd MMM yyyy • HH:mm", Locale.getDefault()).format(Date(milestone.targetTimestamp))
                                    Text(
                                        text = dateStr,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isUrgent) DangerRose else PrimaryIndigo
                                ) {
                                    Text(
                                        text = if (daysRemaining > 0) "In $daysRemaining days" else "In ${hoursRemaining}h",
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
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))

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
                                        Text("Test Alert", fontSize = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { viewModel.deleteMilestone(milestone.id) },
                                        modifier = Modifier.size(28.dp).testTag("delete_milestone_${milestone.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRose.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var daysAheadStr by remember { mutableStateOf("3") }
        var profile by remember { mutableStateOf("COLLEGE_1") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Semester Milestone") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Event / Exam / Submission Title") },
                        modifier = Modifier.fillMaxWidth().testTag("add_milestone_title")
                    )
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Notes / Venue / Syllabus Covered") },
                        modifier = Modifier.fillMaxWidth().testTag("add_milestone_desc")
                    )
                    OutlinedTextField(
                        value = daysAheadStr,
                        onValueChange = { daysAheadStr = it },
                        label = { Text("Days from now (e.g. 1 for tomorrow, 7 for a week)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_milestone_days")
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("COLLEGE_1", "COLLEGE_2", "GATE", "ESE").forEach { p ->
                            FilterChip(
                                selected = profile == p,
                                onClick = { profile = p },
                                label = { Text(p, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val days = daysAheadStr.toLongOrNull() ?: 1L
                        val target = System.currentTimeMillis() + days * 86400000L
                        if (title.isNotBlank()) {
                            viewModel.addMilestone(title, desc, target, profile)
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_milestone")
                ) {
                    Text("Add Milestone")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
