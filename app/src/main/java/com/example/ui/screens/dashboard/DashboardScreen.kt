package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.MetricCard
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigate: (String) -> Unit,
    onOpenDrawer: () -> Unit = {}
) {
    val activeScheduleType by viewModel.activeScheduleType.collectAsState()
    val timetableConfigs by viewModel.timetableConfigs.collectAsState()
    val dailySlotLogs by viewModel.dailySlotLogs.collectAsState()
    val scratchpad by viewModel.scratchpad.collectAsState()
    val allMilestones by viewModel.allMilestones.collectAsState()
    val allTopics by viewModel.allTopics.collectAsState()
    val expenses by viewModel.expenses.collectAsState()

    var isEditingNotepad by remember { mutableStateOf(false) }
    var scratchpadInput by remember { mutableStateOf("") }

    LaunchedEffect(scratchpad) {
        if (!isEditingNotepad) {
            scratchpadInput = scratchpad?.rawMarkdownText ?: ""
        }
    }

    // Active slots for today's schedule type
    val todaySlots = timetableConfigs.filter { it.scheduleType == activeScheduleType }
    val completedSlotsCount = todaySlots.count { slot ->
        dailySlotLogs.any { it.slotTitle == slot.slotTitle && it.isTickedDone }
    }

    // Urgent milestone (under 48 hours)
    val now = System.currentTimeMillis()
    val urgentMilestone = allMilestones.firstOrNull { it.targetTimestamp > now }

    val completedTopics = allTopics.count { it.status == "DONE" }
    val totalTopics = (allTopics.size).coerceAtLeast(1)
    val totalExpense = expenses.sumOf { it.amount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen_scroll"),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryIndigoDark)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onOpenDrawer,
                                modifier = Modifier.testTag("dashboard_drawer_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Open Navigation Drawer",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = "ACADEMIC HUB",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AccentGold,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                )
                                Text(
                                    text = "Command Center",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = AccentGold.copy(alpha = 0.25f),
                                modifier = Modifier
                                    .clickable { onNavigate(Screen.Customizer.route) }
                                    .testTag("dashboard_customize_header_btn")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Tune, contentDescription = "Customize", tint = AccentGold, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Customize",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentGold
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Offline Vault",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "Syllabus Progress",
                            value = "${(completedTopics * 100) / totalTopics}%",
                            subtitle = "$completedTopics of $totalTopics done",
                            icon = Icons.Default.School,
                            accentColor = SecondaryCyanLight,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Academic Spend",
                            value = "₹${String.format("%,.0f", totalExpense)}",
                            subtitle = "${expenses.size} items tracked",
                            icon = Icons.Default.AccountBalanceWallet,
                            accentColor = AccentGoldLight,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Module 10 Proactive Alert Banner (if milestone exists)
        if (urgentMilestone != null) {
            item {
                val hoursRemaining = ((urgentMilestone.targetTimestamp - now) / (1000 * 60 * 60)).coerceAtLeast(0)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("urgent_milestone_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (hoursRemaining < 24) DangerRose.copy(alpha = 0.12f) else AccentGold.copy(alpha = 0.12f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NotificationImportant,
                                    contentDescription = null,
                                    tint = if (hoursRemaining < 24) DangerRose else AccentGold,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (hoursRemaining < 24) "⚠️ 24H PROACTIVE ALERT" else "UPCOMING MILESTONE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (hoursRemaining < 24) DangerRose else AccentGold
                                )
                            }
                            Text(
                                text = "in ${hoursRemaining}h",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (hoursRemaining < 24) DangerRose else AccentGold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = urgentMilestone.eventTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = urgentMilestone.eventDescription,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.triggerTestAlert(urgentMilestone) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("test_system_alert_button")
                            ) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Test System Notification")
                            }
                        }
                    }
                }
            }
        }

        // Module 8: Today's Dynamic Timetable Focus
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("dashboard_timetable_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "MODULE 8: DYNAMIC TIMETABLE",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryIndigo,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Daily Hourly Schedule",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Switch Template Toggle
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(2.dp)
                        ) {
                            FilterChip(
                                selected = activeScheduleType == "WORKING_DAY",
                                onClick = { viewModel.setScheduleType("WORKING_DAY") },
                                label = { Text("Working Day", fontSize = 11.sp) },
                                modifier = Modifier.testTag("timetable_working_day_toggle")
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            FilterChip(
                                selected = activeScheduleType == "HOLIDAY",
                                onClick = { viewModel.setScheduleType("HOLIDAY") },
                                label = { Text("Holiday", fontSize = 11.sp) },
                                modifier = Modifier.testTag("timetable_holiday_toggle")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Focus Completion: $completedSlotsCount / ${todaySlots.size} slots finished",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { if (todaySlots.isNotEmpty()) completedSlotsCount.toFloat() / todaySlots.size else 0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = SuccessEmerald
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    todaySlots.take(3).forEach { slot ->
                        val isDone = dailySlotLogs.any { it.slotTitle == slot.slotTitle && it.isTickedDone }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleSlotCheck(slot.slotTitle, isDone) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isDone,
                                onCheckedChange = { viewModel.toggleSlotCheck(slot.slotTitle, isDone) },
                                modifier = Modifier.testTag("dashboard_slot_checkbox_${slot.id}")
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = slot.slotTitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isDone) FontWeight.Normal else FontWeight.Medium,
                                    color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${slot.timeSlotStart} - ${slot.timeSlotEnd}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    TextButton(
                        onClick = { onNavigate(Screen.TimetableHub.route) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Manage Timetable Slots →")
                    }
                }
            }
        }

        // Module 9: Front-Page Priority Notepad
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("dashboard_scratchpad_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EditNote, contentDescription = null, tint = AccentGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "MODULE 9: PRIORITY NOTEPAD",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AccentGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Markdown Scratchpad",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                if (isEditingNotepad) {
                                    viewModel.updateScratchpadText(scratchpadInput)
                                }
                                isEditingNotepad = !isEditingNotepad
                            },
                            modifier = Modifier.testTag("toggle_scratchpad_edit_button")
                        ) {
                            Icon(
                                imageVector = if (isEditingNotepad) Icons.Default.Check else Icons.Default.Edit,
                                contentDescription = "Edit or Save Note",
                                tint = PrimaryIndigo
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isEditingNotepad) {
                        OutlinedTextField(
                            value = scratchpadInput,
                            onValueChange = { scratchpadInput = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp)
                                .testTag("scratchpad_edit_field"),
                            placeholder = { Text("Write tasks with - [ ] Task name...") },
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        // Render interactive Markdown Checklist
                        val text = scratchpad?.rawMarkdownText ?: scratchpadInput
                        val lines = text.lines()

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            lines.forEachIndexed { index, line ->
                                when {
                                    line.startsWith("# ") -> {
                                        Text(
                                            text = line.removePrefix("# "),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    line.contains("- [ ]") || line.contains("- [x]") || line.contains("- [X]") -> {
                                        val isChecked = line.contains("- [x]") || line.contains("- [X]")
                                        val taskLabel = line.replace("- [ ]", "").replace("- [x]", "").replace("- [X]", "").trim()
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { viewModel.toggleMarkdownCheckbox(index) }
                                                .padding(vertical = 2.dp)
                                        ) {
                                            Checkbox(
                                                checked = isChecked,
                                                onCheckedChange = { viewModel.toggleMarkdownCheckbox(index) },
                                                modifier = Modifier.testTag("notepad_checkbox_$index")
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = taskLabel,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = if (isChecked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                    line.isNotBlank() -> {
                                        Text(
                                            text = line,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Navigation to all 13 Academic Modules
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "ALL 13 ACADEMIC MODULES",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Module Grid
                val modules = listOf(
                    ModuleNavData("1. Academic Expenses", "Track ROI rating & semesters", Icons.Default.ReceiptLong, Screen.Expenses.route, PrimaryIndigo),
                    ModuleNavData("2 & 3. Dual Syllabus", "Degree 1 & Degree 2 Workload", Icons.Default.MenuBook, Screen.DualSyllabus.route, SecondaryCyan),
                    ModuleNavData("4. CGPA Formula Engine", "Dynamic tokenizing sandbox", Icons.Default.Calculate, Screen.ResultsFormula.route, AccentGold),
                    ModuleNavData("5. Triple-Paper GATE", "Revision loop + confidence slider", Icons.Default.FilterFrames, Screen.GateTracker.route, PrimaryIndigoDark),
                    ModuleNavData("6. UPSC ESE Tracker", "Stage 1 & 2 + PYQs solved", Icons.Default.Gavel, Screen.EseTracker.route, WarningAmber),
                    ModuleNavData("7. Certificates Hub", "Digital paper vault & wishlist", Icons.Default.Verified, Screen.Certificates.route, SuccessEmerald),
                    ModuleNavData("8. Dynamic Timetable", "Holiday vs working day slots", Icons.Default.Schedule, Screen.TimetableHub.route, SecondaryCyanDark),
                    ModuleNavData("9. Priority Notepad", "Markdown checklist anchor", Icons.Default.Checklist, Screen.Dashboard.route, AccentGold),
                    ModuleNavData("10. 24h Alert Engine", "Milestones & exams reminder", Icons.Default.Notifications, Screen.Milestones.route, DangerRose),
                    ModuleNavData("11. Upskilling Wishlist", "When, why, cost & prerequisites", Icons.Default.RocketLaunch, Screen.Upskilling.route, PrimaryIndigoLight),
                    ModuleNavData("12. Career Matrix", "Feasibility 1-10 & risk scoring", Icons.Default.TrendingUp, Screen.CareerMatrix.route, SecondaryCyanLight),
                    ModuleNavData("13. Tabular Resume", "Clean page-aligned CV builder", Icons.Default.Description, Screen.ResumeBuilder.route, PrimaryIndigo),
                    ModuleNavData("⚙️ Customize Trackers", "Degrees, GATE papers & exams", Icons.Default.Tune, Screen.Customizer.route, AccentGold)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    modules.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            pair.forEach { mod ->
                                ModuleGridCard(
                                    data = mod,
                                    onClick = { onNavigate(mod.route) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

data class ModuleNavData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String,
    val color: Color
)

@Composable
fun ModuleGridCard(
    data: ModuleNavData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("module_nav_${data.title.replace(" ", "_").lowercase()}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(data.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = data.icon,
                    contentDescription = null,
                    tint = data.color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = data.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
