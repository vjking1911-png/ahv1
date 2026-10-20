package com.example.ui.screens.timetable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
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
import com.example.ui.components.MetricCard
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SuccessEmerald

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val activeScheduleType by viewModel.activeScheduleType.collectAsState()
    val timetableConfigs by viewModel.timetableConfigs.collectAsState()
    val dailySlotLogs by viewModel.dailySlotLogs.collectAsState()

    var showAddSlotDialog by remember { mutableStateOf(false) }

    val currentSlots = timetableConfigs.filter { it.scheduleType == activeScheduleType }
    val completedCount = currentSlots.count { slot ->
        dailySlotLogs.any { it.slotTitle == slot.slotTitle && it.isTickedDone }
    }
    val totalSlots = currentSlots.size.coerceAtLeast(1)
    val streakDays = 7 // Analytical focus habit streak

    Scaffold(
        topBar = {
            AcademicTopAppBar(
                title = "Dynamic Timetable",
                onBack = onBack,
                onOpenDrawer = onOpenDrawer
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSlotDialog = true },
                containerColor = PrimaryIndigo,
                modifier = Modifier.testTag("add_timetable_slot_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Time Slot", tint = Color.White)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("timetable_scroll"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dynamic Template Selector (Tinkered Feature: Holiday vs Working Day)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ACTIVE TEMPLATE SELECTOR",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryIndigo,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.setScheduleType("WORKING_DAY") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (activeScheduleType == "WORKING_DAY") PrimaryIndigo else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (activeScheduleType == "WORKING_DAY") Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("btn_select_working_day")
                            ) {
                                Text("Working Day")
                            }

                            Button(
                                onClick = { viewModel.setScheduleType("HOLIDAY") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (activeScheduleType == "HOLIDAY") PrimaryIndigo else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (activeScheduleType == "HOLIDAY") Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("btn_select_holiday")
                            ) {
                                Text("Holiday Template")
                            }
                        }
                    }
                }
            }

            // Analytical Habit & Streak Tracker
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Today's Focus Slots",
                        value = "$completedCount / ${currentSlots.size}",
                        subtitle = "${(completedCount * 100) / totalSlots}% completed",
                        icon = Icons.Default.Schedule,
                        accentColor = PrimaryIndigo,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Completion Streak",
                        value = "$streakDays Days 🔥",
                        subtitle = "Analytical Routine Score",
                        icon = Icons.Default.TrendingUp,
                        accentColor = AccentGold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Slot list
            items(currentSlots, key = { it.id }) { slot ->
                val isDone = dailySlotLogs.any { it.slotTitle == slot.slotTitle && it.isTickedDone }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleSlotCheck(slot.slotTitle, isDone) }
                        .testTag("slot_card_${slot.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isDone,
                            onCheckedChange = { viewModel.toggleSlotCheck(slot.slotTitle, isDone) },
                            modifier = Modifier.testTag("slot_checkbox_${slot.id}")
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = slot.slotTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (isDone) FontWeight.Normal else FontWeight.Bold,
                                color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${slot.timeSlotStart} - ${slot.timeSlotEnd}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (slot.associatedModuleId != null) {
                                Text(
                                    text = "Tag: ${slot.associatedModuleId}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryIndigo,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddSlotDialog) {
        var start by remember { mutableStateOf("16:00") }
        var end by remember { mutableStateOf("17:30") }
        var title by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddSlotDialog = false },
            title = { Text("Add Timetable Slot") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Slot Name (e.g. Deep Work, Revision)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_slot_title")
                    )
                    OutlinedTextField(
                        value = start,
                        onValueChange = { start = it },
                        label = { Text("Start Time (e.g. 09:00)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_slot_start")
                    )
                    OutlinedTextField(
                        value = end,
                        onValueChange = { end = it },
                        label = { Text("End Time (e.g. 10:30)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_slot_end")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addTimetableSlot(activeScheduleType, start, end, title)
                            showAddSlotDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_slot")
                ) {
                    Text("Add Slot")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSlotDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
