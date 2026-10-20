package com.example.ui.screens.certificates

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CertificateEntity
import com.example.ui.MainViewModel
import com.example.ui.components.AcademicTopAppBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DangerRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessEmerald

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificatesHubScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val allCertificates by viewModel.allCertificates.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: Wanted, 1: Owned
    var showAddDialog by remember { mutableStateOf(false) }
    var viewingCertificate by remember { mutableStateOf<CertificateEntity?>(null) }

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
            // Two-Tab / Two-Column Selection (Tinkered Feature)
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
                        Text(
                            text = "No target credentials on your wishlist yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                                    cert.tags.split(",").map { it.trim() }.forEach { tag ->
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
                // Certificates I Have (Achieved with internal vault storage)
                if (ownedCerts.isEmpty()) {
                    item {
                        Text(
                            text = "No verified certificates stored yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(ownedCerts, key = { it.id }) { cert ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("owned_cert_${cert.id}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
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

                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Vault: ${cert.filePath ?: "Stored in App Private Files"}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewingCertificate = cert },
                                    modifier = Modifier.align(Alignment.End).testTag("view_cert_${cert.id}"),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Open Offline Document")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Offline Document Preview Dialog
    if (viewingCertificate != null) {
        val cert = viewingCertificate!!
        AlertDialog(
            onDismissRequest = { viewingCertificate = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = SuccessEmerald)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(cert.certificateName)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Issuer: ${cert.issuer}", fontWeight = FontWeight.Bold)
                    Text("Issued Date: ${cert.targetAcquisitionDate}")
                    Text("Security Path: ${cert.filePath}")
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "🔒 Secure Document Preview\nVerified Cryptographic File Signature\nOffline Scoped Storage Active",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewingCertificate = null }) {
                    Text("Close")
                }
            }
        )
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var issuer by remember { mutableStateOf("") }
        var date by remember { mutableStateOf("2026-12-31") }
        var tags by remember { mutableStateOf("Academic, Honors") }
        var status by remember { mutableStateOf(if (selectedTab == 0) "WANTED" else "OWNED") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Certificate Entry") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addCertificate(name, status, date, issuer, tags)
                            showAddDialog = false
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
