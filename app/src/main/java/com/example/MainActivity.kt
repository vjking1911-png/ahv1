package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.MainViewModel
import com.example.ui.navigation.Screen
import com.example.ui.screens.career.CareerMatrixScreen
import com.example.ui.screens.certificates.CertificatesHubScreen
import com.example.ui.screens.customizer.CustomizerScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.ese.EseTrackerScreen
import com.example.ui.screens.expenses.ExpenseTrackerScreen
import com.example.ui.screens.gate.GateTrackerScreen
import com.example.ui.screens.milestones.MilestonesScreen
import com.example.ui.screens.results.ResultsFormulaScreen
import com.example.ui.screens.resume.ResumeBuilderScreen
import com.example.ui.screens.syllabus.DualSyllabusScreen
import com.example.ui.screens.timetable.TimetableScreen
import com.example.ui.screens.upskilling.UpskillingScreen
import com.example.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AcademicHubApp(viewModel = viewModel)
            }
        }
    }
}

data class DrawerItemData(
    val screen: Screen,
    val icon: ImageVector,
    val label: String,
    val subtitle: String,
    val badge: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicHubApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Notification permission check for Module 10 proactive alert engine
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Dashboard.route

    // Primary Quick Bottom Nav Destinations
    val bottomNavItems = listOf(
        DrawerItemData(Screen.Dashboard, Icons.Default.Home, "Hub", "Dashboard"),
        DrawerItemData(Screen.DualSyllabus, Icons.Default.School, "Syllabus", "Dual Degrees"),
        DrawerItemData(Screen.GateTracker, Icons.Default.FilterFrames, "Exams", "GATE & ESE"),
        DrawerItemData(Screen.TimetableHub, Icons.Default.Schedule, "Schedule", "Timetable"),
        DrawerItemData(Screen.ResumeBuilder, Icons.Default.Description, "Resume", "CV Builder")
    )

    val isTopLevelRoute = bottomNavItems.any { it.screen.route == currentRoute }

    // Navigation function that strictly preserves offline state across switches
    val navigateToRoute: (String) -> Unit = { route ->
        scope.launch { drawerState.close() }
        if (currentRoute != route) {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(320.dp)
                    .testTag("app_navigation_drawer_sheet"),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 24.dp)
                ) {
                    // Drawer Header Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PrimaryIndigoDark)
                            .padding(horizontal = 20.dp, vertical = 24.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(AccentGold.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = AccentGold,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Academic Hub",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Offline Command Center",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PrimaryIndigoLight
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "13 Modules Active • SQLite Vault",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Group 1: Core Dashboard & Academics
                    DrawerSectionHeader("CORE DASHBOARD & ACADEMICS")
                    DrawerItem(
                        item = DrawerItemData(Screen.Dashboard, Icons.Default.Dashboard, "Dashboard Command", "Overview & Markdown Scratchpad"),
                        isSelected = currentRoute == Screen.Dashboard.route,
                        onClick = { navigateToRoute(Screen.Dashboard.route) }
                    )
                    DrawerItem(
                        item = DrawerItemData(Screen.Expenses, Icons.Default.ReceiptLong, "1. Academic Expenses", "Categories & ROI Rating", badge = "₹ Spend"),
                        isSelected = currentRoute == Screen.Expenses.route,
                        onClick = { navigateToRoute(Screen.Expenses.route) }
                    )
                    DrawerItem(
                        item = DrawerItemData(Screen.DualSyllabus, Icons.Default.School, "2 & 3. Dual Syllabus", "Degree 1 & Degree 2 Workload", badge = "Dual"),
                        isSelected = currentRoute == Screen.DualSyllabus.route,
                        onClick = { navigateToRoute(Screen.DualSyllabus.route) }
                    )
                    DrawerItem(
                        item = DrawerItemData(Screen.ResultsFormula, Icons.Default.Calculate, "4. Results & Formula", "Dynamic CGPA Engine Sandbox", badge = "Formula"),
                        isSelected = currentRoute == Screen.ResultsFormula.route,
                        onClick = { navigateToRoute(Screen.ResultsFormula.route) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                    // Group 2: Competitive Exam Trackers
                    DrawerSectionHeader("COMPETITIVE EXAM ENGINES")
                    DrawerItem(
                        item = DrawerItemData(Screen.GateTracker, Icons.Default.FilterFrames, "5. Triple-Paper GATE", "Revision Loops & Confidence", badge = "3 Papers"),
                        isSelected = currentRoute == Screen.GateTracker.route,
                        onClick = { navigateToRoute(Screen.GateTracker.route) }
                    )
                    DrawerItem(
                        item = DrawerItemData(Screen.EseTracker, Icons.Default.Gavel, "6. UPSC ESE Tracker", "E&T Syllabus & PYQ Mastery", badge = "UPSC"),
                        isSelected = currentRoute == Screen.EseTracker.route,
                        onClick = { navigateToRoute(Screen.EseTracker.route) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                    // Group 3: Schedule & Deadlines
                    DrawerSectionHeader("SCHEDULE & DEADLINES")
                    DrawerItem(
                        item = DrawerItemData(Screen.TimetableHub, Icons.Default.Schedule, "8. Dynamic Timetable", "Working Day vs Holiday Slots", badge = "Dynamic"),
                        isSelected = currentRoute == Screen.TimetableHub.route,
                        onClick = { navigateToRoute(Screen.TimetableHub.route) }
                    )
                    DrawerItem(
                        item = DrawerItemData(Screen.Milestones, Icons.Default.NotificationsActive, "10. Milestone Alerts", "24h Proactive Countdown", badge = "24h Alert"),
                        isSelected = currentRoute == Screen.Milestones.route,
                        onClick = { navigateToRoute(Screen.Milestones.route) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                    // Group 4: Career, Skills & Profile
                    DrawerSectionHeader("CAREER, SKILLS & PROFILE")
                    DrawerItem(
                        item = DrawerItemData(Screen.Certificates, Icons.Default.Verified, "7. Certificates Hub", "Digital Paper Vault & Targets"),
                        isSelected = currentRoute == Screen.Certificates.route,
                        onClick = { navigateToRoute(Screen.Certificates.route) }
                    )
                    DrawerItem(
                        item = DrawerItemData(Screen.Upskilling, Icons.Default.RocketLaunch, "11. Upskilling Checklist", "Timeline & Strategic Rationale"),
                        isSelected = currentRoute == Screen.Upskilling.route,
                        onClick = { navigateToRoute(Screen.Upskilling.route) }
                    )
                    DrawerItem(
                        item = DrawerItemData(Screen.CareerMatrix, Icons.Default.TrendingUp, "12. Career Strategy Matrix", "Feasibility Index & Risk Rating"),
                        isSelected = currentRoute == Screen.CareerMatrix.route,
                        onClick = { navigateToRoute(Screen.CareerMatrix.route) }
                    )
                    DrawerItem(
                        item = DrawerItemData(Screen.ResumeBuilder, Icons.Default.Description, "13. Tabular Resume Builder", "Offline Structured CV Compiler", badge = "Export"),
                        isSelected = currentRoute == Screen.ResumeBuilder.route,
                        onClick = { navigateToRoute(Screen.ResumeBuilder.route) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                    // Group 5: Customization & Preferences
                    DrawerSectionHeader("CUSTOMIZATION & PROFILE")
                    DrawerItem(
                        item = DrawerItemData(Screen.Customizer, Icons.Default.Tune, "Customize Trackers", "Change degrees, GATE papers & exams", badge = "Custom"),
                        isSelected = currentRoute == Screen.Customizer.route,
                        onClick = { navigateToRoute(Screen.Customizer.route) }
                    )
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (isTopLevelRoute) {
                    NavigationBar(
                        modifier = Modifier.testTag("main_bottom_nav"),
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        bottomNavItems.forEach { item ->
                            val selected = currentRoute == item.screen.route
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) },
                                selected = selected,
                                onClick = { navigateToRoute(item.screen.route) },
                                modifier = Modifier.testTag("nav_item_${item.screen.route}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            val openDrawerAction: () -> Unit = { scope.launch { drawerState.open() } }

            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                // Dashboard (Hub)
                composable(Screen.Dashboard.route) {
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigate = navigateToRoute,
                        onOpenDrawer = openDrawerAction
                    )
                }

                // Modules 2 & 3: Dual Syllabus
                composable(Screen.DualSyllabus.route) {
                    DualSyllabusScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onOpenDrawer = openDrawerAction,
                        onNavigateToCustomizer = { navigateToRoute(Screen.Customizer.route) }
                    )
                }

                // Module 4: Results & Formula Engine
                composable(Screen.ResultsFormula.route) {
                    ResultsFormulaScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onOpenDrawer = openDrawerAction
                    )
                }

                // Module 1: Academic Expenses
                composable(Screen.Expenses.route) {
                    ExpenseTrackerScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onOpenDrawer = openDrawerAction
                    )
                }

                // Module 5: Triple-Paper GATE Tracker
                composable(Screen.GateTracker.route) {
                    GateTrackerScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onOpenDrawer = openDrawerAction,
                        onNavigateToCustomizer = { navigateToRoute(Screen.Customizer.route) }
                    )
                }

                // Module 6: UPSC ESE Tracker
                composable(Screen.EseTracker.route) {
                    EseTrackerScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onOpenDrawer = openDrawerAction
                    )
                }

                // Module 7: Digital Certificates Hub
                composable(Screen.Certificates.route) {
                    CertificatesHubScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onOpenDrawer = openDrawerAction
                    )
                }

                // Module 8: Dynamic Timetable
                composable(Screen.TimetableHub.route) {
                    TimetableScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onOpenDrawer = openDrawerAction
                    )
                }

                // Module 10: Semester Milestones & Alert Engine
                composable(Screen.Milestones.route) {
                    MilestonesScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onOpenDrawer = openDrawerAction
                    )
                }

                // Module 11: Future Upskilling Checklist
                composable(Screen.Upskilling.route) {
                    UpskillingScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onOpenDrawer = openDrawerAction
                    )
                }

                // Module 12: Career Strategy Matrix
                composable(Screen.CareerMatrix.route) {
                    CareerMatrixScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onOpenDrawer = openDrawerAction
                    )
                }

                // Module 13: Local Tabular Resume Builder
                composable(Screen.ResumeBuilder.route) {
                    ResumeBuilderScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onOpenDrawer = openDrawerAction
                    )
                }

                // Customizer: Customize Trackers, Degrees, GATE Papers & Exams
                composable(Screen.Customizer.route) {
                    CustomizerScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onOpenDrawer = openDrawerAction
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
    )
}

@Composable
fun DrawerItem(
    item: DrawerItemData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        label = {
            Column {
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        badge = {
            if (item.badge != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSelected) PrimaryIndigo.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = item.badge,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        },
        selected = isSelected,
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("drawer_item_${item.screen.route}"),
        shape = RoundedCornerShape(12.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = PrimaryIndigo.copy(alpha = 0.12f),
            unselectedContainerColor = Color.Transparent
        )
    )
}
