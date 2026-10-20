package com.example.ui.screens.customizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GatePaperEntity
import com.example.ui.MainViewModel
import com.example.ui.components.AcademicTopAppBar
import com.example.ui.components.MetricCard
import com.example.ui.theme.*

data class AcademicPreset(
    val title: String,
    val iconEmoji: String,
    val subtitle: String,
    val deg1Name: String,
    val deg1Inst: String,
    val deg2Name: String,
    val deg2Inst: String,
    val papers: List<Pair<String, String>> // code to title
)

val ACADEMIC_PRESETS = listOf(
    AcademicPreset(
        title = "CS & AI / Data Science",
        iconEmoji = "💻",
        subtitle = "Computer Science + Machine Learning Focus",
        deg1Name = "B.Tech Computer Science & Engineering",
        deg1Inst = "Institute of Technology",
        deg2Name = "BS Data Science & Programming",
        deg2Inst = "National Science Institute",
        papers = listOf("CS" to "Computer Science & IT", "DA" to "Data Science & AI", "MA" to "Engineering Mathematics")
    ),
    AcademicPreset(
        title = "Mechanical & Mechatronics",
        iconEmoji = "⚙️",
        subtitle = "Core Mechanical + Robotics & Systems",
        deg1Name = "B.Tech Mechanical Engineering",
        deg1Inst = "College of Engineering",
        deg2Name = "Minor in Robotics & Mechatronics",
        deg2Inst = "Autonomous University",
        papers = listOf("ME" to "Mechanical Engineering", "XE" to "Engineering Sciences", "PI" to "Production & Industrial")
    ),
    AcademicPreset(
        title = "Electrical & Electronics",
        iconEmoji = "⚡",
        subtitle = "Power Systems, VLSI & Embedded Tech",
        deg1Name = "B.Tech Electrical & Electronics",
        deg1Inst = "University Faculty of Tech",
        deg2Name = "Minor in VLSI Design & Embedded Systems",
        deg2Inst = "Microelectronics Academy",
        papers = listOf("EE" to "Electrical Engineering", "EC" to "Electronics & Communication", "IN" to "Instrumentation Engg")
    ),
    AcademicPreset(
        title = "Civil & Infrastructure",
        iconEmoji = "🏗️",
        subtitle = "Structures, Environmental & Geotech",
        deg1Name = "B.Tech Civil Engineering",
        deg1Inst = "Engineering & Tech University",
        deg2Name = "Minor in GIS & Urban Planning",
        deg2Inst = "School of Planning & Architecture",
        papers = listOf("CE" to "Civil Engineering", "ES" to "Environmental Science", "AR" to "Architecture & Planning")
    ),
    AcademicPreset(
        title = "Commerce, MBA & Finance",
        iconEmoji = "📈",
        subtitle = "Business Analytics, CAT & CFA Prep",
        deg1Name = "B.Com (Honors) / BBA",
        deg1Inst = "School of Business Studies",
        deg2Name = "Diploma in Financial Analytics & CFA",
        deg2Inst = "Institute of Management",
        papers = listOf("CAT" to "Common Admission Test (Quant/DILR/VARC)", "GRE" to "GRE Quantitative & Verbal", "GMAT" to "Graduate Management Exam")
    ),
    AcademicPreset(
        title = "Pure Sciences & Physics",
        iconEmoji = "🔬",
        subtitle = "Theoretical Physics, Research & IIT-JAM",
        deg1Name = "B.Sc (Honors) Physics",
        deg1Inst = "Faculty of Pure Sciences",
        deg2Name = "Minor in Mathematics & Scientific Computing",
        deg2Inst = "Mathematical Institute",
        papers = listOf("PH" to "GATE Physics", "JAM" to "IIT-JAM Physical Sciences", "NET" to "CSIR-UGC NET Physical Sciences")
    ),
    AcademicPreset(
        title = "Biotech & Health Sciences",
        iconEmoji = "🧬",
        subtitle = "Biotechnology, Genetics & Life Sciences",
        deg1Name = "B.Tech Biotechnology",
        deg1Inst = "School of Biosciences",
        deg2Name = "Minor in Bioinformatics & Genomic Computing",
        deg2Inst = "Center for Computational Biology",
        papers = listOf("BT" to "GATE Biotechnology", "XL" to "GATE Life Sciences", "EY" to "Ecology & Evolution")
    ),
    AcademicPreset(
        title = "Law, Policy & Civil Service",
        iconEmoji = "⚖️",
        subtitle = "Public Policy, Legal Studies & UPSC",
        deg1Name = "B.A. (Honors) Political Science & Public Admin",
        deg1Inst = "School of Humanities & Law",
        deg2Name = "Diploma in Cyber Law & Intellectual Property",
        deg2Inst = "National Law University",
        papers = listOf("UPSC_1" to "GS Paper 1 (History, Polity & Econ)", "UPSC_2" to "CSAT Aptitude Paper", "OPT" to "Optional Discipline Paper")
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizerScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val degreeProfiles by viewModel.degreeProfiles.collectAsState()
    val gatePapers by viewModel.gatePapers.collectAsState()
    val eseSections by viewModel.eseSections.collectAsState()

    val deg1Profile = degreeProfiles.find { it.id == "DEG_1" }
    val deg2Profile = degreeProfiles.find { it.id == "DEG_2" }

    var deg1NameInput by remember(deg1Profile) { mutableStateOf(deg1Profile?.degreeName ?: "Degree 1") }
    var deg1InstInput by remember(deg1Profile) { mutableStateOf(deg1Profile?.institutionName ?: "") }

    var deg2NameInput by remember(deg2Profile) { mutableStateOf(deg2Profile?.degreeName ?: "Degree 2") }
    var deg2InstInput by remember(deg2Profile) { mutableStateOf(deg2Profile?.institutionName ?: "") }

    var degreeSaveSuccess by remember { mutableStateOf(false) }

    // Dialog state for adding new competitive exam paper
    var showAddPaperDialog by remember { mutableStateOf(false) }
    var newPaperCode by remember { mutableStateOf("") }
    var newPaperName by remember { mutableStateOf("") }

    // Dialog state for editing existing paper
    var editingPaper by remember { mutableStateOf<GatePaperEntity?>(null) }
    var editPaperCode by remember { mutableStateOf("") }
    var editPaperName by remember { mutableStateOf("") }

    // Confirmation for preset application
    var pendingPreset by remember { mutableStateOf<AcademicPreset?>(null) }

    // Editing ESE section
    var editingSection by remember { mutableStateOf<com.example.data.model.EseSectionEntity?>(null) }
    var editSectionTitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            AcademicTopAppBar(
                title = "Customize Trackers & Names",
                onBack = onBack,
                onOpenDrawer = onOpenDrawer
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("customizer_screen_scroll"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Hero Intro Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryIndigoDark)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(AccentGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = null, tint = AccentGold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Academic Track Customizer",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Tailor every degree, exam & tracker to your field",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PrimaryIndigoLight
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Not tracking Computer Science or standard GATE? You can change Degree 1 & 2 to any program (Engineering, Medical, Commerce, Law, Arts) and configure custom competitive exam papers (GATE, CAT, GRE, UPSC, etc.) below.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Quick One-Tap Field Presets
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ONE-TAP ACADEMIC PRESETS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.1.sp
                        )
                        Text(
                            text = "Tap to apply",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ACADEMIC_PRESETS.forEach { preset ->
                            Card(
                                modifier = Modifier
                                    .width(220.dp)
                                    .clickable { pendingPreset = preset }
                                    .testTag("preset_card_${preset.title.take(6).lowercase()}"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = preset.iconEmoji, fontSize = 22.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = preset.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = preset.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = PrimaryIndigo.copy(alpha = 0.1f)
                                    ) {
                                        Text(
                                            text = "${preset.papers.size} Exam Papers included",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 10.sp,
                                            color = PrimaryIndigo,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Degree 1 & Degree 2 Customizer
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.School, contentDescription = null, tint = PrimaryIndigo)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Dual College Degree Names",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Degree 1 Inputs
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "DEGREE 1 TRACK",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryIndigo
                                )
                                OutlinedTextField(
                                    value = deg1NameInput,
                                    onValueChange = { deg1NameInput = it; degreeSaveSuccess = false },
                                    label = { Text("Degree 1 Title (e.g. B.Tech Mechanical, B.Com Honors)") },
                                    modifier = Modifier.fillMaxWidth().testTag("customizer_deg1_name_input")
                                )
                                OutlinedTextField(
                                    value = deg1InstInput,
                                    onValueChange = { deg1InstInput = it; degreeSaveSuccess = false },
                                    label = { Text("Institution / University 1") },
                                    modifier = Modifier.fillMaxWidth().testTag("customizer_deg1_inst_input")
                                )
                            }
                        }

                        // Degree 2 Inputs
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "DEGREE 2 TRACK",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryCyan
                                )
                                OutlinedTextField(
                                    value = deg2NameInput,
                                    onValueChange = { deg2NameInput = it; degreeSaveSuccess = false },
                                    label = { Text("Degree 2 Title (e.g. Minor in Robotics, Online MBA, Diploma)") },
                                    modifier = Modifier.fillMaxWidth().testTag("customizer_deg2_name_input")
                                )
                                OutlinedTextField(
                                    value = deg2InstInput,
                                    onValueChange = { deg2InstInput = it; degreeSaveSuccess = false },
                                    label = { Text("Institution / University 2") },
                                    modifier = Modifier.fillMaxWidth().testTag("customizer_deg2_inst_input")
                                )
                            }
                        }

                        AnimatedVisibility(visible = degreeSaveSuccess) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SuccessEmerald.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessEmerald, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Degree names and institutions saved successfully!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SuccessEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (deg1NameInput.isNotBlank()) {
                                    viewModel.updateDegreeName("DEG_1", deg1NameInput, deg1InstInput)
                                }
                                if (deg2NameInput.isNotBlank()) {
                                    viewModel.updateDegreeName("DEG_2", deg2NameInput, deg2InstInput)
                                }
                                degreeSaveSuccess = true
                            },
                            modifier = Modifier.fillMaxWidth().testTag("save_custom_degrees_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Degree Track Names")
                        }
                    }
                }
            }

            // Competitive Exam Trackers & GATE Papers Customizer
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FilterFrames, contentDescription = null, tint = AccentGold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Competitive Exam Papers Tracker",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${gatePapers.size} active papers configured",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Button(
                                onClick = {
                                    newPaperCode = ""
                                    newPaperName = ""
                                    showAddPaperDialog = true
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("add_custom_exam_paper_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Paper", fontSize = 12.sp)
                            }
                        }

                        Text(
                            text = "Rename existing codes/titles, delete papers you are not taking, or add custom tests (e.g. GATE Mechanical, GRE, CAT, GMAT, USMLE, State PSC).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // List of current papers
                        gatePapers.forEach { paper ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = PrimaryIndigo,
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = paper.paperCode.take(4),
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color.White,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = paper.paperName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Paper Code: ${paper.paperCode}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = {
                                                editingPaper = paper
                                                editPaperCode = paper.paperCode
                                                editPaperName = paper.paperName
                                            },
                                            modifier = Modifier.size(36.dp).testTag("edit_paper_${paper.paperCode}")
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit Paper", tint = PrimaryIndigo, modifier = Modifier.size(18.dp))
                                        }
                                        if (gatePapers.size > 1) {
                                            IconButton(
                                                onClick = { viewModel.deleteGatePaper(paper.id) },
                                                modifier = Modifier.size(36.dp).testTag("delete_paper_${paper.paperCode}")
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete Paper", tint = DangerRose.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // UPSC / Civil Services & Multi-Stage Exam Stages Customizer
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Gavel, contentDescription = null, tint = PrimaryIndigo)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "UPSC ESE / Civil Service Stages",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Rename exam stages to match your competitive test syllabus (e.g., General Studies, Domain Specific Technical, Mains Written Essay).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        eseSections.forEach { section ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = section.stageType.replace("_", " "),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = PrimaryIndigo,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = section.paperTitle,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            editingSection = section
                                            editSectionTitle = section.paperTitle
                                        },
                                        modifier = Modifier.size(32.dp).testTag("edit_ese_section_${section.id}")
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Section Title", tint = PrimaryIndigo, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Paper Dialog
    if (showAddPaperDialog) {
        AlertDialog(
            onDismissRequest = { showAddPaperDialog = false },
            title = { Text("Add Competitive Exam / Paper") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Specify the test code and descriptive name (e.g. ME for Mechanical, GRE for GRE Quant, CAT for MBA Aptitude).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = newPaperCode,
                        onValueChange = { newPaperCode = it },
                        label = { Text("Paper Code (e.g. ME, CAT, GRE, EE)") },
                        modifier = Modifier.fillMaxWidth().testTag("new_paper_code_input")
                    )
                    OutlinedTextField(
                        value = newPaperName,
                        onValueChange = { newPaperName = it },
                        label = { Text("Full Paper Name (e.g. Mechanical Engineering)") },
                        modifier = Modifier.fillMaxWidth().testTag("new_paper_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPaperCode.isNotBlank() && newPaperName.isNotBlank()) {
                            viewModel.addGatePaper(newPaperCode.trim().uppercase(), newPaperName.trim())
                            showAddPaperDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_paper_btn")
                ) {
                    Text("Add Exam Paper")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPaperDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Paper Dialog
    if (editingPaper != null) {
        val paper = editingPaper!!
        AlertDialog(
            onDismissRequest = { editingPaper = null },
            title = { Text("Edit Exam Paper") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editPaperCode,
                        onValueChange = { editPaperCode = it },
                        label = { Text("Paper Code (e.g. ME, EE, CAT)") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_paper_code_input")
                    )
                    OutlinedTextField(
                        value = editPaperName,
                        onValueChange = { editPaperName = it },
                        label = { Text("Full Paper Name") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_paper_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editPaperCode.isNotBlank() && editPaperName.isNotBlank()) {
                            viewModel.updateGatePaper(paper.id, editPaperCode.trim().uppercase(), editPaperName.trim())
                            editingPaper = null
                        }
                    },
                    modifier = Modifier.testTag("save_edit_paper_btn")
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingPaper = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit ESE Section Dialog
    if (editingSection != null) {
        val section = editingSection!!
        AlertDialog(
            onDismissRequest = { editingSection = null },
            title = { Text("Edit Stage Section Title") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Stage: ${section.stageType.replace("_", " ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryIndigo,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = editSectionTitle,
                        onValueChange = { editSectionTitle = it },
                        label = { Text("Paper / Section Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editSectionTitle.isNotBlank()) {
                            viewModel.updateEseSection(section.id, editSectionTitle.trim())
                            editingSection = null
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSection = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Preset Confirmation Dialog
    if (pendingPreset != null) {
        val preset = pendingPreset!!
        AlertDialog(
            onDismissRequest = { pendingPreset = null },
            title = { Text("Apply Preset: ${preset.title}?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("This will update your Academic Track names:")
                    Text("• Degree 1: ${preset.deg1Name}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("• Degree 2: ${preset.deg2Name}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("• Competitive Papers: ${preset.papers.joinToString { it.first }}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateDegreeName("DEG_1", preset.deg1Name, preset.deg1Inst)
                        viewModel.updateDegreeName("DEG_2", preset.deg2Name, preset.deg2Inst)
                        deg1NameInput = preset.deg1Name
                        deg1InstInput = preset.deg1Inst
                        deg2NameInput = preset.deg2Name
                        deg2InstInput = preset.deg2Inst

                        // Clear and insert preset papers
                        gatePapers.forEach { p ->
                            viewModel.deleteGatePaper(p.id)
                        }
                        preset.papers.forEach { (code, name) ->
                            viewModel.addGatePaper(code, name)
                        }
                        pendingPreset = null
                    }
                ) {
                    Text("Apply Preset")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingPreset = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
