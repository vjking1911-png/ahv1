package com.example.ui.screens.upskilling

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
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
import com.example.ui.theme.SuccessEmerald

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpskillingScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val allCourses by viewModel.allFutureCourses.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val completedCount = allCourses.count { it.isCompleted }
    val pendingCount = allCourses.size - completedCount

    Scaffold(
        topBar = {
            AcademicTopAppBar(
                title = "Future Upskilling Checklist",
                onBack = onBack,
                onOpenDrawer = onOpenDrawer
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryIndigo,
                modifier = Modifier.testTag("add_course_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Course", tint = Color.White)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("upskilling_scroll"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tabular Header Banner
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "STRUCTURED TABULAR WISHLIST",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryIndigo
                            )
                            Text(
                                text = "[Course Name | Target Timeline (When) | Strategic Rationale (Why)]",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SuccessEmerald.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "$completedCount Done • $pendingCount Queued",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = SuccessEmerald,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            if (allCourses.isEmpty()) {
                item {
                    Text(
                        text = "No future courses queued in your wishlist.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(allCourses, key = { it.id }) { course ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("course_card_${course.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = course.isCompleted,
                                        onCheckedChange = { viewModel.toggleCourseCompleted(course) },
                                        modifier = Modifier.testTag("checkbox_course_${course.id}")
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = course.courseName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = if (course.isCompleted) FontWeight.Normal else FontWeight.Bold,
                                            color = if (course.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Cost: ${if (course.costEstimate > 0) "$${course.costEstimate}" else "Free/Open Courseware"}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.deleteCourse(course.id) },
                                    modifier = Modifier.size(28.dp).testTag("delete_course_${course.id}")
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRose.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Three Columns: Course Name | Timeline (When) | Strategic Rationale (Why)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "TARGET TIMELINE (WHEN)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentGold
                                    )
                                    Text(
                                        text = course.timelineWhen,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Column(modifier = Modifier.weight(1.5f)) {
                                    Text(
                                        text = "STRATEGIC RATIONALE (WHY)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryIndigo
                                    )
                                    Text(
                                        text = course.motivationWhy,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            if (course.preRequisites.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Prerequisites: ${course.preRequisites}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var whenTime by remember { mutableStateOf("") }
        var why by remember { mutableStateOf("") }
        var costStr by remember { mutableStateOf("0") }
        var prereqs by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Course to Upskilling Matrix") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Course Name") },
                        modifier = Modifier.fillMaxWidth().testTag("add_course_name")
                    )
                    OutlinedTextField(
                        value = whenTime,
                        onValueChange = { whenTime = it },
                        label = { Text("Target Timeline (When)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_course_when")
                    )
                    OutlinedTextField(
                        value = why,
                        onValueChange = { why = it },
                        label = { Text("Strategic Rationale (Why)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_course_why")
                    )
                    OutlinedTextField(
                        value = prereqs,
                        onValueChange = { prereqs = it },
                        label = { Text("Prerequisites") },
                        modifier = Modifier.fillMaxWidth().testTag("add_course_prereqs")
                    )
                    OutlinedTextField(
                        value = costStr,
                        onValueChange = { costStr = it },
                        label = { Text("Cost Estimate ($)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_course_cost")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cost = costStr.toDoubleOrNull() ?: 0.0
                        if (name.isNotBlank() && whenTime.isNotBlank()) {
                            viewModel.addCourse(name, whenTime, why, cost, prereqs)
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_course")
                ) {
                    Text("Add to Wishlist")
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
