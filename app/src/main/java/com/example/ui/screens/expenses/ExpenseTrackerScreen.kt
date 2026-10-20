package com.example.ui.screens.expenses

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.AcademicTopAppBar
import com.example.ui.components.MetricCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DangerRose
import com.example.ui.theme.PrimaryIndigo
import java.text.SimpleDateFormat
import java.util.*

val EXPENSE_CATEGORIES = listOf(
    "Academic",
    "Certification",
    "Skills",
    "Exam Fees",
    "Instruments & Devices",
    "Other"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTrackerScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val expenses by viewModel.expenses.collectAsState()

    var selectedCategoryFilter by remember { mutableStateOf("ALL") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredExpenses = expenses.filter {
        selectedCategoryFilter == "ALL" || it.category.equals(selectedCategoryFilter, ignoreCase = true)
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
                        title = if (selectedCategoryFilter == "ALL") "Total Expenditure" else "$selectedCategoryFilter Spend",
                        value = "₹${String.format("%,.2f", totalSpent)}",
                        subtitle = "${filteredExpenses.size} Items Tracked",
                        accentColor = PrimaryIndigo,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Academic ROI Score",
                        value = "${String.format("%.1f", avgRoi)} ★",
                        subtitle = "Impact on Career",
                        accentColor = AccentGold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Category Filter Chips
            item {
                Column {
                    Text(
                        text = "FILTER BY CATEGORY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val filterOptions = listOf("ALL") + EXPENSE_CATEGORIES
                        filterOptions.forEach { cat ->
                            FilterChip(
                                selected = selectedCategoryFilter == cat,
                                onClick = { selectedCategoryFilter = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                modifier = Modifier.testTag("filter_cat_${cat.replace(" ", "_").replace("&", "and")}")
                            )
                        }
                    }
                }
            }

            // Items List
            if (filteredExpenses.isEmpty()) {
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
                                text = "No expenses recorded in \"$selectedCategoryFilter\"",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tap the + button to log an expense in this category.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
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
                                    val paymentTypeLabel = if (expense.paymentType == "RECURRING") "Recurring Subscription" else "One-time"
                                    Text(
                                        text = "$paymentTypeLabel • $dateStr",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = "₹${String.format("%,.2f", expense.amount)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PrimaryIndigo
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Impact: ",
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
                                    StatusBadge(status = expense.category)
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
        var selectedCategory by remember { mutableStateOf(if (selectedCategoryFilter != "ALL") selectedCategoryFilter else "Academic") }
        var customCategoryText by remember { mutableStateOf("") }
        var paymentType by remember { mutableStateOf("ONETIME") }
        var roiRating by remember { mutableStateOf(4) }
        var collegeScope by remember { mutableStateOf("COLLEGE_1") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Log Academic Expense") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title (e.g. Graphing Calculator, AWS Exam)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_expense_title")
                    )
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().testTag("add_expense_amount")
                    )

                    Text("Expense Category:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            EXPENSE_CATEGORIES.forEach { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, fontSize = 11.sp) },
                                    modifier = Modifier.testTag("dialog_cat_${cat.replace(" ", "_")}")
                                )
                            }
                        }
                    }

                    if (selectedCategory == "Other") {
                        OutlinedTextField(
                            value = customCategoryText,
                            onValueChange = { customCategoryText = it },
                            label = { Text("Custom Category Name (Optional)") },
                            modifier = Modifier.fillMaxWidth().testTag("add_expense_custom_cat")
                        )
                    }

                    Text("Payment Type:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = paymentType == "ONETIME",
                            onClick = { paymentType = "ONETIME" },
                            label = { Text("One-time") }
                        )
                        FilterChip(
                            selected = paymentType == "RECURRING",
                            onClick = { paymentType = "RECURRING" },
                            label = { Text("Recurring") }
                        )
                    }

                    Text("Academic ROI / Career Impact (1-5 Stars):", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        (1..5).forEach { star ->
                            IconButton(
                                onClick = { roiRating = star },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "$star stars",
                                    tint = if (star <= roiRating) AccentGold else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(22.dp)
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
                        val finalCategory = if (selectedCategory == "Other" && customCategoryText.isNotBlank()) {
                            customCategoryText.trim()
                        } else {
                            selectedCategory
                        }
                        if (title.isNotBlank() && amount > 0) {
                            viewModel.addExpense(
                                title = title.trim(),
                                amount = amount,
                                category = finalCategory,
                                paymentType = paymentType,
                                roiRating = roiRating,
                                collegeScope = collegeScope,
                                semester = finalCategory
                            )
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
