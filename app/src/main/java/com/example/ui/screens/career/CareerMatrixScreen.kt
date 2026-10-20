package com.example.ui.screens.career

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DangerRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SuccessEmerald

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareerMatrixScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val allCareerOptions by viewModel.allCareerOptions.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var sortByFeasibility by remember { mutableStateOf(true) }

    val sortedOptions = if (sortByFeasibility) {
        allCareerOptions.sortedByDescending { it.feasibilityScore }
    } else {
        allCareerOptions.sortedBy {
            when (it.riskLevel) {
                "LOW" -> 1
                "MED" -> 2
                else -> 3
            }
        }
    }

    Scaffold(
        topBar = {
            AcademicTopAppBar(
                title = "Career Strategy Matrix",
                onBack = onBack,
                onOpenDrawer = onOpenDrawer
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryIndigo,
                modifier = Modifier.testTag("add_career_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Career Path", tint = Color.White)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("career_matrix_scroll"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sort Toggle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RANKING & FEASIBILITY MATRIX",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = sortByFeasibility,
                            onClick = { sortByFeasibility = true },
                            label = { Text("By Feasibility", fontSize = 11.sp) },
                            modifier = Modifier.testTag("sort_feasibility")
                        )
                        FilterChip(
                            selected = !sortByFeasibility,
                            onClick = { sortByFeasibility = false },
                            label = { Text("By Risk", fontSize = 11.sp) },
                            modifier = Modifier.testTag("sort_risk")
                        )
                    }
                }
            }

            if (sortedOptions.isEmpty()) {
                item {
                    Text(
                        text = "No career strategy pathways mapped yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(sortedOptions, key = { it.id }) { option ->
                    var sliderScore by remember(option.feasibilityScore) { mutableStateOf(option.feasibilityScore.toFloat()) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("career_card_${option.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = option.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Target: ${option.targetRole} • ${option.salaryRange}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    StatusBadge(status = "${option.riskLevel} RISK")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { viewModel.deleteCareerOption(option.id) },
                                        modifier = Modifier.size(28.dp).testTag("delete_career_${option.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRose.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Feasibility Index Slider (1 to 10) (Tinkered Feature)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Feasibility Index: ${sliderScore.toInt()} / 10",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (sliderScore >= 7) SuccessEmerald else AccentGold
                                )
                                Text(
                                    text = if (sliderScore >= 8) "High Viability" else if (sliderScore >= 5) "Moderate Viability" else "Moonshot",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Slider(
                                value = sliderScore,
                                onValueChange = { sliderScore = it },
                                onValueChangeFinished = {
                                    viewModel.updateCareerFeasibility(option.id, sliderScore.toInt())
                                },
                                valueRange = 1f..10f,
                                steps = 8,
                                modifier = Modifier.testTag("career_slider_${option.id}")
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Concrete Action Steps to unlock
                            Text(
                                text = "Action Steps Required to Unlock:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryIndigo
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = option.actionStepsText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var role by remember { mutableStateOf("") }
        var salary by remember { mutableStateOf("") }
        var risk by remember { mutableStateOf("LOW") }
        var feasibility by remember { mutableStateOf(8) }
        var steps by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Map Career Strategy Option") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Career Pathway Title") },
                        modifier = Modifier.fillMaxWidth().testTag("add_career_title")
                    )
                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        label = { Text("Target Role / Position") },
                        modifier = Modifier.fillMaxWidth().testTag("add_career_role")
                    )
                    OutlinedTextField(
                        value = salary,
                        onValueChange = { salary = it },
                        label = { Text("Compensation / Fellowship Band") },
                        modifier = Modifier.fillMaxWidth().testTag("add_career_salary")
                    )
                    OutlinedTextField(
                        value = steps,
                        onValueChange = { steps = it },
                        label = { Text("Manual Action Steps Required") },
                        modifier = Modifier.fillMaxWidth().testTag("add_career_steps")
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("LOW", "MED", "HIGH").forEach { r ->
                            FilterChip(
                                selected = risk == r,
                                onClick = { risk = r },
                                label = { Text("$r Risk") }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && steps.isNotBlank()) {
                            viewModel.addCareerOption(title, feasibility, risk, steps, role, salary)
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_career")
                ) {
                    Text("Add Pathway")
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
