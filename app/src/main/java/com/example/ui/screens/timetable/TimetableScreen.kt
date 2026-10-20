package com.example.ui.screens.timetable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TimetableConfigEntity
import com.example.ui.MainViewModel
import com.example.ui.components.AcademicTopAppBar
import com.example.ui.components.MetricCard
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DangerRose
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
    var editingSlot by remember { mutableStateOf<TimetableConfigEntity?>(null) }
    var slotToDelete by remember { mutableStateOf<TimetableConfigEntity?>(null) }

    val currentSlots = timetableConfigs.filter { it.scheduleType == activeScheduleType }
    val completedCount = currentSlots.count { slot ->
        dailySlotLogs.any { it.slotTitle == slot.slotTitle && it.isTickedDone }
    }
    val totalSlots = currentSlots.size.coerceAtLeast(1)
    val totalMinutes = currentSlots.sumOf { calculateIntervalMinutes(it.timeSlotStart, it.timeSlotEnd) }
    val totalScheduledDuration = formatMinutesToHours(totalMinutes)
    val streakDays = 7

    Scaffold(
        topBar = {
            AcademicTopAppBar(
                title = "Dynamic Timetable",
                onBack = onBack,
                onOpenDrawer = onOpenDrawer,
                actions = {
                    IconButton(
                        onClick = { showAddSlotDialog = true },
                        modifier = Modifier.testTag("top_appbar_add_slot_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Time Slot", tint = MaterialTheme.colorScheme.primary)
                    }
                }
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
            // Dynamic Template Selector (Holiday vs Working Day)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ACTIVE SCHEDULE TEMPLATE",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryIndigo,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$totalScheduledDuration total",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
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
                                Text("Working Day", fontWeight = FontWeight.SemiBold)
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
                                Text("Holiday Template", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Analytical Habit & Duration Summary
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
                        title = "Scheduled Time",
                        value = totalScheduledDuration,
                        subtitle = "$streakDays Days Streak 🔥",
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        accentColor = AccentGold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "EVENT TILES & TIME SLOTS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Tap pencil icon on any event tile to change slot name, time & interval",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedButton(
                        onClick = { showAddSlotDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("add_slot_header_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Add Slot", fontSize = 11.sp)
                    }
                }
            }

            // Event Tiles List
            if (currentSlots.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No time slots configured for ${activeScheduleType.replace("_", " ").lowercase()}.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { showAddSlotDialog = true },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Add First Time Slot")
                            }
                        }
                    }
                }
            } else {
                items(currentSlots, key = { it.id }) { slot ->
                    val isDone = dailySlotLogs.any { it.slotTitle == slot.slotTitle && it.isTickedDone }
                    val intervalMinutes = calculateIntervalMinutes(slot.timeSlotStart, slot.timeSlotEnd)
                    val intervalText = formatInterval(slot.timeSlotStart, slot.timeSlotEnd)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("slot_card_${slot.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Checkbox to mark complete for today
                            Checkbox(
                                checked = isDone,
                                onCheckedChange = { viewModel.toggleSlotCheck(slot.slotTitle, isDone) },
                                modifier = Modifier.testTag("slot_checkbox_${slot.id}")
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Slot details: Name, Interval badges, and Tags
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.toggleSlotCheck(slot.slotTitle, isDone) }
                            ) {
                                Text(
                                    text = slot.slotTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = if (isDone) FontWeight.Normal else FontWeight.Bold,
                                    color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Time span badge
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.AccessTime,
                                                contentDescription = null,
                                                modifier = Modifier.size(12.dp),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${slot.timeSlotStart} - ${slot.timeSlotEnd}",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    // Calculated Interval Duration badge
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = PrimaryIndigo.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = intervalText,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryIndigo,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    // Module Tag (if any)
                                    if (!slot.associatedModuleId.isNullOrBlank()) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = SecondaryCyan.copy(alpha = 0.12f)
                                        ) {
                                            Text(
                                                text = slot.associatedModuleId,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = SecondaryCyan,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Interactive Action Buttons on the Event Tile
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { editingSlot = slot },
                                    modifier = Modifier.size(36.dp).testTag("edit_slot_${slot.id}")
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit Slot Time, Name & Interval",
                                        tint = PrimaryIndigo,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { slotToDelete = slot },
                                    modifier = Modifier.size(36.dp).testTag("delete_slot_${slot.id}")
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete Slot",
                                        tint = DangerRose.copy(alpha = 0.65f),
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

    // --- DIALOGS ---

    // 1. Edit Time Slot, Name & Interval Dialog
    if (editingSlot != null) {
        val slot = editingSlot!!
        var title by remember(slot) { mutableStateOf(slot.slotTitle) }
        var start by remember(slot) { mutableStateOf(slot.timeSlotStart) }
        var end by remember(slot) { mutableStateOf(slot.timeSlotEnd) }
        var selectedModule by remember(slot) { mutableStateOf(slot.associatedModuleId ?: "") }

        val currentIntervalMins = calculateIntervalMinutes(start, end)
        val formattedCurrentInterval = formatInterval(start, end)

        AlertDialog(
            onDismissRequest = { editingSlot = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EditCalendar, contentDescription = null, tint = PrimaryIndigo)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Slot & Interval", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Slot Name Input
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Slot Name / Focus Activity") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_slot_title_input"),
                        singleLine = true
                    )

                    // Start Time & End Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = start,
                            onValueChange = { start = it },
                            label = { Text("Start Time (HH:mm)") },
                            modifier = Modifier.weight(1f).testTag("edit_slot_start_input"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = end,
                            onValueChange = { end = it },
                            label = { Text("End Time (HH:mm)") },
                            modifier = Modifier.weight(1f).testTag("edit_slot_end_input"),
                            singleLine = true
                        )
                    }

                    // Quick Interval Presets (Adjusts End Time from Start Time)
                    Column {
                        Text(
                            text = "Quick Interval Presets (Adjusts End Time):",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val presetIntervals = listOf(
                                "30m" to 30,
                                "45m" to 45,
                                "1 hour" to 60,
                                "1h 30m" to 90,
                                "2 hours" to 120,
                                "3 hours" to 180
                            )
                            presetIntervals.forEach { (label, minutes) ->
                                AssistChip(
                                    onClick = { end = addMinutesToTime(start, minutes) },
                                    label = { Text(label, fontSize = 11.sp) },
                                    leadingIcon = {
                                        Icon(Icons.Default.HourglassBottom, contentDescription = null, modifier = Modifier.size(12.dp))
                                    }
                                )
                            }
                        }
                    }

                    // Live Computed Interval Duration Banner
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PrimaryIndigo.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = PrimaryIndigo, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Current Interval: $formattedCurrentInterval",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryIndigo
                                )
                                Text(
                                    text = "From $start to $end ($currentIntervalMins mins)",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Associated Module Tag
                    Column {
                        Text(
                            text = "Associated Focus Module (Optional):",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val modules = listOf("GATE", "ESE", "COLLEGE_1", "COLLEGE_2", "UPSKILLING", "RESUME", "")
                            modules.forEach { mod ->
                                FilterChip(
                                    selected = selectedModule == mod,
                                    onClick = { selectedModule = mod },
                                    label = { Text(if (mod.isEmpty()) "None" else mod, fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && start.isNotBlank() && end.isNotBlank()) {
                            viewModel.updateTimetableSlotDetails(
                                slotId = slot.id,
                                title = title.trim(),
                                start = start.trim(),
                                end = end.trim(),
                                moduleId = if (selectedModule.isBlank()) null else selectedModule
                            )
                            editingSlot = null
                        }
                    },
                    modifier = Modifier.testTag("confirm_edit_slot_btn")
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSlot = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 2. Add Time Slot Dialog
    if (showAddSlotDialog) {
        var title by remember { mutableStateOf("") }
        var start by remember { mutableStateOf("09:00") }
        var end by remember { mutableStateOf("10:30") }
        var selectedModule by remember { mutableStateOf("") }

        val currentIntervalMins = calculateIntervalMinutes(start, end)
        val formattedCurrentInterval = formatInterval(start, end)

        AlertDialog(
            onDismissRequest = { showAddSlotDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AddAlarm, contentDescription = null, tint = PrimaryIndigo)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Timetable Slot & Interval", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Slot Name
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Slot Name (e.g. Deep Work, Lab, PYQ Practice)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_slot_title"),
                        singleLine = true
                    )

                    // Start Time & End Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = start,
                            onValueChange = { start = it },
                            label = { Text("Start Time") },
                            modifier = Modifier.weight(1f).testTag("add_slot_start"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = end,
                            onValueChange = { end = it },
                            label = { Text("End Time") },
                            modifier = Modifier.weight(1f).testTag("add_slot_end"),
                            singleLine = true
                        )
                    }

                    // Quick Interval Presets
                    Column {
                        Text(
                            text = "Quick Interval Presets (Adjusts End Time):",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val presetIntervals = listOf(
                                "30m" to 30,
                                "45m" to 45,
                                "1 hour" to 60,
                                "1h 30m" to 90,
                                "2 hours" to 120,
                                "3 hours" to 180
                            )
                            presetIntervals.forEach { (label, minutes) ->
                                AssistChip(
                                    onClick = { end = addMinutesToTime(start, minutes) },
                                    label = { Text(label, fontSize = 11.sp) },
                                    leadingIcon = {
                                        Icon(Icons.Default.HourglassBottom, contentDescription = null, modifier = Modifier.size(12.dp))
                                    }
                                )
                            }
                        }
                    }

                    // Computed Duration Banner
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PrimaryIndigo.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = PrimaryIndigo, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Interval Duration: $formattedCurrentInterval",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryIndigo
                                )
                                Text(
                                    text = "From $start to $end ($currentIntervalMins mins)",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Associated Module Tag
                    Column {
                        Text(
                            text = "Associated Focus Module (Optional):",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val modules = listOf("GATE", "ESE", "COLLEGE_1", "COLLEGE_2", "UPSKILLING", "RESUME", "")
                            modules.forEach { mod ->
                                FilterChip(
                                    selected = selectedModule == mod,
                                    onClick = { selectedModule = mod },
                                    label = { Text(if (mod.isEmpty()) "None" else mod, fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addTimetableSlot(
                                type = activeScheduleType,
                                start = start.trim(),
                                end = end.trim(),
                                title = title.trim(),
                                moduleId = if (selectedModule.isBlank()) null else selectedModule
                            )
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

    // 3. Delete Slot Confirmation Dialog
    if (slotToDelete != null) {
        val slot = slotToDelete!!
        AlertDialog(
            onDismissRequest = { slotToDelete = null },
            title = { Text("Delete Time Slot?") },
            text = { Text("Are you sure you want to delete '${slot.slotTitle}' (${slot.timeSlotStart} - ${slot.timeSlotEnd})?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTimetableSlot(slot.id)
                        slotToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRose)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { slotToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Calculates interval duration in minutes between HH:mm start and end strings.
 */
private fun calculateIntervalMinutes(start: String, end: String): Int {
    return try {
        val (startH, startM) = start.split(":").map { it.trim().toInt() }
        val (endH, endM) = end.split(":").map { it.trim().toInt() }
        var diff = (endH * 60 + endM) - (startH * 60 + startM)
        if (diff < 0) diff += 24 * 60
        diff
    } catch (e: Exception) {
        60
    }
}

/**
 * Formats start and end times into a readable interval (e.g. "1h 30m").
 */
private fun formatInterval(start: String, end: String): String {
    val mins = calculateIntervalMinutes(start, end)
    val hours = mins / 60
    val remMins = mins % 60
    return when {
        hours > 0 && remMins > 0 -> "${hours}h ${remMins}m"
        hours > 0 -> "${hours}h"
        else -> "${remMins}m"
    }
}

/**
 * Formats total minutes into readable hours and minutes (e.g. "8h 30m").
 */
private fun formatMinutesToHours(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return when {
        hours > 0 && mins > 0 -> "${hours}h ${mins}m"
        hours > 0 -> "${hours}h"
        else -> "${mins}m"
    }
}

/**
 * Adds minutes to an HH:mm string and returns the new HH:mm string.
 */
private fun addMinutesToTime(startTime: String, minutesToAdd: Int): String {
    return try {
        val (h, m) = startTime.split(":").map { it.trim().toInt() }
        val totalMins = (h * 60 + m + minutesToAdd) % (24 * 60)
        val newH = totalMins / 60
        val newM = totalMins % 60
        String.format("%02d:%02d", newH, newM)
    } catch (e: Exception) {
        startTime
    }
}
