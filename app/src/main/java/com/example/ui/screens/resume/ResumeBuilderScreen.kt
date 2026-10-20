package com.example.ui.screens.resume

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ResumeProfileEntity
import com.example.ui.MainViewModel
import com.example.ui.components.AcademicTopAppBar
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SuccessEmerald

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeBuilderScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val resumeProfile by viewModel.resumeProfile.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedTab by remember { mutableStateOf(0) } // 0: Form Editor, 1: Tabular Preview

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var github by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var projects by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }

    LaunchedEffect(resumeProfile) {
        resumeProfile?.let {
            fullName = it.fullName
            email = it.email
            phone = it.phone
            github = it.githubUrl
            summary = it.summary
            education = it.educationJson
            skills = it.skillsJson
            projects = it.projectsJson
            experience = it.experienceJson
        }
    }

    Scaffold(
        topBar = {
            AcademicTopAppBar(
                title = "Local Tabular Resume Builder",
                onBack = onBack,
                onOpenDrawer = onOpenDrawer,
                actions = {
                    IconButton(
                        onClick = {
                            val profile = ResumeProfileEntity(
                                id = "DEFAULT_PROFILE",
                                fullName = fullName,
                                email = email,
                                phone = phone,
                                githubUrl = github,
                                summary = summary,
                                educationJson = education,
                                skillsJson = skills,
                                projectsJson = projects,
                                experienceJson = experience
                            )
                            viewModel.saveResumeProfile(profile)
                            Toast.makeText(context, "Resume Profile Saved Locally!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("save_resume_icon_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("resume_builder_scroll"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mode Tabs: Form Inputs vs Clean Tabular Document Preview
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Form Entry Parameters", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        modifier = Modifier.testTag("tab_resume_form")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Tabular Layout Preview", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        modifier = Modifier.testTag("tab_resume_preview")
                    )
                }
            }

            if (selectedTab == 0) {
                // Form Entry
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "PERSONAL & CONTACT PARAMETERS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryIndigo
                            )
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("Full Name") },
                                modifier = Modifier.fillMaxWidth().testTag("resume_input_name")
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Academic Email") },
                                    modifier = Modifier.weight(1f).testTag("resume_input_email")
                                )
                                OutlinedTextField(
                                    value = phone,
                                    onValueChange = { phone = it },
                                    label = { Text("Phone") },
                                    modifier = Modifier.weight(1f).testTag("resume_input_phone")
                                )
                            }
                            OutlinedTextField(
                                value = github,
                                onValueChange = { github = it },
                                label = { Text("GitHub / Portfolio URL") },
                                modifier = Modifier.fillMaxWidth().testTag("resume_input_github")
                            )
                            OutlinedTextField(
                                value = summary,
                                onValueChange = { summary = it },
                                label = { Text("Professional / Academic Bio") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "STRUCTURAL ARRAYS (EDUCATION & PROJECTS)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryIndigo
                            )
                            OutlinedTextField(
                                value = education,
                                onValueChange = { education = it },
                                label = { Text("Education Array (Degree | Institute | CGPA | Years)") },
                                modifier = Modifier.fillMaxWidth().testTag("resume_input_education"),
                                minLines = 3
                            )
                            OutlinedTextField(
                                value = skills,
                                onValueChange = { skills = it },
                                label = { Text("Technical Skills (Languages, Systems, Frameworks)") },
                                modifier = Modifier.fillMaxWidth().testTag("resume_input_skills"),
                                minLines = 2
                            )
                            OutlinedTextField(
                                value = projects,
                                onValueChange = { projects = it },
                                label = { Text("Technical Projects (Title: Description)") },
                                modifier = Modifier.fillMaxWidth().testTag("resume_input_projects"),
                                minLines = 3
                            )
                            OutlinedTextField(
                                value = experience,
                                onValueChange = { experience = it },
                                label = { Text("Research & Experience Entries") },
                                modifier = Modifier.fillMaxWidth().testTag("resume_input_experience"),
                                minLines = 2
                            )
                        }
                    }
                }
            } else {
                // Production-Ready Tabular Document Preview (Tinkered Feature)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CLEAN PAGE-ALIGNED TABULAR RESUME",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryIndigo
                            )

                            Button(
                                onClick = {
                                    val formattedCv = """
================================================================================
$fullName
$email | $phone | $github
--------------------------------------------------------------------------------
ACADEMIC SUMMARY:
$summary

--------------------------------------------------------------------------------
EDUCATION:
$education

--------------------------------------------------------------------------------
TECHNICAL SKILLS:
$skills

--------------------------------------------------------------------------------
PROJECTS & RESEARCH:
$projects

--------------------------------------------------------------------------------
EXPERIENCE & HONORS:
$experience
================================================================================
                                    """.trimIndent()
                                    clipboardManager.setText(AnnotatedString(formattedCv))
                                    Toast.makeText(context, "Exported Formatted Resume to Clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("copy_resume_button")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Export to Clipboard")
                            }
                        }

                        // Dynamic Tabular Paper Render
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp)),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Header Block
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = fullName.ifBlank { "Alex Rivera" },
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "$email • $phone • $github",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                HorizontalDivider(thickness = 2.dp, color = PrimaryIndigo)

                                // Bio / Summary
                                Text(
                                    text = summary.ifBlank { "Dual-Degree Honors scholar with rigorous technical foundations in Systems and Machine Learning." },
                                    style = MaterialTheme.typography.bodySmall,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )

                                // Tabular Section 1: Education
                                ResumeTabularSection(
                                    title = "EDUCATION & ACADEMIC STANDING",
                                    content = education.ifBlank { "National Institute of Technology | B.Tech CSE (CGPA: 9.25/10.0) | 2024-2028\nIIT Madras | BS Data Science (GPA: 8.8/10.0) | 2024-2028" }
                                )

                                // Tabular Section 2: Technical Skills
                                ResumeTabularSection(
                                    title = "TECHNICAL SKILL ARRAYS",
                                    content = skills.ifBlank { "Languages: Kotlin, C++, Python, SQL\nCore: Distributed Systems, Operating Systems, Machine Learning" }
                                )

                                // Tabular Section 3: Projects
                                ResumeTabularSection(
                                    title = "KEY TECHNICAL PROJECTS",
                                    content = projects.ifBlank { "Mini-Raft KV Store: Fault-tolerant consensus engine.\nNeural Compiler Pass: LLVM tensor lowering pass." }
                                )

                                // Tabular Section 4: Experience
                                ResumeTabularSection(
                                    title = "RESEARCH & HONORS",
                                    content = experience.ifBlank { "Undergraduate Research Assistant - High Performance Computing Lab\nDepartment Academic Mentor" }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResumeTabularSection(
    title: String,
    content: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            color = PrimaryIndigo.copy(alpha = 0.08f),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = PrimaryIndigo,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
        content.lines().forEach { line ->
            if (line.isNotBlank()) {
                Text(
                    text = "• $line",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    }
}
