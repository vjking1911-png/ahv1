package com.example.ui.screens.gate

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val allGateTopics by viewModel.allGateTopics.collectAsState()

    var showAddTopicDialog by remember { mutableStateOf(false) }
    var showAddPaperDialog by remember { mutableStateOf(false) }
    var showEditPaperDialog by remember { mutableStateOf(false) }

    val activePaper = gatePapers.find { it.id == selectedPaperId } ?: gatePapers.firstOrNull()
    val currentTopics = allGateTopics.filter { it.paperId == (activePaper?.id ?: "") }
    val doneTopics = currentTopics.count { it.status == "DONE" }
    val totalTopics = currentTopics.size.coerceAtLeast(1)
    val progressPercent = (doneTopics * 100) / totalTopics

    // Tinkered: Surface weak topics (confidence < 60% or revision < 2)
    val weakTopics = currentTopics.filter { it.confidenceLevel < 60 }

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
            if (activePaper != null) {
                FloatingActionButton(
                    onClick = { showAddTopicDialog = true },
                    containerColor = PrimaryIndigo,
                    modifier = Modifier.testTag("add_gate_topic_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add GATE Topic", tint = Color.White)
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
            // Paper Selector & Paper Customization Controls
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

            // Summary Card
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
                                text = "PAPER SYLLABUS STATUS",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryIndigo,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = activePaper?.paperName ?: "Select or Add a Paper",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$doneTopics of $totalTopics Topics Mastered • ${weakTopics.size} Weak Areas Detected",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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

            // Weak Topics Warning Banner
            if (weakTopics.isNotEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = DangerRose.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = DangerRose)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Weak Syllabus Areas Need Attention (${weakTopics.size})",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DangerRose
                                )
                                Text(
                                    text = "Confidence ratings under 60%. Use 'Tap to Revise' to trigger additional rounds.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Group topics by category
            if (currentTopics.isEmpty()) {
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
                                text = "No topics added for ${activePaper?.paperCode ?: "this paper"} yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { showAddTopicDialog = true },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Add First Topic")
                            }
                        }
                    }
                }
            } else {
                val categories = currentTopics.groupBy { it.subjectCategory }

                categories.forEach { (category, topicsList) ->
                    item {
                        Text(
                            text = category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.2.sp
                        )
                    }

                    items(topicsList, key = { it.id }) { topic ->
                        var localConfidence by remember(topic.confidenceLevel) { mutableStateOf(topic.confidenceLevel.toFloat()) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("gate_topic_${topic.id}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = topic.topicName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        val timeline = if (topic.startDate.isNotBlank()) "Started: ${topic.startDate} • Target: ${topic.endDate}" else "Timeline unassigned"
                                        Text(
                                            text = timeline,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        StatusBadge(status = topic.status)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = { viewModel.deleteGateTopic(topic.id) },
                                            modifier = Modifier.size(28.dp).testTag("delete_gate_topic_${topic.id}")
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete Topic", tint = DangerRose.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                                Spacer(modifier = Modifier.height(10.dp))

                                // Revision Loop Counter Button (+1)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Revision Loop:",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = PrimaryIndigo.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "${topic.revisionCount} Rounds",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryIndigo,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.incrementGateRevision(topic.id) },
                                        modifier = Modifier.testTag("revision_loop_button_${topic.id}"),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Loop, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("+1 Revise Loop", fontSize = 12.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Confidence Metric Slider (1-100%)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Confidence: ${localConfidence.toInt()}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = if (localConfidence < 60) DangerRose else SuccessEmerald
                                    )
                                    if (localConfidence < 60) {
                                        Text(
                                            text = "Weak Topic (Tap to Revise)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = DangerRose,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Slider(
                                    value = localConfidence,
                                    onValueChange = { localConfidence = it },
                                    onValueChangeFinished = {
                                        viewModel.updateGateConfidence(topic.id, localConfidence.toInt())
                                    },
                                    valueRange = 0f..100f,
                                    modifier = Modifier.testTag("gate_slider_${topic.id}")
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Paper Dialog
    if (showEditPaperDialog && activePaper != null) {
        var code by remember { mutableStateOf(activePaper.paperCode) }
        var name by remember { mutableStateOf(activePaper.paperName) }

        AlertDialog(
            onDismissRequest = { showEditPaperDialog = false },
            title = { Text("Edit GATE Paper") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Change code and title to the test paper you wish to track:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Paper Code (e.g. ME, EE, CE, XE, CS)") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_paper_code_input")
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Paper Name (e.g. Mechanical Engineering)") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_paper_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (code.isNotBlank() && name.isNotBlank()) {
                            viewModel.updateGatePaper(activePaper.id, code.uppercase().trim(), name.trim())
                            showEditPaperDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_edit_paper_btn")
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

    // Add New Paper Dialog
    if (showAddPaperDialog) {
        var code by remember { mutableStateOf("") }
        var name by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddPaperDialog = false },
            title = { Text("Add Custom GATE Paper") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Paper Code (e.g. EE, ME, CE, CH)") },
                        modifier = Modifier.fillMaxWidth().testTag("new_paper_code_input")
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Paper Full Name") },
                        modifier = Modifier.fillMaxWidth().testTag("new_paper_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (code.isNotBlank() && name.isNotBlank()) {
                            viewModel.addGatePaper(code.uppercase().trim(), name.trim())
                            showAddPaperDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_paper_btn")
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

    // Add Topic Dialog
    if (showAddTopicDialog && activePaper != null) {
        var cat by remember { mutableStateOf("") }
        var title by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddTopicDialog = false },
            title = { Text("Add Topic to ${activePaper.paperCode}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = cat,
                        onValueChange = { cat = it },
                        label = { Text("Category (e.g. Thermodynamics, Control Systems)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_gate_topic_cat")
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Topic Title") },
                        modifier = Modifier.fillMaxWidth().testTag("add_gate_topic_name")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && cat.isNotBlank()) {
                            viewModel.addGateTopic(activePaper.id, cat, title)
                            showAddTopicDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_gate_topic")
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
