package com.example.ui.screens.gate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GateSubjectEntity
import com.example.data.model.GateTopicEntity
import com.example.ui.MainViewModel
import com.example.ui.components.AcademicTopAppBar
import com.example.ui.components.CircularProgressMeter
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DangerRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SuccessEmerald

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GateTrackerScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null,
    onNavigateToCustomizer: (() -> Unit)? = null
) {
    val gatePapers by viewModel.gatePapers.collectAsState()
    val selectedPaperId by viewModel.selectedGatePaperId.collectAsState()
    val allGateSubjects by viewModel.allGateSubjects.collectAsState()
    val allGateTopics by viewModel.allGateTopics.collectAsState()

    var showAddTopicDialog by remember { mutableStateOf(false) }
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showAddPaperDialog by remember { mutableStateOf(false) }
    var showEditPaperDialog by remember { mutableStateOf(false) }

    var editingSubject by remember { mutableStateOf<GateSubjectEntity?>(null) }
    var editingTopic by remember { mutableStateOf<GateTopicEntity?>(null) }
    var selectedSubjectForNewTopic by remember { mutableStateOf<GateSubjectEntity?>(null) }

    val activePaper = gatePapers.find { it.id == selectedPaperId } ?: gatePapers.firstOrNull()
    val activePaperId = activePaper?.id ?: ""

    // Subjects and topics for active paper
    val currentSubjects = allGateSubjects.filter { it.paperId == activePaperId }
    val currentTopics = allGateTopics.filter { it.paperId == activePaperId }

    val doneTopics = currentTopics.count { it.status == "DONE" }
    val totalTopics = currentTopics.size.coerceAtLeast(1)
    val progressPercent = (doneTopics * 100) / totalTopics

    // Workload Hours & Backlog calculation (matching Dual Syllabus feature)
    val totalHoursNeeded = currentTopics.sumOf { it.estimatedHours }
    val remainingHours = currentTopics.filter { it.status != "DONE" }.sumOf { it.estimatedHours }
    val hardTopicsCount = currentTopics.count { it.weightage == "HARD" && it.status != "DONE" }
    val weakTopics = currentTopics.filter { it.confidenceLevel < 60 || it.revisionCount < 2 }

    Scaffold(
        topBar = {
            AcademicTopAppBar(
                title = "Competitive Exam & GATE Tracker",
                onBack = onBack,
                onOpenDrawer = onOpenDrawer,
                actions = {
                    if (onNavigateToCustomizer != null) {
                        IconButton(
                            onClick = onNavigateToCustomizer,
                            modifier = Modifier.testTag("gate_customizer_top_btn")
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = "Customize All Papers", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    IconButton(
                        onClick = { showEditPaperDialog = true },
                        modifier = Modifier.testTag("edit_gate_paper_top_btn")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Paper", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        floatingActionButton = {
            if (activePaper != null && currentSubjects.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        selectedSubjectForNewTopic = currentSubjects.firstOrNull()
                        showAddTopicDialog = true
                    },
                    containerColor = PrimaryIndigo,
                    modifier = Modifier.testTag("add_gate_topic_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Topic", tint = Color.White)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("gate_tracker_scroll"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Paper Selector & Management Controls
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            gatePapers.forEach { paper ->
                                FilterChip(
                                    selected = paper.id == selectedPaperId,
                                    onClick = { viewModel.setGatePaper(paper.id) },
                                    label = { Text("GATE ${paper.paperCode}", fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.testTag("gate_paper_${paper.paperCode}")
                                )
                            }

                            OutlinedButton(
                                onClick = { showAddPaperDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.testTag("add_new_gate_paper_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Paper", fontSize = 11.sp)
                            }
                        }

                        if (activePaper != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { showEditPaperDialog = true },
                                    modifier = Modifier.size(32.dp).testTag("edit_paper_btn")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Paper", tint = PrimaryIndigo, modifier = Modifier.size(18.dp))
                                }
                                if (gatePapers.size > 1) {
                                    IconButton(
                                        onClick = { viewModel.deleteGatePaper(activePaper.id) },
                                        modifier = Modifier.size(32.dp).testTag("delete_paper_btn")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Paper", tint = DangerRose.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }

                    if (activePaper != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tracking: GATE ${activePaper.paperCode} • ${activePaper.paperName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Workload & Backlog Analytics Header (Matching Dual Syllabus Module)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "WORKLOAD & REVISION BACKLOG",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryIndigo,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${remainingHours}h Remaining",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Total Workload: ${totalHoursNeeded}h | $hardTopicsCount Hard Backlogs",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${currentSubjects.size} Subjects • $doneTopics of ${currentTopics.size} Sub-Topics Mastered",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        CircularProgressMeter(
                            progressPercent = progressPercent,
                            label = "Mastered",
                            color = PrimaryIndigo
                        )
                    }
                }
            }

            // Weak Topics Alert Banner
            if (weakTopics.isNotEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DangerRose.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = DangerRose, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "WEAK SUB-TOPIC ALERTS (${weakTopics.size})",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = DangerRose
                                )
                                Text(
                                    text = "Topics with confidence < 60% or revision < 2 rounds. Prioritize these during preparation.",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Subjects Section Header & Add Subject Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SUBJECTS & SUB-TOPICS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Master each subject with targeted sub-topic revision loops",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedButton(
                        onClick = { showAddSubjectDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("add_gate_subject_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Add Subject", fontSize = 12.sp)
                    }
                }
            }

            // List of Subjects with Sub-Topics (Matching Dual Syllabus Module Pattern)
            if (currentSubjects.isEmpty()) {
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
                                text = "No subjects added for ${activePaper?.paperCode ?: "this paper"} yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { showAddSubjectDialog = true },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Add First Subject")
                            }
                        }
                    }
                }
            } else {
                items(currentSubjects, key = { it.id }) { subject ->
                    val subTopics = currentTopics.filter {
                        it.subjectId == subject.id || (it.subjectId.isBlank() && it.subjectCategory.equals(subject.subjectName, ignoreCase = true))
                    }
                    val subDone = subTopics.count { it.status == "DONE" }
                    val subTotal = subTopics.size
                    val subHours = subTopics.sumOf { it.estimatedHours }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("gate_subject_card_${subject.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Subject Header Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (subject.subjectCode.isNotBlank()) "${subject.subjectCode}: ${subject.subjectName}" else subject.subjectName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${subject.weightageMarks} Marks • $subDone of $subTotal Topics Done • ${subHours}h Workload",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { editingSubject = subject },
                                        modifier = Modifier.size(32.dp).testTag("edit_gate_subject_${subject.id}")
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Subject", tint = PrimaryIndigo, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteGateSubject(subject.id) },
                                        modifier = Modifier.size(32.dp).testTag("delete_gate_subject_${subject.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Subject", tint = DangerRose.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))

                            // Sub-Topics List
                            if (subTopics.isEmpty()) {
                                Text(
                                    text = "No sub-topics added yet. Tap '+ Add Sub-Topic' below to populate this subject.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            } else {
                                subTopics.forEach { topic ->
                                    val isDone = topic.status == "DONE"

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                val nextStatus = when (topic.status) {
                                                    "TODO" -> "DOING"
                                                    "DOING" -> "REVISING"
                                                    "REVISING" -> "DONE"
                                                    else -> "TODO"
                                                }
                                                viewModel.updateGateTopicStatus(topic.id, nextStatus)
                                            }
                                            .padding(vertical = 6.dp)
                                            .testTag("gate_topic_row_${topic.id}"),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = topic.topicName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (isDone) FontWeight.Normal else FontWeight.Medium,
                                                textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
                                                color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface
                                            )
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = "Est: ${topic.estimatedHours}h",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = "•",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = "${topic.revisionCount} Revs",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = PrimaryIndigo
                                                )
                                                Text(
                                                    text = "•",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = "${topic.confidenceLevel}% Conf",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 10.sp,
                                                    color = when {
                                                        topic.confidenceLevel >= 75 -> SuccessEmerald
                                                        topic.confidenceLevel >= 50 -> AccentGold
                                                        else -> DangerRose
                                                    }
                                                )
                                            }
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            StatusBadge(status = topic.weightage)
                                            StatusBadge(status = topic.status)

                                            // Quick +1 revision button
                                            IconButton(
                                                onClick = { viewModel.incrementGateRevision(topic.id) },
                                                modifier = Modifier.size(28.dp).testTag("quick_rev_${topic.id}")
                                            ) {
                                                Icon(
                                                    Icons.Default.Sync,
                                                    contentDescription = "+1 Revision Round",
                                                    tint = PrimaryIndigo,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            // Edit subtopic details
                                            IconButton(
                                                onClick = { editingTopic = topic },
                                                modifier = Modifier.size(28.dp).testTag("edit_topic_${topic.id}")
                                            ) {
                                                Icon(
                                                    Icons.Default.Edit,
                                                    contentDescription = "Edit Topic",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }

                                            // Delete subtopic
                                            IconButton(
                                                onClick = { viewModel.deleteGateTopic(topic.id) },
                                                modifier = Modifier.size(28.dp).testTag("delete_gate_topic_${topic.id}")
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Delete Topic",
                                                    tint = DangerRose.copy(alpha = 0.5f),
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Add Sub-Topic to this specific Subject button (Matching Dual Syllabus)
                            TextButton(
                                onClick = {
                                    selectedSubjectForNewTopic = subject
                                    showAddTopicDialog = true
                                },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .testTag("add_topic_to_subject_${subject.id}")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Sub-Topic to ${subject.subjectName}", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIALOGS ---

    // 1. Add Subject Dialog
    if (showAddSubjectDialog && activePaper != null) {
        var subjectName by remember { mutableStateOf("") }
        var subjectCode by remember { mutableStateOf("") }
        var marksStr by remember { mutableStateOf("10") }

        AlertDialog(
            onDismissRequest = { showAddSubjectDialog = false },
            title = { Text("Add Subject to GATE ${activePaper.paperCode}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter subject information (e.g. Operating Systems, Algorithms, Machine Learning):",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = subjectName,
                        onValueChange = { subjectName = it },
                        label = { Text("Subject Name (e.g. Theory of Computation)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_gate_subject_name_input")
                    )
                    OutlinedTextField(
                        value = subjectCode,
                        onValueChange = { subjectCode = it },
                        label = { Text("Subject Code (Optional, e.g. CS-TOC)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_gate_subject_code_input")
                    )
                    OutlinedTextField(
                        value = marksStr,
                        onValueChange = { marksStr = it },
                        label = { Text("Approx Marks Weightage in GATE (e.g. 8, 10, 12)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("add_gate_subject_marks_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val marks = marksStr.toIntOrNull() ?: 10
                        if (subjectName.isNotBlank()) {
                            viewModel.addGateSubject(
                                paperId = activePaper.id,
                                name = subjectName.trim(),
                                code = subjectCode.trim(),
                                marks = marks
                            )
                            showAddSubjectDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_gate_subject_btn")
                ) {
                    Text("Add Subject")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSubjectDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 2. Edit Subject Dialog
    if (editingSubject != null) {
        val subject = editingSubject!!
        var subjectName by remember { mutableStateOf(subject.subjectName) }
        var subjectCode by remember { mutableStateOf(subject.subjectCode) }
        var marksStr by remember { mutableStateOf(subject.weightageMarks.toString()) }

        AlertDialog(
            onDismissRequest = { editingSubject = null },
            title = { Text("Edit Subject") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = subjectName,
                        onValueChange = { subjectName = it },
                        label = { Text("Subject Name") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_gate_subject_name_input")
                    )
                    OutlinedTextField(
                        value = subjectCode,
                        onValueChange = { subjectCode = it },
                        label = { Text("Subject Code") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = marksStr,
                        onValueChange = { marksStr = it },
                        label = { Text("Approx Marks Weightage") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val marks = marksStr.toIntOrNull() ?: subject.weightageMarks
                        if (subjectName.isNotBlank()) {
                            viewModel.updateGateSubject(
                                subject.copy(
                                    subjectName = subjectName.trim(),
                                    subjectCode = subjectCode.trim(),
                                    weightageMarks = marks
                                )
                            )
                            editingSubject = null
                        }
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSubject = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 3. Add Sub-Topic Dialog (Matching Dual Syllabus Topic Creation)
    if (showAddTopicDialog && activePaper != null) {
        var topicTitle by remember { mutableStateOf("") }
        var chosenSubject by remember { mutableStateOf(selectedSubjectForNewTopic ?: currentSubjects.firstOrNull()) }
        var weightage by remember { mutableStateOf("MEDIUM") }
        var hoursStr by remember { mutableStateOf("4") }
        var confidence by remember { mutableStateOf(60f) }

        AlertDialog(
            onDismissRequest = { showAddTopicDialog = false },
            title = { Text("Add Sub-Topic to GATE ${activePaper.paperCode}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = topicTitle,
                        onValueChange = { topicTitle = it },
                        label = { Text("Sub-Topic Name (e.g. Regular Expressions & DFA)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_gate_topic_title_input")
                    )

                    Text("Assign to Subject:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        currentSubjects.forEach { sub ->
                            FilterChip(
                                selected = chosenSubject?.id == sub.id,
                                onClick = { chosenSubject = sub },
                                label = { Text(sub.subjectName, fontSize = 11.sp) }
                            )
                        }
                    }

                    Text("Topic Difficulty / Weightage:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("EASY", "MEDIUM", "HARD").forEach { w ->
                            FilterChip(
                                selected = weightage == w,
                                onClick = { weightage = w },
                                label = { Text(w, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = hoursStr,
                        onValueChange = { hoursStr = it },
                        label = { Text("Estimated Study Hours Needed") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Column {
                        Text(
                            text = "Initial Confidence Level: ${confidence.toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Slider(
                            value = confidence,
                            onValueChange = { confidence = it },
                            valueRange = 10f..100f,
                            steps = 9
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val hours = hoursStr.toIntOrNull() ?: 4
                        val targetSubject = chosenSubject ?: currentSubjects.firstOrNull()
                        if (topicTitle.isNotBlank() && targetSubject != null) {
                            viewModel.addGateTopic(
                                paperId = activePaper.id,
                                subjectId = targetSubject.id,
                                category = targetSubject.subjectName,
                                topicName = topicTitle.trim(),
                                weightage = weightage,
                                estimatedHours = hours,
                                confidenceLevel = confidence.toInt()
                            )
                            showAddTopicDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_gate_topic_btn")
                ) {
                    Text("Add Sub-Topic")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTopicDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 4. Edit Sub-Topic Dialog
    if (editingTopic != null) {
        val topic = editingTopic!!
        var topicTitle by remember { mutableStateOf(topic.topicName) }
        var weightage by remember { mutableStateOf(topic.weightage) }
        var hoursStr by remember { mutableStateOf(topic.estimatedHours.toString()) }
        var status by remember { mutableStateOf(topic.status) }
        var confidence by remember { mutableStateOf(topic.confidenceLevel.toFloat()) }
        var revisionRoundsStr by remember { mutableStateOf(topic.revisionCount.toString()) }

        AlertDialog(
            onDismissRequest = { editingTopic = null },
            title = { Text("Edit Sub-Topic") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = topicTitle,
                        onValueChange = { topicTitle = it },
                        label = { Text("Sub-Topic Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Weightage / Difficulty:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("EASY", "MEDIUM", "HARD").forEach { w ->
                            FilterChip(
                                selected = weightage == w,
                                onClick = { weightage = w },
                                label = { Text(w, fontSize = 11.sp) }
                            )
                        }
                    }

                    Text("Status:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("TODO", "DOING", "REVISING", "DONE").forEach { s ->
                            FilterChip(
                                selected = status == s,
                                onClick = { status = s },
                                label = { Text(s, fontSize = 11.sp) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = hoursStr,
                            onValueChange = { hoursStr = it },
                            label = { Text("Est Hours") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = revisionRoundsStr,
                            onValueChange = { revisionRoundsStr = it },
                            label = { Text("Revision Rounds") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Column {
                        Text(
                            text = "Confidence Level: ${confidence.toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Slider(
                            value = confidence,
                            onValueChange = { confidence = it },
                            valueRange = 10f..100f,
                            steps = 9
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val hours = hoursStr.toIntOrNull() ?: topic.estimatedHours
                        val revs = revisionRoundsStr.toIntOrNull() ?: topic.revisionCount
                        if (topicTitle.isNotBlank()) {
                            viewModel.updateGateTopic(
                                topic.copy(
                                    topicName = topicTitle.trim(),
                                    weightage = weightage,
                                    status = status,
                                    estimatedHours = hours,
                                    revisionCount = revs,
                                    confidenceLevel = confidence.toInt()
                                )
                            )
                            editingTopic = null
                        }
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingTopic = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 5. Add Paper Dialog
    if (showAddPaperDialog) {
        var paperCodeInput by remember { mutableStateOf("") }
        var paperNameInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddPaperDialog = false },
            title = { Text("Add Competitive Paper") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = paperCodeInput,
                        onValueChange = { paperCodeInput = it.uppercase() },
                        label = { Text("Paper Code (e.g. CS, DA, EC, ME)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_gate_paper_code_input")
                    )
                    OutlinedTextField(
                        value = paperNameInput,
                        onValueChange = { paperNameInput = it },
                        label = { Text("Full Paper Name (e.g. Mechanical Engineering)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_gate_paper_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (paperCodeInput.isNotBlank() && paperNameInput.isNotBlank()) {
                            viewModel.addGatePaper(paperCodeInput.trim(), paperNameInput.trim())
                            showAddPaperDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_gate_paper_btn")
                ) {
                    Text("Add Paper")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPaperDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 6. Edit Paper Dialog
    if (showEditPaperDialog && activePaper != null) {
        var paperCodeInput by remember { mutableStateOf(activePaper.paperCode) }
        var paperNameInput by remember { mutableStateOf(activePaper.paperName) }

        AlertDialog(
            onDismissRequest = { showEditPaperDialog = false },
            title = { Text("Edit Paper Details") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = paperCodeInput,
                        onValueChange = { paperCodeInput = it.uppercase() },
                        label = { Text("Paper Code") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_gate_paper_code_input")
                    )
                    OutlinedTextField(
                        value = paperNameInput,
                        onValueChange = { paperNameInput = it },
                        label = { Text("Full Paper Name") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_gate_paper_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (paperCodeInput.isNotBlank() && paperNameInput.isNotBlank()) {
                            viewModel.updateGatePaper(activePaper.id, paperCodeInput.trim(), paperNameInput.trim())
                            showEditPaperDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_edit_gate_paper_btn")
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditPaperDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
