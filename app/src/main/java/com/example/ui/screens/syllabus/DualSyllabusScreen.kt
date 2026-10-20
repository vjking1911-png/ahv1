package com.example.ui.screens.syllabus

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubjectEntity
import com.example.data.model.TopicEntity
import com.example.ui.MainViewModel
import com.example.ui.components.AcademicTopAppBar
import com.example.ui.components.CircularProgressMeter
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DangerRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DualSyllabusScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null,
    onNavigateToCustomizer: (() -> Unit)? = null
) {
    val selectedScope by viewModel.selectedCollegeScope.collectAsState()
    val semCollege1 by viewModel.semestersCollege1.collectAsState()
    val semCollege2 by viewModel.semestersCollege2.collectAsState()
    val allSubjects by viewModel.allSubjects.collectAsState()
    val allTopics by viewModel.allTopics.collectAsState()
    val degreeProfiles by viewModel.degreeProfiles.collectAsState()

    var showAddTopicDialog by remember { mutableStateOf(false) }
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showEditDegreeDialog by remember { mutableStateOf(false) }
    var editingSubject by remember { mutableStateOf<SubjectEntity?>(null) }
    var selectedSubjectForNewTopic by remember { mutableStateOf<SubjectEntity?>(null) }

    val deg1Profile = degreeProfiles.find { it.id == "DEG_1" }
    val deg2Profile = degreeProfiles.find { it.id == "DEG_2" }
    val deg1Name = deg1Profile?.degreeName ?: "Degree 1: B.Tech CSE"
    val deg2Name = deg2Profile?.degreeName ?: "Degree 2: BS Data Science"

    val currentSemesters = if (selectedScope == "COLLEGE_1") semCollege1 else semCollege2
    val currentSemesterIds = currentSemesters.map { it.id }.toSet()
    val currentSubjects = allSubjects.filter { it.semesterId in currentSemesterIds }
    val currentSubjectIds = currentSubjects.map { it.id }.toSet()
    val currentTopics = allTopics.filter { it.subjectId in currentSubjectIds }

    val doneTopics = currentTopics.count { it.status == "DONE" }
    val totalTopics = currentTopics.size.coerceAtLeast(1)
    val progressPercent = (doneTopics * 100) / totalTopics

    // Workload Hours & Backlog calculation
    val totalHoursNeeded = currentTopics.sumOf { it.estimatedHours }
    val remainingHours = currentTopics.filter { it.status != "DONE" }.sumOf { it.estimatedHours }
    val hardTopicsCount = currentTopics.count { it.weightage == "HARD" && it.status != "DONE" }

    Scaffold(
        topBar = {
            AcademicTopAppBar(
                title = "Dual College Syllabus",
                onBack = onBack,
                onOpenDrawer = onOpenDrawer,
                actions = {
                    if (onNavigateToCustomizer != null) {
                        IconButton(
                            onClick = onNavigateToCustomizer,
                            modifier = Modifier.testTag("dual_customizer_top_btn")
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = "Customize All Degree Tracks", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    IconButton(
                        onClick = { showEditDegreeDialog = true },
                        modifier = Modifier.testTag("edit_degree_names_btn")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Degree Track Name", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        floatingActionButton = {
            if (currentSubjects.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        selectedSubjectForNewTopic = currentSubjects.firstOrNull()
                        showAddTopicDialog = true
                    },
                    modifier = Modifier.testTag("add_syllabus_topic_fab"),
                    containerColor = PrimaryIndigo
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Topic", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("dual_syllabus_scroll"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dynamic Degree Pathway Tabs
            item {
                Column {
                    TabRow(
                        selectedTabIndex = if (selectedScope == "COLLEGE_1") 0 else 1,
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        Tab(
                            selected = selectedScope == "COLLEGE_1",
                            onClick = { viewModel.setCollegeScope("COLLEGE_1") },
                            text = { Text(deg1Name, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 12.sp) },
                            modifier = Modifier.testTag("tab_college_1")
                        )
                        Tab(
                            selected = selectedScope == "COLLEGE_2",
                            onClick = { viewModel.setCollegeScope("COLLEGE_2") },
                            text = { Text(deg2Name, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 12.sp) },
                            modifier = Modifier.testTag("tab_college_2")
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { showEditDegreeDialog = true },
                            modifier = Modifier.testTag("rename_active_degree_btn")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Change Degree Name", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { showAddSubjectDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("add_custom_subject_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Add Subject", fontSize = 12.sp)
                        }
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
                                text = "WORKLOAD & BACKLOG",
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
                        }
                        CircularProgressMeter(
                            progressPercent = progressPercent,
                            label = "Completed",
                            color = if (selectedScope == "COLLEGE_1") PrimaryIndigo else SecondaryCyan
                        )
                    }
                }
            }

            // Subjects & Topics
            if (currentSubjects.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No subjects tracked for this degree yet.",
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
                    val topics = currentTopics.filter { it.subjectId == subject.id }
                    val subDone = topics.count { it.status == "DONE" }
                    val subTotal = topics.size.coerceAtLeast(1)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${subject.subjectCode}: ${subject.subjectName}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${subject.credits} Credits • $subDone of $subTotal Topics Done",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { editingSubject = subject },
                                        modifier = Modifier.size(32.dp).testTag("edit_subject_${subject.id}")
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Subject", tint = PrimaryIndigo, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteSubject(subject.id) },
                                        modifier = Modifier.size(32.dp).testTag("delete_subject_${subject.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Subject", tint = DangerRose.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))

                            topics.forEach { topic ->
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
                                            viewModel.updateTopicStatus(topic.id, nextStatus)
                                        }
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = topic.topicTitle,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (topic.status == "DONE") FontWeight.Normal else FontWeight.Medium,
                                            color = if (topic.status == "DONE") MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Est: ${topic.estimatedHours} hrs needed",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        StatusBadge(status = topic.weightage)
                                        StatusBadge(status = topic.status)
                                        IconButton(
                                            onClick = { viewModel.deleteTopic(topic.id) },
                                            modifier = Modifier.size(28.dp).testTag("delete_topic_${topic.id}")
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete Topic", tint = DangerRose.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }

                            TextButton(
                                onClick = {
                                    selectedSubjectForNewTopic = subject
                                    showAddTopicDialog = true
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Topic to ${subject.subjectCode}", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Degree Names Dialog
    if (showEditDegreeDialog) {
        val currentProfileId = if (selectedScope == "COLLEGE_1") "DEG_1" else "DEG_2"
        val activeProfile = if (selectedScope == "COLLEGE_1") deg1Profile else deg2Profile
        var nameInput by remember { mutableStateOf(activeProfile?.degreeName ?: "") }
        var instInput by remember { mutableStateOf(activeProfile?.institutionName ?: "") }

        AlertDialog(
            onDismissRequest = { showEditDegreeDialog = false },
            title = { Text("Rename Degree Track") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Customize this degree pathway to match your actual program:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Degree Name (e.g. B.Sc Physics, MBBS, B.Com)") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_degree_name_input")
                    )
                    OutlinedTextField(
                        value = instInput,
                        onValueChange = { instInput = it },
                        label = { Text("Institution / University Name") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_degree_inst_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            viewModel.updateDegreeName(currentProfileId, nameInput, instInput)
                            showEditDegreeDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_edit_degree_btn")
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDegreeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Subject Dialog
    if (showAddSubjectDialog) {
        var subName by remember { mutableStateOf("") }
        var subCode by remember { mutableStateOf("") }
        var creditsStr by remember { mutableStateOf("4") }

        AlertDialog(
            onDismissRequest = { showAddSubjectDialog = false },
            title = { Text("Add Subject to Track") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val activeSemId = currentSemesters.firstOrNull()?.id ?: "SEM_DEFAULT"
                    OutlinedTextField(
                        value = subName,
                        onValueChange = { subName = it },
                        label = { Text("Subject Name (e.g. Quantum Physics, Financial Accounting)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_subject_name_input")
                    )
                    OutlinedTextField(
                        value = subCode,
                        onValueChange = { subCode = it },
                        label = { Text("Subject Code (e.g. PHY201, ACC101)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_subject_code_input")
                    )
                    OutlinedTextField(
                        value = creditsStr,
                        onValueChange = { creditsStr = it },
                        label = { Text("Credits (e.g. 3, 4)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_subject_credits_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val credits = creditsStr.toIntOrNull() ?: 4
                        val semId = currentSemesters.firstOrNull()?.id
                        if (subName.isNotBlank() && semId != null) {
                            viewModel.addSubject(semId, subName, subCode.ifBlank { "SUB" }, credits)
                            showAddSubjectDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_subject_btn")
                ) {
                    Text("Create Subject")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSubjectDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Subject Dialog
    if (editingSubject != null) {
        val subject = editingSubject!!
        var subName by remember { mutableStateOf(subject.subjectName) }
        var subCode by remember { mutableStateOf(subject.subjectCode) }
        var creditsStr by remember { mutableStateOf(subject.credits.toString()) }

        AlertDialog(
            onDismissRequest = { editingSubject = null },
            title = { Text("Edit Subject Details") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = subName,
                        onValueChange = { subName = it },
                        label = { Text("Subject Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = subCode,
                        onValueChange = { subCode = it },
                        label = { Text("Subject Code") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = creditsStr,
                        onValueChange = { creditsStr = it },
                        label = { Text("Credits") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val credits = creditsStr.toIntOrNull() ?: subject.credits
                        if (subName.isNotBlank()) {
                            viewModel.updateSubject(subject.copy(subjectName = subName, subjectCode = subCode, credits = credits))
                            editingSubject = null
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSubject = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Topic Dialog
    if (showAddTopicDialog && selectedSubjectForNewTopic != null) {
        var topicName by remember { mutableStateOf("") }
        var weightage by remember { mutableStateOf("MEDIUM") }
        var hoursStr by remember { mutableStateOf("4") }

        AlertDialog(
            onDismissRequest = { showAddTopicDialog = false },
            title = { Text("Add Syllabus Topic") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Subject: ${selectedSubjectForNewTopic?.subjectName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = topicName,
                        onValueChange = { topicName = it },
                        label = { Text("Topic Title") },
                        modifier = Modifier.fillMaxWidth().testTag("add_topic_title_field")
                    )
                    OutlinedTextField(
                        value = hoursStr,
                        onValueChange = { hoursStr = it },
                        label = { Text("Estimated Hours Needed") },
                        modifier = Modifier.fillMaxWidth().testTag("add_topic_hours_field")
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("EASY", "MEDIUM", "HARD").forEach { w ->
                            FilterChip(
                                selected = weightage == w,
                                onClick = { weightage = w },
                                label = { Text(w) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val hours = hoursStr.toIntOrNull() ?: 4
                        if (topicName.isNotBlank()) {
                            viewModel.addTopic(selectedSubjectForNewTopic!!.id, topicName, weightage, hours)
                            showAddTopicDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_topic_button")
                ) {
                    Text("Add Topic")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTopicDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
