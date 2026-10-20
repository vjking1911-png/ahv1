package com.example.ui.screens.results

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DegreeProfileEntity
import com.example.data.model.SubjectExamEntity
import com.example.data.model.SubjectMarkEntity
import com.example.ui.MainViewModel
import com.example.ui.components.AcademicTopAppBar
import com.example.ui.components.MetricCard
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DangerRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SuccessEmerald

enum class ResultsTrackingTab {
    DEGREE_WISE,
    SEMESTER_WISE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsFormulaScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val degreeProfiles by viewModel.degreeProfiles.collectAsState()
    val selectedProfileId by viewModel.selectedDegreeProfileId.collectAsState()
    val activeProfile = degreeProfiles.find { it.id == selectedProfileId } ?: degreeProfiles.firstOrNull()

    val semesterGrades by viewModel.semesterGrades.collectAsState()
    val allSubjectMarks by viewModel.allSubjectMarks.collectAsState()
    val allSubjectExams by viewModel.allSubjectExams.collectAsState()
    val selectedSemesterNumber by viewModel.selectedSemesterNumber.collectAsState()

    val sandboxFormula by viewModel.sandboxFormula.collectAsState()
    val sandboxCgpaInput by viewModel.sandboxCgpaInput.collectAsState()
    val sandboxResult by viewModel.sandboxResult.collectAsState()

    var activeTab by remember { mutableStateOf(ResultsTrackingTab.DEGREE_WISE) }
    var showEditDegreeDialog by remember { mutableStateOf(false) }
    var showEditTargetCreditsDialog by remember { mutableStateOf(false) }
    var showAddSemesterDialog by remember { mutableStateOf(false) }
    var showAddSubjectDialog by remember { mutableStateOf(false) }

    var subjectToEdit by remember { mutableStateOf<SubjectMarkEntity?>(null) }
    var subjectForNewExam by remember { mutableStateOf<SubjectMarkEntity?>(null) }
    var examToEdit by remember { mutableStateOf<SubjectExamEntity?>(null) }
    var formulaSaveSuccess by remember { mutableStateOf(false) }

    // Derived Degree Metrics
    val totalDegreeCreditsEarned = allSubjectMarks.filter { it.gradePoint >= 4.0 }.sumOf { it.credits }
    val totalTargetCredits = activeProfile?.totalTargetCredits ?: 160
    val totalAllSubjectsCredits = allSubjectMarks.sumOf { it.credits }
    val cumulativeCgpa = if (totalAllSubjectsCredits > 0) {
        allSubjectMarks.sumOf { it.gradePoint * it.credits } / totalAllSubjectsCredits.toDouble()
    } else 0.0

    // Available Semesters for this Degree
    val availableSemesters = remember(semesterGrades, allSubjectMarks) {
        val gradeSems = semesterGrades.map { it.semesterNumber }
        val subjectSems = allSubjectMarks.map { it.semesterNumber }
        val combined = (gradeSems + subjectSems).distinct().sorted()
        if (combined.isEmpty()) listOf(1) else combined
    }

    LaunchedEffect(availableSemesters) {
        if (!availableSemesters.contains(selectedSemesterNumber) && availableSemesters.isNotEmpty()) {
            viewModel.setSelectedSemesterNumber(availableSemesters.first())
        }
    }

    // Selected Semester Metrics
    val subjectsInSelectedSem = allSubjectMarks.filter { it.semesterNumber == selectedSemesterNumber }
    val semTotalCredits = subjectsInSelectedSem.sumOf { it.credits }
    val semSgpa = if (semTotalCredits > 0) {
        subjectsInSelectedSem.sumOf { it.gradePoint * it.credits } / semTotalCredits.toDouble()
    } else 0.0
    val semAvgScore = if (subjectsInSelectedSem.isNotEmpty()) {
        subjectsInSelectedSem.map { it.marksObtained }.average().takeIf { !it.isNaN() } ?: 0.0
    } else 0.0

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
            // Mode Selector: Degree-Wise vs Semester-Wise
            item {
                TabRow(
                    selectedTabIndex = activeTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = activeTab == ResultsTrackingTab.DEGREE_WISE,
                        onClick = { activeTab = ResultsTrackingTab.DEGREE_WISE },
                        text = { Text("Degree-Wise Tracking", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("tab_degree_tracking")
                    )
                    Tab(
                        selected = activeTab == ResultsTrackingTab.SEMESTER_WISE,
                        onClick = { activeTab = ResultsTrackingTab.SEMESTER_WISE },
                        text = { Text("Semester-Wise Tracking", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("tab_semester_tracking")
                    )
                }
            }

            // Degree Profile Selector Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        degreeProfiles.forEach { profile ->
                            FilterChip(
                                selected = profile.id == selectedProfileId,
                                onClick = {
                                    viewModel.setDegreeProfile(profile.id)
                                    viewModel.setSandboxFormula(profile.calculationFormulaString)
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
                        Icon(Icons.Default.Edit, contentDescription = "Edit Degree Profile", tint = PrimaryIndigo)
                    }
                }
            }

            // TAB 1: DEGREE-WISE TRACKING
            if (activeTab == ResultsTrackingTab.DEGREE_WISE) {
                // Degree Credit Vault & Progress Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("degree_credits_vault_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = activeProfile?.degreeName ?: "Degree Program",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = activeProfile?.institutionName ?: "Academic Institution",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PrimaryIndigo.copy(alpha = 0.12f),
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text(
                                        text = "${String.format("%.1f", (totalDegreeCreditsEarned.toDouble() / totalTargetCredits) * 100)}% Complete",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryIndigo,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Degree Credit Vault: $totalDegreeCreditsEarned / $totalTargetCredits Credits Earned",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                TextButton(
                                    onClick = { showEditTargetCreditsDialog = true },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                ) {
                                    Text("Change Target", fontSize = 11.sp)
                                }
                            }

                            val progressFraction = (totalDegreeCreditsEarned.toFloat() / totalTargetCredits.toFloat()).coerceIn(0f, 1f)
                            LinearProgressIndicator(
                                progress = { progressFraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = PrimaryIndigo,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }

                // Cumulative CGPA & Formula Score
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "Cumulative CGPA",
                            value = "${String.format("%.2f", cumulativeCgpa)} / 10.0",
                            subtitle = "$totalDegreeCreditsEarned Credits Evaluated",
                            accentColor = PrimaryIndigo,
                            modifier = Modifier.weight(1f)
                        )
                        val formulaResult = sandboxResult.getOrNull()
                        val calculatedPercentage = if (cumulativeCgpa > 0) {
                            "${String.format("%.2f", cumulativeCgpa * 9.5)}%"
                        } else "0.00%"
                        MetricCard(
                            title = "Formula Score",
                            value = if (formulaResult != null) "${String.format("%.2f", formulaResult)}%" else calculatedPercentage,
                            subtitle = activeProfile?.calculationFormulaString ?: "(CGPA * 9.5)",
                            accentColor = SuccessEmerald,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Semester Breakdown Overview
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("degree_semesters_overview_card"),
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
                                        text = "SEMESTER-WISE DEGREE PROGRESS",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryIndigo
                                    )
                                    Text(
                                        text = "${availableSemesters.size} Semesters Recorded",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                TextButton(onClick = { showAddSemesterDialog = true }) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Semester", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            availableSemesters.forEach { semNum ->
                                val semSubjects = allSubjectMarks.filter { it.semesterNumber == semNum }
                                val semCreds = semSubjects.sumOf { it.credits }
                                val semGpa = if (semCreds > 0) {
                                    semSubjects.sumOf { it.gradePoint * it.credits } / semCreds.toDouble()
                                } else 0.0

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Semester $semNum",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "$semCreds Credits • ${semSubjects.size} Subjects Recorded",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${String.format("%.2f", semGpa)} SGPA",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = PrimaryIndigo
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        FilledTonalButton(
                                            onClick = {
                                                viewModel.setSelectedSemesterNumber(semNum)
                                                activeTab = ResultsTrackingTab.SEMESTER_WISE
                                            },
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Track", fontSize = 11.sp)
                                        }
                                    }
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            }
                        }
                    }
                }

                // Dynamic Formula Sandbox Card
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
                                        text = "Offline CGPA-to-Percentage Engine",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Formula uses 'CGPA' variable to calculate your official percentage equivalent:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = sandboxFormula,
                                onValueChange = { viewModel.setSandboxFormula(it) },
                                label = { Text("Formula Expression (e.g. (CGPA * 9.5) or (CGPA * 10))") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("sandbox_formula_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = sandboxCgpaInput,
                                onValueChange = { viewModel.setSandboxCgpaInput(it) },
                                label = { Text("Simulate CGPA Value") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("sandbox_cgpa_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

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
                                    if (sandboxResult.isSuccess) {
                                        Text(
                                            text = "${String.format("%.2f", sandboxResult.getOrNull())}% Equivalent",
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = PrimaryIndigo
                                        )
                                    } else {
                                        Text(
                                            text = "Parsing error: ${sandboxResult.exceptionOrNull()?.message}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Preset Formulas:", style = MaterialTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("(CGPA * 9.5)", "(CGPA * 10)", "(CGPA * 7.1) + 12", "(CGPA * 10) + 5").forEach { preset ->
                                    SuggestionChip(
                                        onClick = { viewModel.setSandboxFormula(preset) },
                                        label = { Text(preset, fontSize = 11.sp) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

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
                                    text = "Formula saved to local database!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SuccessEmerald
                                )
                            }
                        }
                    }
                }
            }

            // TAB 2: SEMESTER-WISE TRACKING
            if (activeTab == ResultsTrackingTab.SEMESTER_WISE) {
                // Semester Selector Horizontal Row
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SELECT SEMESTER",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            TextButton(
                                onClick = { showAddSemesterDialog = true },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Semester", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            availableSemesters.forEach { sem ->
                                FilterChip(
                                    selected = selectedSemesterNumber == sem,
                                    onClick = { viewModel.setSelectedSemesterNumber(sem) },
                                    label = { Text("Semester $sem", fontSize = 11.sp) },
                                    modifier = Modifier.testTag("filter_sem_chip_$sem")
                                )
                            }
                        }
                    }
                }

                // Semester Performance Summary
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("semester_summary_card"),
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
                                        text = "SEMESTER $selectedSemesterNumber SCORECARD",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryIndigo
                                    )
                                    Text(
                                        text = "${subjectsInSelectedSem.size} Subjects Enrolled",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (availableSemesters.size > 1) {
                                    IconButton(
                                        onClick = {
                                            if (activeProfile != null) {
                                                viewModel.deleteSemester(activeProfile.id, selectedSemesterNumber)
                                            }
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete Semester",
                                            tint = DangerRose.copy(alpha = 0.7f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MetricCard(
                                    title = "Semester SGPA",
                                    value = "${String.format("%.2f", semSgpa)} / 10.0",
                                    subtitle = "$semTotalCredits Total Credits",
                                    accentColor = PrimaryIndigo,
                                    modifier = Modifier.weight(1f)
                                )
                                MetricCard(
                                    title = "Average Score",
                                    value = "${String.format("%.1f", semAvgScore)}%",
                                    subtitle = "Weighted Across Exams",
                                    accentColor = SuccessEmerald,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Subjects & Exam Contributions Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "SUBJECTS & EXAM WEIGHTAGE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Configure exams and weightage for Semester $selectedSemesterNumber",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { showAddSubjectDialog = true },
                            modifier = Modifier.testTag("add_subject_btn"),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Subject", fontSize = 12.sp)
                        }
                    }
                }

                // List of Subjects with Exam Breakdown
                if (subjectsInSelectedSem.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No subjects added for Semester $selectedSemesterNumber yet",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Tap '+ Add Subject' above to add course subjects and exams.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(subjectsInSelectedSem, key = { it.id }) { subject ->
                        val exams = allSubjectExams.filter { it.subjectMarkId == subject.id }
                        val totalWeightage = exams.sumOf { it.weightagePercent }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("subject_card_${subject.id}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                // Subject Header Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = subject.subjectName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            if (subject.subjectCode.isNotBlank()) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = PrimaryIndigo.copy(alpha = 0.1f)
                                                ) {
                                                    Text(
                                                        text = subject.subjectCode,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = PrimaryIndigo,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "${subject.credits} Credits",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (subject.marksObtained >= 80) SuccessEmerald.copy(alpha = 0.15f) else PrimaryIndigo.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "${String.format("%.1f", subject.marksObtained)}% • ${String.format("%.1f", subject.gradePoint)} GP",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (subject.marksObtained >= 80) SuccessEmerald else PrimaryIndigo,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }

                                        Row(modifier = Modifier.padding(top = 4.dp)) {
                                            IconButton(
                                                onClick = { subjectToEdit = subject },
                                                modifier = Modifier.size(28.dp).testTag("edit_subject_${subject.id}")
                                            ) {
                                                Icon(
                                                    Icons.Default.Edit,
                                                    contentDescription = "Edit Subject",
                                                    tint = PrimaryIndigo,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = { viewModel.deleteSubjectMark(subject.id) },
                                                modifier = Modifier.size(28.dp).testTag("delete_subject_${subject.id}")
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Delete Subject",
                                                    tint = DangerRose.copy(alpha = 0.7f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Weightage Allocation Bar
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Exam Weightage Allocated: ${String.format("%.0f", totalWeightage)}% / 100%",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Medium,
                                        color = if (totalWeightage >= 100) SuccessEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    OutlinedButton(
                                        onClick = { subjectForNewExam = subject },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.testTag("add_exam_btn_${subject.id}")
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Add Exam", fontSize = 11.sp)
                                    }
                                }

                                LinearProgressIndicator(
                                    progress = { (totalWeightage.toFloat() / 100f).coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp),
                                    color = if (totalWeightage >= 100) SuccessEmerald else AccentGold,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // List of Exams in this Subject
                                if (exams.isEmpty()) {
                                    Text(
                                        text = "No exams logged yet. Tap '+ Add Exam' to enter exam scores and their percentage contribution.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        exams.forEach { exam ->
                                            val examPercentage = if (exam.maxMarks > 0) (exam.marksObtained / exam.maxMarks) * 100.0 else 0.0
                                            val contributedScore = examPercentage * (exam.weightagePercent / 100.0)

                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                                modifier = Modifier.fillMaxWidth().testTag("exam_item_${exam.id}")
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = exam.examName,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = "Contributes ${String.format("%.0f", exam.weightagePercent)}% of final score • Got ${String.format("%.1f", exam.marksObtained)}/${String.format("%.1f", exam.maxMarks)} (${String.format("%.1f", examPercentage)}%)",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontSize = 11.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                        Text(
                                                            text = "+${String.format("%.2f", contributedScore)}% to final subject score",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = PrimaryIndigo
                                                        )
                                                    }

                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        IconButton(
                                                            onClick = { examToEdit = exam },
                                                            modifier = Modifier.size(28.dp).testTag("edit_exam_score_${exam.id}")
                                                        ) {
                                                            Icon(
                                                                Icons.Default.Edit,
                                                                contentDescription = "Change Score",
                                                                tint = PrimaryIndigo,
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }
                                                        IconButton(
                                                            onClick = { viewModel.deleteSubjectExam(exam.id) },
                                                            modifier = Modifier.size(28.dp).testTag("delete_exam_${exam.id}")
                                                        ) {
                                                            Icon(
                                                                Icons.Default.Delete,
                                                                contentDescription = "Delete Exam",
                                                                tint = DangerRose.copy(alpha = 0.7f),
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIALOGS ---

    // 1. Rename Degree Profile Dialog
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
                        label = { Text("Degree Title (e.g. B.Tech CS, BS Data Science, MBBS)") },
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

    // 2. Edit Target Credits Dialog
    if (showEditTargetCreditsDialog && activeProfile != null) {
        var targetInput by remember { mutableStateOf(activeProfile.totalTargetCredits.toString()) }

        AlertDialog(
            onDismissRequest = { showEditTargetCreditsDialog = false },
            title = { Text("Degree Target Credits") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Set the total credits required to complete this degree (e.g. 160 for 4-year B.Tech, 120 for 3-year B.Sc):",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = targetInput,
                        onValueChange = { targetInput = it },
                        label = { Text("Total Target Credits") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = targetInput.toIntOrNull() ?: 160
                        if (target > 0) {
                            viewModel.updateDegreeTargetCredits(activeProfile.id, target)
                            showEditTargetCreditsDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditTargetCreditsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 3. Add Semester Dialog
    if (showAddSemesterDialog && activeProfile != null) {
        val nextSem = (availableSemesters.maxOrNull() ?: 0) + 1
        var semNumberInput by remember { mutableStateOf(nextSem.toString()) }

        AlertDialog(
            onDismissRequest = { showAddSemesterDialog = false },
            title = { Text("Add Semester") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter the semester number to begin tracking subjects and exams:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = semNumberInput,
                        onValueChange = { semNumberInput = it },
                        label = { Text("Semester Number (e.g. 1, 2, 3...)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val sem = semNumberInput.toIntOrNull() ?: nextSem
                        if (sem > 0) {
                            viewModel.addSemester(activeProfile.id, sem)
                            viewModel.setSelectedSemesterNumber(sem)
                            activeTab = ResultsTrackingTab.SEMESTER_WISE
                            showAddSemesterDialog = false
                        }
                    }
                ) {
                    Text("Add Semester")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSemesterDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 4. Add Subject Dialog
    if (showAddSubjectDialog && activeProfile != null) {
        var subjectName by remember { mutableStateOf("") }
        var subjectCode by remember { mutableStateOf("") }
        var creditsStr by remember { mutableStateOf("4") }

        AlertDialog(
            onDismissRequest = { showAddSubjectDialog = false },
            title = { Text("Add Subject to Semester $selectedSemesterNumber") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = subjectName,
                        onValueChange = { subjectName = it },
                        label = { Text("Subject Name (e.g. Operating Systems)") },
                        modifier = Modifier.fillMaxWidth().testTag("new_subject_name_input")
                    )
                    OutlinedTextField(
                        value = subjectCode,
                        onValueChange = { subjectCode = it },
                        label = { Text("Subject Code (Optional, e.g. CS301)") },
                        modifier = Modifier.fillMaxWidth().testTag("new_subject_code_input")
                    )
                    OutlinedTextField(
                        value = creditsStr,
                        onValueChange = { creditsStr = it },
                        label = { Text("Course Credits (e.g. 4, 3, 2)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("new_subject_credits_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val credits = creditsStr.toIntOrNull() ?: 4
                        if (subjectName.isNotBlank() && credits > 0) {
                            viewModel.addSubjectMark(
                                degreeProfileId = activeProfile.id,
                                semesterNumber = selectedSemesterNumber,
                                subjectName = subjectName.trim(),
                                subjectCode = subjectCode.trim(),
                                credits = credits
                            )
                            showAddSubjectDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_subject_button")
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

    // 5. Edit Subject Dialog
    if (subjectToEdit != null) {
        val subject = subjectToEdit!!
        var subjectName by remember { mutableStateOf(subject.subjectName) }
        var subjectCode by remember { mutableStateOf(subject.subjectCode) }
        var creditsStr by remember { mutableStateOf(subject.credits.toString()) }

        AlertDialog(
            onDismissRequest = { subjectToEdit = null },
            title = { Text("Edit Subject") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = subjectName,
                        onValueChange = { subjectName = it },
                        label = { Text("Subject Name") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_subject_name_input")
                    )
                    OutlinedTextField(
                        value = subjectCode,
                        onValueChange = { subjectCode = it },
                        label = { Text("Subject Code") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = creditsStr,
                        onValueChange = { creditsStr = it },
                        label = { Text("Credits") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val credits = creditsStr.toIntOrNull() ?: subject.credits
                        if (subjectName.isNotBlank() && credits > 0) {
                            viewModel.updateSubjectMark(
                                subjectId = subject.id,
                                subjectName = subjectName.trim(),
                                subjectCode = subjectCode.trim(),
                                credits = credits
                            )
                            subjectToEdit = null
                        }
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { subjectToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 6. Add Exam Dialog (Asks percentage contribution & marks gotten)
    if (subjectForNewExam != null) {
        val subject = subjectForNewExam!!
        val existingExams = allSubjectExams.filter { it.subjectMarkId == subject.id }
        val remainingWeightage = (100.0 - existingExams.sumOf { it.weightagePercent }).coerceAtLeast(0.0)

        var examName by remember { mutableStateOf("") }
        var weightageStr by remember { mutableStateOf(if (remainingWeightage > 0) String.format("%.0f", remainingWeightage) else "25") }
        var marksGottenStr by remember { mutableStateOf("") }
        var maxMarksStr by remember { mutableStateOf("50") }

        AlertDialog(
            onDismissRequest = { subjectForNewExam = null },
            title = { Text("Add Exam to ${subject.subjectName}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Specify the exam details and its percentage contribution to the final grade:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = examName,
                        onValueChange = { examName = it },
                        label = { Text("Exam Name (e.g. Mid-Term, End-Sem, Quiz 1, Lab Exam)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_exam_name_input")
                    )

                    // Percentage Contribution Question
                    OutlinedTextField(
                        value = weightageStr,
                        onValueChange = { weightageStr = it },
                        label = { Text("What % of final score does this exam contribute?") },
                        supportingText = { Text("Remaining unallocated weightage: ${String.format("%.0f", remainingWeightage)}%") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().testTag("add_exam_weightage_input")
                    )

                    // Marks Gotten Question
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = marksGottenStr,
                            onValueChange = { marksGottenStr = it },
                            label = { Text("Marks Gotten") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("add_exam_marks_input")
                        )
                        OutlinedTextField(
                            value = maxMarksStr,
                            onValueChange = { maxMarksStr = it },
                            label = { Text("Out of (Max)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("add_exam_max_marks_input")
                        )
                    }

                    // Live calculation preview
                    val w = weightageStr.toDoubleOrNull() ?: 0.0
                    val scored = marksGottenStr.toDoubleOrNull() ?: 0.0
                    val maxM = maxMarksStr.toDoubleOrNull() ?: 50.0
                    if (maxM > 0 && w > 0) {
                        val examPct = (scored / maxM) * 100.0
                        val contrib = examPct * (w / 100.0)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PrimaryIndigo.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Preview: ${String.format("%.1f", examPct)}% scored × $w% weightage = +${String.format("%.2f", contrib)}% towards subject score",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryIndigo,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val weightage = weightageStr.toDoubleOrNull() ?: 0.0
                        val marks = marksGottenStr.toDoubleOrNull() ?: 0.0
                        val maxMarks = maxMarksStr.toDoubleOrNull() ?: 50.0
                        if (examName.isNotBlank() && weightage > 0 && maxMarks > 0) {
                            viewModel.addSubjectExam(
                                subjectMarkId = subject.id,
                                examName = examName.trim(),
                                weightagePercent = weightage,
                                marksObtained = marks,
                                maxMarks = maxMarks
                            )
                            subjectForNewExam = null
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_exam_button")
                ) {
                    Text("Add Exam")
                }
            },
            dismissButton = {
                TextButton(onClick = { subjectForNewExam = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 7. Edit Exam & Change Score Dialog
    if (examToEdit != null) {
        val exam = examToEdit!!
        var examName by remember { mutableStateOf(exam.examName) }
        var weightageStr by remember { mutableStateOf(String.format("%.0f", exam.weightagePercent)) }
        var marksGottenStr by remember { mutableStateOf(String.format("%.1f", exam.marksObtained)) }
        var maxMarksStr by remember { mutableStateOf(String.format("%.1f", exam.maxMarks)) }

        AlertDialog(
            onDismissRequest = { examToEdit = null },
            title = { Text("Change Exam Score & Weightage") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = examName,
                        onValueChange = { examName = it },
                        label = { Text("Exam Name") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_exam_name_input")
                    )

                    OutlinedTextField(
                        value = weightageStr,
                        onValueChange = { weightageStr = it },
                        label = { Text("Percentage of final score contributed (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().testTag("edit_exam_weightage_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = marksGottenStr,
                            onValueChange = { marksGottenStr = it },
                            label = { Text("Marks Gotten") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("edit_exam_marks_input")
                        )
                        OutlinedTextField(
                            value = maxMarksStr,
                            onValueChange = { maxMarksStr = it },
                            label = { Text("Max Marks") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("edit_exam_max_input")
                        )
                    }

                    val w = weightageStr.toDoubleOrNull() ?: 0.0
                    val scored = marksGottenStr.toDoubleOrNull() ?: 0.0
                    val maxM = maxMarksStr.toDoubleOrNull() ?: 50.0
                    if (maxM > 0 && w > 0) {
                        val examPct = (scored / maxM) * 100.0
                        val contrib = examPct * (w / 100.0)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PrimaryIndigo.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Updated Score: ${String.format("%.1f", examPct)}% scored × $w% weight = +${String.format("%.2f", contrib)}% to subject",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryIndigo,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val weightage = weightageStr.toDoubleOrNull() ?: exam.weightagePercent
                        val marks = marksGottenStr.toDoubleOrNull() ?: exam.marksObtained
                        val maxMarks = maxMarksStr.toDoubleOrNull() ?: exam.maxMarks
                        if (examName.isNotBlank() && weightage > 0 && maxMarks > 0) {
                            viewModel.updateSubjectExam(
                                examId = exam.id,
                                examName = examName.trim(),
                                weightagePercent = weightage,
                                marksObtained = marks,
                                maxMarks = maxMarks
                            )
                            examToEdit = null
                        }
                    },
                    modifier = Modifier.testTag("confirm_edit_exam_button")
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { examToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
