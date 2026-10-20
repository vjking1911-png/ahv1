package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

// ==========================================
// 1. Academic Expense Tracker
// ==========================================
@Entity(tableName = "academic_expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val category: String, // Academic, Certification, Skills, Exam Fees, Instruments & Devices, Other
    val paymentType: String, // ONETIME, RECURRING
    val timestamp: Long = System.currentTimeMillis(),
    val roiRating: Int? = null, // 1 to 5 Academic Impact Score / ROI rating
    val collegeProfileId: String? = null, // Links to COLLEGE_1 or COLLEGE_2
    val semesterTag: String = "Academic"
)

// ==========================================
// 2 & 3. Dual College Syllabus Trackers
// ==========================================
@Entity(tableName = "semesters")
data class SemesterEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val profileScope: String, // COLLEGE_1 or COLLEGE_2
    val semesterNumber: Int,
    val isActive: Boolean = true,
    val gpaEarned: Double = 0.0
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val semesterId: String,
    val subjectName: String,
    val subjectCode: String,
    val credits: Int
)

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val subjectId: String,
    val topicTitle: String,
    val status: String = "TODO", // TODO, DOING, REVISING, DONE
    val weightage: String = "MEDIUM", // EASY, MEDIUM, HARD
    val estimatedHours: Int = 4
)

// ==========================================
// 4. Dual-Degree Result Tracker & Formula Engine
// ==========================================
@Entity(tableName = "degree_profiles")
data class DegreeProfileEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val degreeName: String,
    val institutionName: String,
    val calculationFormulaString: String = "(CGPA * 9.5)", // e.g. (CGPA * 9.5) or (CGPA * 10) + 5
    val totalTargetCredits: Int = 160
)

@Entity(tableName = "semester_grades")
data class SemesterGradeEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val degreeProfileId: String,
    val semesterNumber: Int,
    val sgpa: Double = 0.0,
    val totalCreditsEarned: Int = 0
)

@Entity(tableName = "subject_marks")
data class SubjectMarkEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val degreeProfileId: String,
    val semesterNumber: Int,
    val subjectName: String,
    val marksObtained: Double = 0.0,
    val maxMarks: Double = 100.0,
    val gradePoint: Double = 0.0,
    val credits: Int = 4,
    val subjectCode: String = ""
)

@Entity(tableName = "subject_exams")
data class SubjectExamEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val subjectMarkId: String,
    val examName: String,
    val weightagePercent: Double, // What percentage of final score does this exam contribute (e.g. 30.0 for 30%)
    val marksObtained: Double, // How much marks gotten in that exam (e.g. 42.0)
    val maxMarks: Double = 50.0 // Maximum marks in this exam (e.g. 50.0)
)

// ==========================================
// 5. Triple-Paper GATE Syllabus Tracker
// ==========================================
@Entity(tableName = "gate_papers")
data class GatePaperEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val paperCode: String, // e.g., CS, DA, EC
    val paperName: String
)

@Entity(tableName = "gate_subjects")
data class GateSubjectEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val paperId: String,
    val subjectName: String,
    val subjectCode: String = "",
    val weightageMarks: Int = 10
)

@Entity(tableName = "gate_topics")
data class GateTopicEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val paperId: String,
    val subjectCategory: String = "", // e.g. Operating Systems, Machine Learning, Linear Algebra
    val topicName: String,
    val status: String = "TODO", // TODO, DOING, REVISING, DONE
    val startDate: String = "",
    val endDate: String = "",
    val revisionCount: Int = 0,
    val confidenceLevel: Int = 50, // 1 to 100%
    val weightage: String = "MEDIUM", // EASY, MEDIUM, HARD
    val estimatedHours: Int = 4,
    val subjectId: String = ""
)

// ==========================================
// 6. ESE UPSC Syllabus Tracker (Electronics & Telecommunication)
// ==========================================
@Entity(tableName = "ese_papers")
data class EsePaperEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val paperCode: String, // e.g. "ET-1", "ET-2", "GS-EA", "MAINS-ET"
    val paperName: String,
    val stageType: String = "PRELIMS_TECH", // PRELIMS_GS, PRELIMS_TECH, MAINS_TECH
    val totalMarks: Int = 300,
    val targetScore: Int = 210,
    val examDate: String = "",
    val displayOrder: Int = 0
)

@Entity(tableName = "ese_subjects")
data class EseSubjectEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val paperId: String,
    val subjectName: String,
    val subjectCode: String = "",
    val weightageMarks: Int = 30,
    val displayOrder: Int = 0
)

@Entity(tableName = "ese_sections")
data class EseSectionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val stageType: String, // PRELIMS_GS, PRELIMS_TECH, MAINS_TECH
    val paperTitle: String
)

@Entity(tableName = "ese_topics")
data class EseTopicEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val paperId: String = "",
    val subjectId: String = "",
    val sectionId: String = "",
    val subjectCategory: String = "",
    val topicName: String,
    val status: String = "TODO", // TODO, DOING, REVISING, DONE
    val pyqSolvedCount: Int = 0,
    val notesAttachedFlag: Boolean = false,
    val revisionCount: Int = 0,
    val confidenceLevel: Int = 50, // 1 to 100%
    val weightage: String = "MEDIUM", // EASY, MEDIUM, HARD
    val estimatedHours: Int = 4
)

// ==========================================
// 7. Digital Certificates Hub
// ==========================================
@Entity(tableName = "certificates")
data class CertificateEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val certificateName: String,
    val status: String = "WANTED", // WANTED (to have), OWNED (I have)
    val filePath: String? = null,
    val targetAcquisitionDate: String = "",
    val issuer: String = "",
    val tags: String = "Academic"
)

// ==========================================
// 8. Holiday vs. Working Day Dynamic Timetable
// ==========================================
@Entity(tableName = "timetable_configs")
data class TimetableConfigEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val scheduleType: String, // WORKING_DAY, HOLIDAY
    val timeSlotStart: String,
    val timeSlotEnd: String,
    val slotTitle: String,
    val associatedModuleId: String? = null
)

@Entity(tableName = "daily_slot_logs")
data class DailySlotLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val dateString: String, // YYYY-MM-DD
    val scheduleTypeUsed: String,
    val slotTitle: String,
    val isTickedDone: Boolean = false
)

// ==========================================
// 9. Front-Page Priority Notepad
// ==========================================
@Entity(tableName = "daily_scratchpad")
data class DailyScratchpadEntity(
    @PrimaryKey val dateString: String, // YYYY-MM-DD
    val rawMarkdownText: String = "",
    val layoutPinnedFlag: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
)

// ==========================================
// 10. Semester Writeup & Proactive Alert Engine
// ==========================================
@Entity(tableName = "academic_milestones")
data class AcademicMilestoneEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val eventTitle: String,
    val eventDescription: String = "",
    val targetTimestamp: Long, // Epoch millis
    val alertTriggeredFlag: Boolean = false,
    val collegeProfileId: String = "COLLEGE_1"
)

// ==========================================
// 11. Future Upskilling Checklist
// ==========================================
@Entity(tableName = "future_courses")
data class FutureCourseEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val courseName: String,
    val timelineWhen: String, // Target Timeline (When)
    val motivationWhy: String, // Strategic Rationale (Why)
    val costEstimate: Double = 0.0,
    val preRequisites: String = "",
    val isCompleted: Boolean = false
)

// ==========================================
// 12. Future Career Options Strategy Matrix
// ==========================================
@Entity(tableName = "career_options")
data class CareerOptionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val feasibilityScore: Int = 8, // 1 to 10
    val riskLevel: String = "LOW", // LOW, MED, HIGH
    val actionStepsText: String = "",
    val targetRole: String = "",
    val salaryRange: String = ""
)

// ==========================================
// 13. Local Tabular Resume Builder
// ==========================================
@Entity(tableName = "resume_profiles")
data class ResumeProfileEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val profileName: String = "Default Technical CV",
    val fullName: String = "Alex Rivera",
    val email: String = "alex.rivera@university.edu",
    val phone: String = "+1 (555) 019-2834",
    val githubUrl: String = "github.com/alexrivera",
    val linkedinUrl: String = "linkedin.com/in/alexrivera",
    val summary: String = "Motivated Dual-Degree Scholar with rigorous foundations in Systems Software and AI.",
    val educationJson: String = "",
    val projectsJson: String = "",
    val skillsJson: String = "",
    val experienceJson: String = "",
    val photoUri: String = ""
)
