package com.example.ui.screens.ese

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
import com.example.data.model.EsePaperEntity
import com.example.data.model.EseSubjectEntity
import com.example.data.model.EseTopicEntity
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
fun EseTrackerScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val esePapers by viewModel.esePapers.collectAsState()
    val selectedPaperId by viewModel.selectedEsePaperId.collectAsState()
    val allEseSubjects by viewModel.allEseSubjects.collectAsState()
    val allEseTopics by viewModel.allEseTopics.collectAsState()

    var showAddTopicDialog by remember { mutableStateOf(false) }
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showAddPaperDialog by remember { mutableStateOf(false) }
    var showEditPaperDialog by remember { mutableStateOf(false) }

    var editingSubject by remember { mutableStateOf<EseSubjectEntity?>(null) }
    var editingTopic by remember { mutableStateOf<EseTopicEntity?>(null) }
    var selectedSubjectForNewTopic by remember { mutableStateOf<EseSubjectEntity?>(null) }
    var subjectToDelete by remember { mutableStateOf<EseSubjectEntity?>(null) }
    var paperToDelete by remember { mutableStateOf<EsePaperEntity?>(null) }

    val activePaper = esePapers.find { it.id == selectedPaperId } ?: esePapers.firstOrNull()
    val activePaperId = activePaper?.id ?: ""

    // Subjects and topics for active paper
    val currentSubjects = allEseSubjects.filter { it.paperId == activePaperId }
    val currentTopics = allEseTopics.filter { it.paperId == activePaperId }

    val doneTopics = currentTopics.count { it.status == "DONE" }
    val totalTopics = currentTopics.size.coerceAtLeast(1)
    val progressPercent = (doneTopics * 100) / totalTopics

    // Workload & Analytics calculations
    val totalHoursNeeded = currentTopics.sumOf { it.estimatedHours }
    val remainingHours = currentTopics.filter { it.status != "DONE" }.sumOf { it.estimatedHours }
    val hardTopicsCount = currentTopics.count { it.weightage == "HARD" && it.status != "DONE" }
    val totalPyqsSolved = currentTopics.sumOf { it.pyqSolvedCount }
    val weakTopics = currentTopics.filter { it.confidenceLevel < 60 || it.revisionCount < 2 || it.pyqSolvedCount < 15 }

    Scaffold(
        topBar = {
            AcademicTopAppBar(
                title = "UPSC ESE E&T Tracker",
                onBack = onBack,
                onOpenDrawer = onOpenDrawer,
                actions = {
                    if (activePaper != null) {
                        IconButton(
                            onClick = { showEditPaperDialog = true },
                            modifier = Modifier.testTag("edit_ese_paper_top_btn")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Paper", tint = MaterialTheme.colorScheme.primary)
                        }
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
                    modifier = Modifier.testTag("add_ese_topic_fab")
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
                .testTag("ese_tracker_scroll"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Paper Selector & Management Controls (Matching GATE Triple Paper pattern)
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
                            esePapers.forEach { paper ->
                                FilterChip(
                                    selected = paper.id == selectedPaperId,
                                    onClick = { viewModel.setEsePaper(paper.id) },
                                    label = { Text("ESE ${paper.paperCode}", fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.testTag("ese_paper_${paper.paperCode}")
                                )
                            }

                            OutlinedButton(
                                onClick = { showAddPaperDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.testTag("add_new_ese_paper_btn")
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
                                    modifier = Modifier.size(32.dp).testTag("edit_ese_paper_btn")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Paper", tint = PrimaryIndigo, modifier = Modifier.size(18.dp))
                                }
                                if (esePapers.size > 1) {
                                    IconButton(
                                        onClick = { paperToDelete = activePaper },
                                        modifier = Modifier.size(32.dp).testTag("delete_ese_paper_btn")
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
                            text = "Tracking: ESE ${activePaper.paperCode} • ${activePaper.paperName} (Target: ${activePaper.targetScore}/${activePaper.totalMarks} Marks)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Workload & Analytics Header
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
                                text = "WORKLOAD & PYQ BACKLOG",
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
                                text = "${currentSubjects.size} Subjects • $doneTopics of ${currentTopics.size} Topics Mastered • $totalPyqsSolved PYQs Solved",
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
                                    text = "Topics with confidence < 60%, revision < 2 rounds, or PYQs < 15. Prioritize these for UPSC ESE standard.",
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
                            text = "Electronics & Telecommunication syllabus hierarchy with PYQ loop",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedButton(
                        onClick = { showAddSubjectDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("add_ese_subject_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Add Subject", fontSize = 12.sp)
                    }
                }
            }

            // List of Subjects with Sub-Topics
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
                    val subPyqs = subTopics.sumOf { it.pyqSolvedCount }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ese_subject_card_${subject.id}"),
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
                                        text = "${subject.weightageMarks} Marks • $subDone of $subTotal Topics Done • ${subHours}h • $subPyqs PYQs",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { editingSubject = subject },
                                        modifier = Modifier.size(32.dp).testTag("edit_ese_subject_${subject.id}")
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Subject", tint = PrimaryIndigo, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = { subjectToDelete = subject },
                                        modifier = Modifier.size(32.dp).testTag("delete_ese_subject_${subject.id}")
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
                                                viewModel.updateEseTopicStatus(topic.id, nextStatus)
                                            }
                                            .padding(vertical = 6.dp)
                                            .testTag("ese_topic_row_${topic.id}"),
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
                                                Text(text = "•", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(
                                                    text = "${topic.revisionCount} Revs",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = PrimaryIndigo
                                                )
                                                Text(text = "•", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(
                                                    text = "${topic.pyqSolvedCount} PYQs",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AccentGold
                                                )
                                                Text(text = "•", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                                onClick = { viewModel.incrementEseRevision(topic.id) },
                                                modifier = Modifier.size(28.dp).testTag("quick_ese_rev_${topic.id}")
                                            ) {
                                                Icon(
                                                    Icons.Default.Sync,
                                                    contentDescription = "+1 Revision Round",
                                                    tint = PrimaryIndigo,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            // Quick +1 PYQ button
                                            IconButton(
                                                onClick = { viewModel.incrementEsePyq(topic.id) },
                                                modifier = Modifier.size(28.dp).testTag("quick_ese_pyq_${topic.id}")
                                            ) {
                                                Icon(
                                                    Icons.Default.PostAdd,
                                                    contentDescription = "+1 PYQ Solved",
                                                    tint = AccentGold,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            // Edit subtopic details
                                            IconButton(
                                                onClick = { editingTopic = topic },
                                                modifier = Modifier.size(28.dp).testTag("edit_ese_topic_${topic.id}")
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
                                                onClick = { viewModel.deleteEseTopic(topic.id) },
                                                modifier = Modifier.size(28.dp).testTag("delete_ese_topic_${topic.id}")
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

                            // Add Sub-Topic to this specific Subject button
                            TextButton(
                                onClick = {
                                    selectedSubjectForNewTopic = subject
                                    showAddTopicDialog = true
                                },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .testTag("add_topic_to_ese_subject_${subject.id}")
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
        var marksStr by remember { mutableStateOf("40") }

        AlertDialog(
            onDismissRequest = { showAddSubjectDialog = false },
            title = { Text("Add Subject to ESE ${activePaper.paperCode}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter subject information (e.g. Microwave Engineering, Network Analysis):",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = subjectName,
                        onValueChange = { subjectName = it },
                        label = { Text("Subject Name (e.g. Electromagnetics)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_ese_subject_name_input")
                    )
                    OutlinedTextField(
                        value = subjectCode,
                        onValueChange = { subjectCode = it },
                        label = { Text("Subject Code (Optional, e.g. ET-EMT)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_ese_subject_code_input")
                    )
                    OutlinedTextField(
                        value = marksStr,
                        onValueChange = { marksStr = it },
                        label = { Text("Approx Marks Weightage (e.g. 40, 45, 50)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("add_ese_subject_marks_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val marks = marksStr.toIntOrNull() ?: 40
                        if (subjectName.isNotBlank()) {
                            viewModel.addEseSubject(
                                paperId = activePaper.id,
                                name = subjectName.trim(),
                                code = subjectCode.trim(),
                                marks = marks
                            )
                            showAddSubjectDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_ese_subject_btn")
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
                        modifier = Modifier.fillMaxWidth().testTag("edit_ese_subject_name_input")
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
                            viewModel.updateEseSubject(
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

    // 3. Delete Subject Confirmation Dialog
    if (subjectToDelete != null) {
        val sub = subjectToDelete!!
        AlertDialog(
            onDismissRequest = { subjectToDelete = null },
            title = { Text("Delete Subject?") },
            text = { Text("Are you sure you want to delete '${sub.subjectName}' and all of its associated sub-topics?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEseSubject(sub.id)
                        subjectToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRose)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { subjectToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 4. Add Sub-Topic Dialog
    if (showAddTopicDialog && activePaper != null) {
        var topicTitle by remember { mutableStateOf("") }
        var chosenSubject by remember { mutableStateOf(selectedSubjectForNewTopic ?: currentSubjects.firstOrNull()) }
        var weightage by remember { mutableStateOf("MEDIUM") }
        var hoursStr by remember { mutableStateOf("5") }
        var confidence by remember { mutableStateOf(60f) }
        var pyqsStr by remember { mutableStateOf("20") }

        AlertDialog(
            onDismissRequest = { showAddTopicDialog = false },
            title = { Text("Add Sub-Topic to ESE ${activePaper.paperCode}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = topicTitle,
                        onValueChange = { topicTitle = it },
                        label = { Text("Sub-Topic Name (e.g. Smith Chart Matching)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_ese_topic_title_input")
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
                            value = pyqsStr,
                            onValueChange = { pyqsStr = it },
                            label = { Text("Initial PYQs") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

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
                        val hours = hoursStr.toIntOrNull() ?: 5
                        val pyqs = pyqsStr.toIntOrNull() ?: 0
                        val targetSubject = chosenSubject ?: currentSubjects.firstOrNull()
                        if (topicTitle.isNotBlank() && targetSubject != null) {
                            viewModel.addEseTopic(
                                paperId = activePaper.id,
                                subjectId = targetSubject.id,
                                category = targetSubject.subjectName,
                                topicName = topicTitle.trim(),
                                weightage = weightage,
                                estimatedHours = hours,
                                confidenceLevel = confidence.toInt(),
                                pyqs = pyqs
                            )
                            showAddTopicDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_ese_topic_btn")
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

    // 5. Edit Sub-Topic Dialog
    if (editingTopic != null) {
        val topic = editingTopic!!
        var topicTitle by remember { mutableStateOf(topic.topicName) }
        var weightage by remember { mutableStateOf(topic.weightage) }
        var hoursStr by remember { mutableStateOf(topic.estimatedHours.toString()) }
        var status by remember { mutableStateOf(topic.status) }
        var confidence by remember { mutableStateOf(topic.confidenceLevel.toFloat()) }
        var revisionRoundsStr by remember { mutableStateOf(topic.revisionCount.toString()) }
        var pyqsStr by remember { mutableStateOf(topic.pyqSolvedCount.toString()) }

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
                            label = { Text("Revisions") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = pyqsStr,
                            onValueChange = { pyqsStr = it },
                            label = { Text("PYQs Solved") },
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
                        val pyqs = pyqsStr.toIntOrNull() ?: topic.pyqSolvedCount
                        if (topicTitle.isNotBlank()) {
                            viewModel.updateEseTopic(
                                topic.copy(
                                    topicName = topicTitle.trim(),
                                    weightage = weightage,
                                    status = status,
                                    estimatedHours = hours,
                                    revisionCount = revs,
                                    pyqSolvedCount = pyqs,
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

    // 6. Add Paper Dialog
    if (showAddPaperDialog) {
        var paperCode by remember { mutableStateOf("") }
        var paperName by remember { mutableStateOf("") }
        var totalMarksStr by remember { mutableStateOf("300") }
        var targetScoreStr by remember { mutableStateOf("210") }
        var stageType by remember { mutableStateOf("PRELIMS_TECH") }

        AlertDialog(
            onDismissRequest = { showAddPaperDialog = false },
            title = { Text("Add ESE Paper / Stage") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = paperCode,
                        onValueChange = { paperCode = it },
                        label = { Text("Paper Code (e.g. ET-3, INTERVIEW)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = paperName,
                        onValueChange = { paperName = it },
                        label = { Text("Paper Title (e.g. Personality Test / Interview)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = totalMarksStr,
                            onValueChange = { totalMarksStr = it },
                            label = { Text("Total Marks") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = targetScoreStr,
                            onValueChange = { targetScoreStr = it },
                            label = { Text("Target Score") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text("Stage Type:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("PRELIMS_GS", "PRELIMS_TECH", "MAINS_TECH").forEach { st ->
                            FilterChip(
                                selected = stageType == st,
                                onClick = { stageType = st },
                                label = { Text(st.replace("_", " "), fontSize = 10.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val marks = totalMarksStr.toIntOrNull() ?: 300
                        val target = targetScoreStr.toIntOrNull() ?: 210
                        if (paperCode.isNotBlank() && paperName.isNotBlank()) {
                            viewModel.addEsePaper(
                                code = paperCode.trim(),
                                name = paperName.trim(),
                                marks = marks,
                                target = target,
                                stage = stageType
                            )
                            showAddPaperDialog = false
                        }
                    }
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

    // 7. Edit Paper Dialog
    if (showEditPaperDialog && activePaper != null) {
        var paperCode by remember { mutableStateOf(activePaper.paperCode) }
        var paperName by remember { mutableStateOf(activePaper.paperName) }
        var totalMarksStr by remember { mutableStateOf(activePaper.totalMarks.toString()) }
        var targetScoreStr by remember { mutableStateOf(activePaper.targetScore.toString()) }

        AlertDialog(
            onDismissRequest = { showEditPaperDialog = false },
            title = { Text("Edit ESE Paper Details") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = paperCode,
                        onValueChange = { paperCode = it },
                        label = { Text("Paper Code") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = paperName,
                        onValueChange = { paperName = it },
                        label = { Text("Paper Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = totalMarksStr,
                            onValueChange = { totalMarksStr = it },
                            label = { Text("Total Marks") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = targetScoreStr,
                            onValueChange = { targetScoreStr = it },
                            label = { Text("Target Score") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val marks = totalMarksStr.toIntOrNull() ?: activePaper.totalMarks
                        val target = targetScoreStr.toIntOrNull() ?: activePaper.targetScore
                        if (paperCode.isNotBlank() && paperName.isNotBlank()) {
                            viewModel.updateEsePaper(
                                activePaper.copy(
                                    paperCode = paperCode.trim(),
                                    paperName = paperName.trim(),
                                    totalMarks = marks,
                                    targetScore = target
                                )
                            )
                            showEditPaperDialog = false
                        }
                    }
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

    // 8. Delete Paper Confirmation Dialog
    if (paperToDelete != null) {
        val paper = paperToDelete!!
        AlertDialog(
            onDismissRequest = { paperToDelete = null },
            title = { Text("Delete Paper?") },
            text = { Text("Are you sure you want to delete 'ESE ${paper.paperCode}' and all its subjects and sub-topics?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEsePaper(paper.id)
                        paperToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRose)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { paperToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
