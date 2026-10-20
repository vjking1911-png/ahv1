package com.example.ui.screens.results

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun ResultsFormulaScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val degreeProfiles by viewModel.degreeProfiles.collectAsState()
    val selectedProfileId by viewModel.selectedDegreeProfileId.collectAsState()

    val sandboxFormula by viewModel.sandboxFormula.collectAsState()
    val sandboxCgpaInput by viewModel.sandboxCgpaInput.collectAsState()
    val sandboxResult by viewModel.sandboxResult.collectAsState()

    val activeProfile = degreeProfiles.find { it.id == selectedProfileId } ?: degreeProfiles.firstOrNull()

    var customFormulaInput by remember(activeProfile) {
        mutableStateOf(activeProfile?.calculationFormulaString ?: "(CGPA * 9.5)")
    }

    var formulaSaveSuccess by remember { mutableStateOf(false) }
    var showEditDegreeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AcademicTopAppBar(
                title = "Results & Formula Engine",
                onBack = onBack,
                onOpenDrawer = onOpenDrawer
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("results_formula_scroll"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Degree Profiles Selector & Rename
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        degreeProfiles.forEach { profile ->
                            FilterChip(
                                selected = profile.id == selectedProfileId,
                                onClick = {
                                    viewModel.setDegreeProfile(profile.id)
                                    customFormulaInput = profile.calculationFormulaString
                                },
                                label = { Text(profile.degreeName, maxLines = 1) },
                                modifier = Modifier.testTag("degree_chip_${profile.id}")
                            )
                        }
                    }

                    IconButton(
                        onClick = { showEditDegreeDialog = true },
                        modifier = Modifier.testTag("edit_degree_btn_results")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Degree Name", tint = PrimaryIndigo)
                    }
                }
            }

            // Results Summary
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Cumulative CGPA",
                        value = if (selectedProfileId == "DEG_1") "9.20" else "8.80",
                        subtitle = "Overall Academic Standing",
                        accentColor = PrimaryIndigo,
                        modifier = Modifier.weight(1f)
                    )
                    val officialScore = if (selectedProfileId == "DEG_1") "87.40%" else "88.00%"
                    MetricCard(
                        title = "Formula Score",
                        value = officialScore,
                        subtitle = activeProfile?.calculationFormulaString ?: "(CGPA * 9.5)",
                        accentColor = SuccessEmerald,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Formula Sandbox Input Box (Tinkered Feature)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("formula_sandbox_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Calculate, contentDescription = null, tint = AccentGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "DYNAMIC FORMULA SANDBOX",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AccentGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Offline Tokenizing Evaluation Engine",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Test expressions with 'CGPA' variable in real-time. Evaluates offline with safe lexical parsing:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = sandboxFormula,
                            onValueChange = { viewModel.setSandboxFormula(it) },
                            label = { Text("Formula Expression (e.g. (CGPA * 9.5) or (CGPA * 10) + 5)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sandbox_formula_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = sandboxCgpaInput,
                            onValueChange = { viewModel.setSandboxCgpaInput(it) },
                            label = { Text("Simulate CGPA Value (e.g. 9.25)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sandbox_cgpa_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Sandbox Result Banner
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = PrimaryIndigo.copy(alpha = 0.12f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "EVALUATION RESULT",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryIndigo,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                when {
                                    sandboxResult.isSuccess -> {
                                        Text(
                                            text = "${String.format("%.2f", sandboxResult.getOrNull())}% Equivalent",
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = PrimaryIndigo
                                        )
                                        Text(
                                            text = "Expression parsed cleanly: $sandboxFormula with CGPA=$sandboxCgpaInput",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    else -> {
                                        Text(
                                            text = "Parsing error: ${sandboxResult.exceptionOrNull()?.message}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick presets
                        Text(
                            text = "Preset Academic Formulas:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("(CGPA * 9.5)", "(CGPA * 10)", "(CGPA * 7.1) + 12", "(CGPA * 10) + 5").forEach { preset ->
                                SuggestionChip(
                                    onClick = { viewModel.setSandboxFormula(preset) },
                                    label = { Text(preset, fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Save as degree official formula button
                        Button(
                            onClick = {
                                if (activeProfile != null) {
                                    viewModel.saveDegreeFormula(activeProfile.id, sandboxFormula)
                                    formulaSaveSuccess = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_formula_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Set as Official Formula for ${activeProfile?.degreeName}")
                        }

                        if (formulaSaveSuccess) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Formula updated successfully in local SQLite database!",
                                style = MaterialTheme.typography.bodySmall,
                                color = SuccessEmerald
                            )
                        }
                    }
                }
            }

            // Semester breakdown
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "SEMESTER GRADE BREAKDOWN",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryIndigo
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val semGrades = if (selectedProfileId == "DEG_1") {
                            listOf(Pair(1, 9.1), Pair(2, 9.3))
                        } else {
                            listOf(Pair(1, 8.8))
                        }

                        semGrades.forEach { (semNum, sgpa) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Semester $semNum SGPA",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "$sgpa / 10.0",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryIndigo
                                )
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        }
                    }
                }
            }
        }
    }

    if (showEditDegreeDialog && activeProfile != null) {
        var nameInput by remember { mutableStateOf(activeProfile.degreeName) }
        var instInput by remember { mutableStateOf(activeProfile.institutionName) }

        AlertDialog(
            onDismissRequest = { showEditDegreeDialog = false },
            title = { Text("Rename Degree Track") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Customize this degree profile to reflect what you are pursuing:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Degree Title (e.g. B.Sc Physics, MBBS, B.Com)") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_results_degree_name")
                    )
                    OutlinedTextField(
                        value = instInput,
                        onValueChange = { instInput = it },
                        label = { Text("Institution Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            viewModel.updateDegreeName(activeProfile.id, nameInput, instInput)
                            showEditDegreeDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_edit_degree_results")
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDegreeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
