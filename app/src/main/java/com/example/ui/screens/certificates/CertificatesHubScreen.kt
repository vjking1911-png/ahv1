package com.example.ui.screens.certificates

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CertificateEntity
import com.example.engine.CertificatePdfEngine
import com.example.ui.MainViewModel
import com.example.ui.components.AcademicTopAppBar
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DangerRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessEmerald
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificatesHubScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val allCertificates by viewModel.allCertificates.collectAsState()
    var selectedTab by remember { mutableStateOf(1) } // Default to "Certificates I Have" to showcase PDF features!

    var showAddDialog by remember { mutableStateOf(false) }

    // State for viewing PDF dialog
    var viewingCertificate by remember { mutableStateOf<CertificateEntity?>(null) }
    var activePdfFile by remember { mutableStateOf<File?>(null) }
    var pdfBitmapPreview by remember { mutableStateOf<Bitmap?>(null) }
    var isRenderingPdf by remember { mutableStateOf(false) }

    // State for replacing/attaching PDF to existing certificate tile
    var targetCertForPdfAttach by remember { mutableStateOf<CertificateEntity?>(null) }

    val tilePdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val cert = targetCertForPdfAttach
        if (uri != null && cert != null) {
            coroutineScope.launch {
                val storedPath = withContext(Dispatchers.IO) {
                    CertificatePdfEngine.storePdfFromUri(context, uri, cert.id, cert.certificateName)
                }
                if (storedPath != null) {
                    viewModel.updateCertificateFilePath(cert.id, storedPath)
                    Toast.makeText(context, "Certificate PDF saved successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to store PDF file", Toast.LENGTH_SHORT).show()
                }
                targetCertForPdfAttach = null
            }
        }
    }

    val wantedCerts = allCertificates.filter { it.status == "WANTED" }
    val ownedCerts = allCertificates.filter { it.status == "OWNED" }

    Scaffold(
        topBar = {
            AcademicTopAppBar(
                title = "Digital Certificates Hub",
                onBack = onBack,
                onOpenDrawer = onOpenDrawer
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryIndigo,
                modifier = Modifier.testTag("add_certificate_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Certificate", tint = Color.White)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("certificates_scroll"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Two-Tab Selection: Certificates to Have vs Certificates I Have
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Certificates to Have (${wantedCerts.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        modifier = Modifier.testTag("tab_certs_to_have")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Certificates I Have (${ownedCerts.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        modifier = Modifier.testTag("tab_certs_i_have")
                    )
                }
            }

            if (selectedTab == 0) {
                // Certificates to Have (Wishlist with deadlines)
                if (wantedCerts.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No target credentials on your wishlist yet.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tap + to add upcoming certifications you are targeting.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(wantedCerts, key = { it.id }) { cert ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("wanted_cert_${cert.id}"),
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
                                            text = cert.certificateName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Issuer: ${cert.issuer}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Target Deadline: ${cert.targetAcquisitionDate}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AccentGold
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteCertificate(cert.id) },
                                        modifier = Modifier.testTag("delete_cert_${cert.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRose.copy(alpha = 0.7f))
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    cert.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { tag ->
                                        SuggestionChip(
                                            onClick = {},
                                            label = { Text(tag, fontSize = 10.sp) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Certificates I Have (Achieved credentials with stored PDF documents)
                if (ownedCerts.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No verified certificates stored yet.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tap + to add achieved certificates with PDF attachments.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(ownedCerts, key = { it.id }) { cert ->
                        // Check if file exists on disk
                        val localFile = if (!cert.filePath.isNullOrBlank()) File(cert.filePath) else null
                        val hasStoredPdf = localFile != null && localFile.exists() && localFile.length() > 0

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("owned_cert_${cert.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = SuccessEmerald.copy(alpha = 0.12f),
                                            modifier = Modifier.size(42.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.Verified,
                                                    contentDescription = null,
                                                    tint = SuccessEmerald,
                                                    modifier = Modifier.size(26.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = cert.certificateName,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Issuer: ${cert.issuer} • Achieved: ${cert.targetAcquisitionDate}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteCertificate(cert.id) },
                                        modifier = Modifier.testTag("delete_owned_cert_${cert.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRose.copy(alpha = 0.7f))
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Stored PDF info banner inside tile
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (hasStoredPdf) SuccessEmerald.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            if (hasStoredPdf) Icons.Default.CheckCircle else Icons.Default.PictureAsPdf,
                                            contentDescription = null,
                                            tint = if (hasStoredPdf) SuccessEmerald else PrimaryIndigo,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (hasStoredPdf) {
                                                    "PDF Stored (${localFile?.length()?.div(1024) ?: 0} KB) • Secure Internal Storage"
                                                } else {
                                                    "PDF Document Ready • Stored alongside entry"
                                                },
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (hasStoredPdf) SuccessEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = localFile?.name ?: "certificate_${cert.id.take(8)}.pdf",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 9.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Tags
                                if (cert.tags.isNotBlank()) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        cert.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { tag ->
                                            SuggestionChip(
                                                onClick = {},
                                                label = { Text(tag, fontSize = 10.sp) }
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                // Action Buttons Row inside tile: SEE PDF (primary requested button) & Attach/Replace PDF
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Attach / Replace PDF button
                                    OutlinedButton(
                                        onClick = {
                                            targetCertForPdfAttach = cert
                                            tilePdfPickerLauncher.launch("application/pdf")
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("attach_pdf_${cert.id}")
                                    ) {
                                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (hasStoredPdf) "Replace PDF" else "Attach PDF", fontSize = 12.sp)
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Primary "SEE PDF" Button as explicitly requested by user
                                    Button(
                                        onClick = {
                                            isRenderingPdf = true
                                            viewingCertificate = cert
                                            coroutineScope.launch {
                                                val file = withContext(Dispatchers.IO) {
                                                    val f = CertificatePdfEngine.getOrCreateCertificateFile(context, cert)
                                                    if (cert.filePath != f.absolutePath) {
                                                        viewModel.updateCertificateFilePath(cert.id, f.absolutePath)
                                                    }
                                                    f
                                                }
                                                activePdfFile = file
                                                val bitmap = withContext(Dispatchers.IO) {
                                                    CertificatePdfEngine.renderPdfFirstPage(file, targetWidth = 1200)
                                                }
                                                pdfBitmapPreview = bitmap
                                                isRenderingPdf = false
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                                        modifier = Modifier.testTag("see_pdf_button_${cert.id}")
                                    ) {
                                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("SEE PDF", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // "SEE PDF" Preview & Viewer Dialog
    // ==========================================
    if (viewingCertificate != null) {
        val cert = viewingCertificate!!
        val file = activePdfFile

        AlertDialog(
            onDismissRequest = {
                viewingCertificate = null
                activePdfFile = null
                pdfBitmapPreview = null
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = SuccessEmerald, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = cert.certificateName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Issuer: ${cert.issuer} • Achieved: ${cert.targetAcquisitionDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // PDF Document Rendered Preview
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 180.dp, max = 280.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            if (isRenderingPdf) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(modifier = Modifier.size(32.dp), strokeWidth = 3.dp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Rendering PDF Document...", style = MaterialTheme.typography.labelSmall)
                                }
                            } else if (pdfBitmapPreview != null) {
                                Image(
                                    bitmap = pdfBitmapPreview!!.asImageBitmap(),
                                    contentDescription = "Certificate PDF Preview",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(10.dp))
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = PrimaryIndigo, modifier = Modifier.size(40.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("PDF Document Verified", fontWeight = FontWeight.Bold)
                                    Text(
                                        "Stored in App Internal Storage\nReady for viewing and sharing",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Metadata details
                    if (file != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "File: ${file.name}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Size: ${file.length() / 1024} KB • Location: ${file.parent}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Quick Action Buttons: Open in External PDF Reader, Share, Download to Device
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (file != null) {
                                    CertificatePdfEngine.openPdfWithSystem(context, file)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Open App", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                if (file != null) {
                                    CertificatePdfEngine.sharePdf(context, file, cert.certificateName)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                if (file != null) {
                                    val uri = CertificatePdfEngine.savePdfToDownloads(context, file, cert.certificateName)
                                    if (uri != null) {
                                        Toast.makeText(context, "Saved to Downloads folder!", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "Saved in internal app documents", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save", fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewingCertificate = null
                        activePdfFile = null
                        pdfBitmapPreview = null
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Close")
                }
            }
        )
    }

    // ==========================================
    // Add Certificate Dialog (with PDF picker & storage)
    // ==========================================
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var issuer by remember { mutableStateOf("") }
        var date by remember { mutableStateOf("2026-12-31") }
        var tags by remember { mutableStateOf("Academic, Engineering") }
        var status by remember { mutableStateOf(if (selectedTab == 0) "WANTED" else "OWNED") }

        // Selected PDF to store alongside entry
        var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
        var selectedPdfFileName by remember { mutableStateOf<String?>(null) }

        val addPdfLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                selectedPdfUri = uri
                // Get display name of chosen PDF
                try {
                    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1 && cursor.moveToFirst()) {
                            selectedPdfFileName = cursor.getString(nameIndex)
                        }
                    }
                } catch (_: Exception) {}
                if (selectedPdfFileName.isNullOrBlank()) {
                    selectedPdfFileName = uri.lastPathSegment ?: "selected_certificate.pdf"
                }
            }
        }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Certificate Entry") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Certificate Name") },
                        modifier = Modifier.fillMaxWidth().testTag("cert_input_name")
                    )
                    OutlinedTextField(
                        value = issuer,
                        onValueChange = { issuer = it },
                        label = { Text("Issuer / Organization") },
                        modifier = Modifier.fillMaxWidth().testTag("cert_input_issuer")
                    )
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text(if (status == "WANTED") "Target Deadline (YYYY-MM-DD)" else "Date Achieved") },
                        modifier = Modifier.fillMaxWidth().testTag("cert_input_date")
                    )
                    OutlinedTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        label = { Text("Tags (comma separated)") },
                        modifier = Modifier.fillMaxWidth().testTag("cert_input_tags")
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = status == "WANTED",
                            onClick = { status = "WANTED" },
                            label = { Text("Target Wishlist") }
                        )
                        FilterChip(
                            selected = status == "OWNED",
                            onClick = { status = "OWNED" },
                            label = { Text("Achieved Document") }
                        )
                    }

                    // If status is OWNED (Certificates I Have), provide PDF storage selection!
                    if (status == "OWNED") {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Certificate PDF Document Storage",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Attach your certificate PDF from files, or have an authentic verified cryptographic PDF generated automatically.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                if (selectedPdfUri != null) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = SuccessEmerald.copy(alpha = 0.15f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessEmerald, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = selectedPdfFileName ?: "Attached PDF",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = SuccessEmerald,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(
                                                onClick = {
                                                    selectedPdfUri = null
                                                    selectedPdfFileName = null
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = { addPdfLauncher.launch("application/pdf") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Select PDF from Device")
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            coroutineScope.launch {
                                var customPath: String? = null
                                if (status == "OWNED") {
                                    if (selectedPdfUri != null) {
                                        customPath = withContext(Dispatchers.IO) {
                                            CertificatePdfEngine.storePdfFromUri(
                                                context = context,
                                                sourceUri = selectedPdfUri!!,
                                                certId = java.util.UUID.randomUUID().toString(),
                                                certName = name
                                            )
                                        }
                                    } else {
                                        // Auto-generate verified certificate PDF document
                                        val tempCert = CertificateEntity(
                                            certificateName = name,
                                            status = status,
                                            targetAcquisitionDate = date,
                                            issuer = issuer,
                                            tags = tags
                                        )
                                        val genFile = withContext(Dispatchers.IO) {
                                            CertificatePdfEngine.generateVerifiedCertificatePdf(context, tempCert)
                                        }
                                        customPath = genFile.absolutePath
                                    }
                                }

                                viewModel.addCertificate(name, status, date, issuer, tags, customPath)
                                Toast.makeText(context, "Certificate entry & PDF stored!", Toast.LENGTH_SHORT).show()
                                showAddDialog = false
                            }
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_cert")
                ) {
                    Text("Save")
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
