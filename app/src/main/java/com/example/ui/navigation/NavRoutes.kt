package com.example.ui.navigation

sealed class Screen(val route: String, val title: String) {
    data object Dashboard : Screen("dashboard", "Dashboard")
    data object AcademicsHub : Screen("academics_hub", "Academics")
    data object DualSyllabus : Screen("dual_syllabus", "Dual Syllabus")
    data object ResultsFormula : Screen("results_formula", "Results & Formula Engine")
    data object Expenses : Screen("expenses", "Expense Tracker")
    data object CompetitiveHub : Screen("competitive_hub", "Exams")
    data object GateTracker : Screen("gate_tracker", "GATE Tracker")
    data object EseTracker : Screen("ese_tracker", "UPSC ESE Tracker")
    data object CareerHub : Screen("career_hub", "Career & Skills")
    data object Certificates : Screen("certificates", "Certificates Hub")
    data object Upskilling : Screen("upskilling", "Upskilling Wishlist")
    data object CareerMatrix : Screen("career_matrix", "Career Matrix")
    data object ResumeBuilder : Screen("resume_builder", "Resume Builder")
    data object TimetableHub : Screen("timetable_hub", "Timetable & Alerts")
    data object Milestones : Screen("milestones", "Milestone Alerts")
    data object Customizer : Screen("customizer", "Customize Trackers")
}
