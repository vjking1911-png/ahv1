package com.example.ui.screens.ese

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
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
fun EseTrackerScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val eseSections by viewModel.eseSections.collectAsState()
    val selectedStage by viewModel.selectedEseStage.collectAsState()
    val allEseTopics by viewModel.allEseTopics.collectAsState()

    val currentSections = eseSections.filter { it.stageType == selectedStage }
    val currentSectionIds = currentSections.map { it.id }.toSet()
    val currentTopics = allEseTopics.filter { it.sectionId in currentSectionIds }

    val totalPyqs = currentTopics.sumOf { it.pyqSolvedCount }
    val completedTopics = currentTopics.count { it.status == "DONE" }
    val totalTopics = currentTopics.size.coerceAtLeast(1)

    var editingSection by remember { mutableStateOf<com.example.data.model.EseSectionEntity?>(null) }

    Scaffold(
        topBar = {
            AcademicTopAppBar(
                title = "ESE UPSC Civil Service Tracker",
                onBack = onBack,
                onOpenDrawer = onOpenDrawer
            )
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
            // Stage Segmentation Tabs (Tinkered Feature)
            item {
                TabRow(
                    selectedTabIndex = when (selectedStage) {
                        "PRELIMS_GS" -> 0
                        "PRELIMS_TECH" -> 1
                        else -> 2
                    },
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedStage == "PRELIMS_GS",
                        onClick = { viewModel.setEseStage("PRELIMS_GS") },
                        text = { Text("Stage 1: GS", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        modifier = Modifier.testTag("tab_ese_prelims_gs")
                    )
                    Tab(
                        selected = selectedStage == "PRELIMS_TECH",
                        onClick = { viewModel.setEseStage("PRELIMS_TECH") },
                        text = { Text("Stage 1: Tech", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        modifier = Modifier.testTag("tab_ese_prelims_tech")
                    )
                    Tab(
                        selected = selectedStage == "MAINS_TECH",
                        onClick = { viewModel.setEseStage("MAINS_TECH") },
                        text = { Text("Stage 2: Mains", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        modifier = Modifier.testTag("tab_ese_mains_tech")
                    )
                }
            }

            // Metrics Summary
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Stage Progress",
                        value = "${(completedTopics * 100) / totalTopics}%",
                        subtitle = "$completedTopics of $totalTopics Topics Done",
                        accentColor = PrimaryIndigo,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Total PYQs Solved",
                        value = "$totalPyqs PYQs",
                        subtitle = "Previous Year Questions",
                        accentColor = AccentGold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Section papers & topics
            currentSections.forEach { section ->
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = section.paperTitle.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.1.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { editingSection = section },
                            modifier = Modifier.size(28.dp).testTag("edit_ese_section_${section.id}")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Section Title", tint = PrimaryIndigo, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                val topics = currentTopics.filter { it.sectionId == section.id }
                items(topics, key = { it.id }) { topic ->
                    val isDone = topic.status == "DONE"
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ese_topic_${topic.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isDone,
                                    onCheckedChange = { viewModel.toggleEseTopicStatus(topic) },
                                    modifier = Modifier.testTag("checkbox_ese_${topic.id}")
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = topic.topicName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (isDone) FontWeight.Normal else FontWeight.SemiBold,
                                        color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (topic.notesAttachedFlag) {
                                        Text(
                                            text = "✓ Revision notes attached",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SuccessEmerald
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))

                            // PYQs Solved Counter (+1 Counter button)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "PYQs Solved:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = AccentGold.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "${topic.pyqSolvedCount} Solved",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = AccentGold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                OutlinedButton(
                                    onClick = { viewModel.incrementEsePyq(topic.id) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("increment_pyq_${topic.id}"),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+1 PYQ", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
