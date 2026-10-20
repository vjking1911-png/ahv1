package com.example.ui.screens.expenses

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
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
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DangerRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTrackerScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val expenses by viewModel.expenses.collectAsState()

    var selectedSemesterFilter by remember { mutableStateOf("ALL") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredExpenses = expenses.filter {
        selectedSemesterFilter == "ALL" || it.semesterTag.equals(selectedSemesterFilter, ignoreCase = true)
    }

    val totalSpent = filteredExpenses.sumOf { it.amount }
    val avgRoi = if (filteredExpenses.isNotEmpty()) {
        filteredExpenses.mapNotNull { it.roiRating }.average().takeIf { !it.isNaN() } ?: 0.0
    } else 0.0

    Scaffold(
        topBar = {
            AcademicTopAppBar(
                title = "Academic Expense Tracker",
                onBack = onBack,
                onOpenDrawer = onOpenDrawer
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryIndigo,
                modifier = Modifier.testTag("add_expense_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense", tint = Color.White)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("expenses_scroll"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Analytics Summary
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Semester Expenditure",
                        value = "$${String.format("%.2f", totalSpent)}",
                        subtitle = "${filteredExpenses.size} Academic Items",
                        accentColor = PrimaryIndigo,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Academic ROI Score",
                        value = "${String.format("%.1f", avgRoi)} ★",
                        subtitle = "Impact on Grades",
                        accentColor = AccentGold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Semester Filters (Tinkered Requirement)
            item {
                Column {
                    Text(
                        text = "FILTER BY SEMESTER",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("ALL", "Semester 1", "Semester 2", "Semester 3", "Semester 4").forEach { sem ->
                            FilterChip(
                                selected = selectedSemesterFilter == sem,
                                onClick = { selectedSemesterFilter = sem },
                                label = { Text(sem, fontSize = 11.sp) },
                                modifier = Modifier.testTag("filter_sem_${sem.replace(" ", "_")}")
                            )
                        }
                    }
                }
            }

            // Items List
            if (filteredExpenses.isEmpty()) {
                item {
                    Text(
                        text = "No expenses recorded in this semester filter.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(filteredExpenses, key = { it.id }) { expense ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("expense_card_${expense.id}"),
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
                                        text = expense.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(expense.timestamp))
                                    Text(
                                        text = "${expense.category} • ${expense.paymentType} • $dateStr",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = "$${String.format("%.2f", expense.amount)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PrimaryIndigo
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Tinkered: Academic Impact Score (1 to 5 stars)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Grade Impact Score: ",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    val stars = expense.roiRating ?: 0
                                    repeat(5) { i ->
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = if (i < stars) AccentGold else MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    StatusBadge(status = expense.semesterTag)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { viewModel.deleteExpense(expense.id) },
                                        modifier = Modifier.size(28.dp).testTag("delete_expense_${expense.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Expense",
                                            tint = DangerRose.copy(alpha = 0.7f),
                                            modifier = Modifier.size(18.dp)
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

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var amountStr by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("BOOKS") }
        var paymentType by remember { mutableStateOf("ONETIME") }
        var roiRating by remember { mutableStateOf(4) }
        var semester by remember { mutableStateOf("Semester 3") }
        var collegeScope by remember { mutableStateOf("COLLEGE_1") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Log Academic Expense") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title (e.g. Graphing Calculator, Safari Books)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_expense_title")
                    )
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("Amount ($)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_expense_amount")
                    )

                    Text("Category & Payment Plan:", style = MaterialTheme.typography.labelSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("BOOKS", "SUBSCRIPTION", "STATIONERY", "EXAM_REGISTRATION").forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat.take(5), fontSize = 10.sp) }
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = paymentType == "ONETIME",
                            onClick = { paymentType = "ONETIME" },
                            label = { Text("One-time") }
                        )
                        FilterChip(
                            selected = paymentType == "RECURRING",
                            onClick = { paymentType = "RECURRING" },
                            label = { Text("Subscription") }
                        )
                    }

                    Text("Academic Impact Score (1-5 Stars):", style = MaterialTheme.typography.labelSmall)
                    Row {
                        (1..5).forEach { star ->
                            IconButton(onClick = { roiRating = star }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (star <= roiRating) AccentGold else MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = amountStr.toDoubleOrNull() ?: 0.0
                        if (title.isNotBlank() && amount > 0) {
                            viewModel.addExpense(title, amount, category, paymentType, roiRating, collegeScope, semester)
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_expense_button")
                ) {
                    Text("Add Expense")
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
