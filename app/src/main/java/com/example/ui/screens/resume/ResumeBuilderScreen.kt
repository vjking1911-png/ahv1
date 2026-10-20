package com.example.ui.screens.resume

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.ResumeProfileEntity
import com.example.engine.ResumePdfEngine
import com.example.ui.MainViewModel
import com.example.ui.components.AcademicTopAppBar
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DangerRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SuccessEmerald
import java.io.File

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

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Form & Photo Editor, 1: Tabular Preview

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var github by remember { mutableStateOf("") }
    var linkedin by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var projects by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf("") }

    // Download state dialog
    var downloadedPdfFile by remember { mutableStateOf<File?>(null) }
    var isGeneratingPdf by remember { mutableStateOf(false) }

    LaunchedEffect(resumeProfile) {
        resumeProfile?.let {
            fullName = it.fullName
            email = it.email
            phone = it.phone
            github = it.githubUrl
            linkedin = it.linkedinUrl
            summary = it.summary
            education = it.educationJson
            skills = it.skillsJson
            projects = it.projectsJson
            experience = it.experienceJson
            photoUri = it.photoUri
        }
    }

    // Photo picker launcher (zero-permission modern Android Photo Picker)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                // Copy locally into internal app files directory to persist permanently offline
                val localPhotoFile = File(context.filesDir, "resume_photo_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    localPhotoFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                photoUri = localPhotoFile.absolutePath

                // Auto-save the updated profile
                val updatedProfile = ResumeProfileEntity(
                    id = "DEFAULT_PROFILE",
                    fullName = fullName,
                    email = email,
                    phone = phone,
                    githubUrl = github,
                    linkedinUrl = linkedin,
                    summary = summary,
                    educationJson = education,
                    skillsJson = skills,
                    projectsJson = projects,
                    experienceJson = experience,
                    photoUri = photoUri
                )
                viewModel.saveResumeProfile(updatedProfile)
                Toast.makeText(context, "Profile photo added successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load photo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // PDF generation & download action
    val executePdfDownload: () -> Unit = {
        isGeneratingPdf = true
        try {
            val currentProfile = ResumeProfileEntity(
                id = "DEFAULT_PROFILE",
                fullName = fullName,
                email = email,
                phone = phone,
                githubUrl = github,
                linkedinUrl = linkedin,
                summary = summary,
                educationJson = education,
                skillsJson = skills,
                projectsJson = projects,
                experienceJson = experience,
                photoUri = photoUri
            )
            viewModel.saveResumeProfile(currentProfile)

            val photoBitmap = loadBitmapFromPathOrUri(context, photoUri)
            val generatedPdf = ResumePdfEngine.generateResumePdf(context, currentProfile, photoBitmap)
            val downloadUri = ResumePdfEngine.savePdfToDownloads(context, generatedPdf, fullName.ifBlank { "Alex_Rivera" })

            downloadedPdfFile = generatedPdf
            Toast.makeText(context, "Resume PDF downloaded to Downloads!", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error creating PDF: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            isGeneratingPdf = false
        }
    }

    Scaffold(
        topBar = {
            AcademicTopAppBar(
                title = "Resume Builder & PDF Engine",
                onBack = onBack,
                onOpenDrawer = onOpenDrawer,
                actions = {
                    IconButton(
                        onClick = executePdfDownload,
                        modifier = Modifier.testTag("appbar_download_pdf_btn")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Download PDF", tint = PrimaryIndigo)
                    }

                    IconButton(
                        onClick = {
                            val profile = ResumeProfileEntity(
                                id = "DEFAULT_PROFILE",
                                fullName = fullName,
                                email = email,
                                phone = phone,
                                githubUrl = github,
                                linkedinUrl = linkedin,
                                summary = summary,
                                educationJson = education,
                                skillsJson = skills,
                                projectsJson = projects,
                                experienceJson = experience,
                                photoUri = photoUri
                            )
                            viewModel.saveResumeProfile(profile)
                            Toast.makeText(context, "Resume Profile Saved Locally!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("save_resume_icon_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save Profile", tint = MaterialTheme.colorScheme.primary)
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
            // Mode Tabs: Form Inputs & Photo vs Clean Tabular Document Preview
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Form & Photo", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        },
                        modifier = Modifier.testTag("tab_resume_form")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("A4 Tabular Preview & PDF", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        },
                        modifier = Modifier.testTag("tab_resume_preview")
                    )
                }
            }

            if (selectedTab == 0) {
                // PHOTO ATTACHMENT CARD
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("card_resume_photo"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "CANDIDATE PROFILE PHOTO",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryIndigo
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Photo Circle Avatar Preview
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, PrimaryIndigo, CircleShape)
                                        .background(PrimaryIndigo.copy(alpha = 0.08f))
                                        .clickable {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (photoUri.isNotBlank()) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                                                .data(File(photoUri))
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "Candidate Profile Photo",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                Icons.Default.AddAPhoto,
                                                contentDescription = "Add Photo",
                                                tint = PrimaryIndigo,
                                                modifier = Modifier.size(28.dp)
                                            )
                                            Text("Photo", fontSize = 10.sp, color = PrimaryIndigo, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (photoUri.isNotBlank()) "Photo Attached" else "No Photo Selected",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Your image will appear in the top header of both the preview and downloaded PDF.",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                photoPickerLauncher.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            modifier = Modifier.testTag("btn_pick_resume_photo")
                                        ) {
                                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (photoUri.isNotBlank()) "Change Photo" else "Upload Photo", fontSize = 12.sp)
                                        }

                                        if (photoUri.isNotBlank()) {
                                            OutlinedButton(
                                                onClick = {
                                                    photoUri = ""
                                                    val updatedProfile = ResumeProfileEntity(
                                                        id = "DEFAULT_PROFILE",
                                                        fullName = fullName,
                                                        email = email,
                                                        phone = phone,
                                                        githubUrl = github,
                                                        linkedinUrl = linkedin,
                                                        summary = summary,
                                                        educationJson = education,
                                                        skillsJson = skills,
                                                        projectsJson = projects,
                                                        experienceJson = experience,
                                                        photoUri = ""
                                                    )
                                                    viewModel.saveResumeProfile(updatedProfile)
                                                    Toast.makeText(context, "Photo removed", Toast.LENGTH_SHORT).show()
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRose)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Remove", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Personal & Contact Info
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
                                label = { Text("Full Name *") },
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
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = github,
                                    onValueChange = { github = it },
                                    label = { Text("GitHub / Portfolio") },
                                    modifier = Modifier.weight(1f).testTag("resume_input_github")
                                )
                                OutlinedTextField(
                                    value = linkedin,
                                    onValueChange = { linkedin = it },
                                    label = { Text("LinkedIn URL") },
                                    modifier = Modifier.weight(1f).testTag("resume_input_linkedin")
                                )
                            }
                            OutlinedTextField(
                                value = summary,
                                onValueChange = { summary = it },
                                label = { Text("Professional / Academic Bio & Objective") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )
                        }
                    }
                }

                // Structural Arrays (Education, Skills, Projects, Experience)
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
                                placeholder = { Text("National Institute of Technology | B.Tech CSE (CGPA: 9.25) | 2024-2028\nIIT Madras | BS Data Science (GPA: 8.8) | 2024-2028") },
                                modifier = Modifier.fillMaxWidth().testTag("resume_input_education"),
                                minLines = 3
                            )
                            OutlinedTextField(
                                value = skills,
                                onValueChange = { skills = it },
                                label = { Text("Technical Skills (Languages, Systems, Frameworks)") },
                                placeholder = { Text("Languages: Kotlin, C++, Python, SQL\nCore: Distributed Systems, Operating Systems, Machine Learning") },
                                modifier = Modifier.fillMaxWidth().testTag("resume_input_skills"),
                                minLines = 2
                            )
                            OutlinedTextField(
                                value = projects,
                                onValueChange = { projects = it },
                                label = { Text("Technical Projects (Title: Description)") },
                                placeholder = { Text("Mini-Raft KV Store: Fault-tolerant consensus engine.\nNeural Compiler Pass: LLVM tensor lowering pass.") },
                                modifier = Modifier.fillMaxWidth().testTag("resume_input_projects"),
                                minLines = 3
                            )
                            OutlinedTextField(
                                value = experience,
                                onValueChange = { experience = it },
                                label = { Text("Research, Experience & Honors Entries") },
                                placeholder = { Text("Undergraduate Research Assistant - High Performance Computing Lab\nDepartment Academic Peer Mentor") },
                                modifier = Modifier.fillMaxWidth().testTag("resume_input_experience"),
                                minLines = 2
                            )
                        }
                    }
                }

                // Bottom Action Button in Form Mode
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val profile = ResumeProfileEntity(
                                    id = "DEFAULT_PROFILE",
                                    fullName = fullName,
                                    email = email,
                                    phone = phone,
                                    githubUrl = github,
                                    linkedinUrl = linkedin,
                                    summary = summary,
                                    educationJson = education,
                                    skillsJson = skills,
                                    projectsJson = projects,
                                    experienceJson = experience,
                                    photoUri = photoUri
                                )
                                viewModel.saveResumeProfile(profile)
                                Toast.makeText(context, "Saved Successfully!", Toast.LENGTH_SHORT).show()
                                selectedTab = 1 // Switch to preview
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("btn_save_and_preview")
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save & Preview A4")
                        }

                        Button(
                            onClick = executePdfDownload,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                            modifier = Modifier.weight(1f).testTag("btn_direct_download_pdf")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Download PDF")
                        }
                    }
                }
            } else {
                // PRODUCTION-READY TABULAR DOCUMENT PREVIEW & PDF DOWNLOAD
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Action Toolbar
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = PrimaryIndigo.copy(alpha = 0.08f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "A4 Page-Aligned Resume",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryIndigo
                                    )
                                    Text(
                                        text = if (photoUri.isNotBlank()) "Photo attached • Ready for export" else "Text only • Photo optional",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = executePdfDownload,
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                        modifier = Modifier.testTag("download_pdf_primary_btn")
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Download PDF", fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            val formattedCv = """
================================================================================
${fullName.ifBlank { "Alex Rivera" }}
$email | $phone | $github | $linkedin
--------------------------------------------------------------------------------
ACADEMIC PROFILE & OBJECTIVE:
$summary

--------------------------------------------------------------------------------
EDUCATION & ACADEMIC STANDING:
$education

--------------------------------------------------------------------------------
TECHNICAL SKILL ARRAYS:
$skills

--------------------------------------------------------------------------------
KEY TECHNICAL PROJECTS:
$projects

--------------------------------------------------------------------------------
EXPERIENCE & HONORS:
$experience
================================================================================
                                            """.trimIndent()
                                            clipboardManager.setText(AnnotatedString(formattedCv))
                                            Toast.makeText(context, "Exported Formatted Text to Clipboard!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                }
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
                                // Top Color Accent Bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .background(PrimaryIndigo, RoundedCornerShape(2.dp))
                                )

                                // Header Block with Candidate Photo & Contact Details
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (photoUri.isNotBlank()) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                                                .data(File(photoUri))
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "Resume Photo",
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(CircleShape)
                                                .border(2.dp, PrimaryIndigo, CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(14.dp))
                                    }

                                    Column(
                                        horizontalAlignment = if (photoUri.isNotBlank()) Alignment.Start else Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = fullName.ifBlank { "Alex Rivera" },
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            color = PrimaryIndigo
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${email.ifBlank { "alex.rivera@university.edu" }} • ${phone.ifBlank { "+1 (555) 019-2834" }}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "${github.ifBlank { "github.com/alexrivera" }} • ${linkedin.ifBlank { "linkedin.com/in/alexrivera" }}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

                                // Bio / Summary
                                ResumeTabularSection(
                                    title = "ACADEMIC PROFILE & OBJECTIVE",
                                    content = summary.ifBlank { "Dual-Degree Honors scholar with rigorous technical foundations in Systems Software, Operating Systems, and Machine Learning." }
                                )

                                // Tabular Section 1: Education
                                ResumeTabularSection(
                                    title = "EDUCATION & ACADEMIC STANDING",
                                    content = education.ifBlank { "National Institute of Technology | B.Tech CSE (CGPA: 9.25/10.0) | 2024-2028\nIIT Madras | BS Data Science (GPA: 8.8/10.0) | 2024-2028" }
                                )

                                // Tabular Section 2: Technical Skills
                                ResumeTabularSection(
                                    title = "TECHNICAL SKILL ARRAYS",
                                    content = skills.ifBlank { "Languages: Kotlin, C++, Python, SQL, Rust, Bash\nSystems: Linux Kernel Internals, Multithreading, Distributed Consensus (Raft)\nFrameworks: Jetpack Compose, Room SQLite, PyTorch, Ktor" }
                                )

                                // Tabular Section 3: Projects
                                ResumeTabularSection(
                                    title = "KEY TECHNICAL PROJECTS",
                                    content = projects.ifBlank { "Mini-Raft KV Store: Fault-tolerant consensus engine with leader election and log replication.\nNeural Compiler Pass: Custom LLVM optimization pass lowering tensor computations." }
                                )

                                // Tabular Section 4: Experience
                                ResumeTabularSection(
                                    title = "EXPERIENCE, RESEARCH & HONORS",
                                    content = experience.ifBlank { "Undergraduate Research Assistant - High Performance Computing Lab\nDepartment Academic Peer Mentor - Computer Science Department" }
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                                Text(
                                    text = "Academic Hub • Certified Offline Local Resume Document",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // PDF Download Success & Quick Actions Dialog
    if (downloadedPdfFile != null) {
        val pdfFile = downloadedPdfFile!!
        AlertDialog(
            onDismissRequest = { downloadedPdfFile = null },
            icon = {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessEmerald, modifier = Modifier.size(36.dp))
            },
            title = {
                Text("Resume PDF Downloaded!", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Your resume PDF has been generated and saved to your device Downloads folder:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "📄 ${pdfFile.name}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryIndigo,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    if (photoUri.isNotBlank()) {
                        Text(
                            text = "✓ Candidate photo included in PDF header",
                            style = MaterialTheme.typography.labelSmall,
                            color = SuccessEmerald,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            val openIntent = ResumePdfEngine.createViewPdfIntent(context, pdfFile)
                            context.startActivity(openIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "No PDF viewer app installed", Toast.LENGTH_SHORT).show()
                        }
                        downloadedPdfFile = null
                    },
                    modifier = Modifier.testTag("btn_open_downloaded_pdf")
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Open PDF")
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(
                        onClick = {
                            try {
                                val shareIntent = ResumePdfEngine.createSharePdfIntent(context, pdfFile)
                                context.startActivity(Intent.createChooser(shareIntent, "Share Resume PDF"))
                            } catch (e: Exception) {
                                Toast.makeText(context, "Could not open share sheet", Toast.LENGTH_SHORT).show()
                            }
                            downloadedPdfFile = null
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share")
                    }
                    TextButton(onClick = { downloadedPdfFile = null }) {
                        Text("Done")
                    }
                }
            }
        )
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
                    text = "• ${line.trim()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    }
}

/**
 * Loads a Bitmap from either an absolute file path or a content URI.
 */
private fun loadBitmapFromPathOrUri(context: Context, pathOrUri: String?): Bitmap? {
    if (pathOrUri.isNullOrBlank()) return null
    return try {
        val file = File(pathOrUri)
        if (file.exists() && file.isFile) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            val uri = Uri.parse(pathOrUri)
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        }
    } catch (e: Exception) {
        null
    }
}
