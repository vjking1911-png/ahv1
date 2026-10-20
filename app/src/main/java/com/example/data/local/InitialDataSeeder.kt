package com.example.data.local

import com.example.data.model.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object InitialDataSeeder {

    suspend fun seedInitialData(dao: AcademicDao) {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // 1. Academic Expenses
        dao.insertExpense(
            ExpenseEntity(
                id = UUID.randomUUID().toString(),
                title = "Texas Instruments Scientific Graphing Calculator",
                amount = 129.99,
                category = "STATIONERY",
                paymentType = "ONETIME",
                timestamp = System.currentTimeMillis() - 86400000L * 12,
                roiRating = 5,
                collegeProfileId = "COLLEGE_1",
                semesterTag = "Semester 3"
            )
        )
        dao.insertExpense(
            ExpenseEntity(
                id = UUID.randomUUID().toString(),
                title = "O'Reilly Safari Online Academic Subscription",
                amount = 49.00,
                category = "SUBSCRIPTION",
                paymentType = "RECURRING",
                timestamp = System.currentTimeMillis() - 86400000L * 5,
                roiRating = 4,
                collegeProfileId = "COLLEGE_2",
                semesterTag = "Semester 3"
            )
        )
        dao.insertExpense(
            ExpenseEntity(
                id = UUID.randomUUID().toString(),
                title = "GATE CS & DA Official Test Series Registration",
                amount = 35.00,
                category = "EXAM_REGISTRATION",
                paymentType = "ONETIME",
                timestamp = System.currentTimeMillis() - 86400000L * 2,
                roiRating = 5,
                collegeProfileId = "GENERAL",
                semesterTag = "Semester 4"
            )
        )
        dao.insertExpense(
            ExpenseEntity(
                id = UUID.randomUUID().toString(),
                title = "Operating System Concepts - Silberschatz Hardcover",
                amount = 85.50,
                category = "BOOKS",
                paymentType = "ONETIME",
                timestamp = System.currentTimeMillis() - 86400000L * 25,
                roiRating = 4,
                collegeProfileId = "COLLEGE_1",
                semesterTag = "Semester 3"
            )
        )

        // 2 & 3. Dual College Syllabus
        // Degree 1: B.Tech Computer Science (COLLEGE_1)
        val sem1Id = UUID.randomUUID().toString()
        val sem2Id = UUID.randomUUID().toString()
        dao.insertSemester(
            SemesterEntity(id = sem1Id, profileScope = "COLLEGE_1", semesterNumber = 3, isActive = true, gpaEarned = 9.2)
        )
        dao.insertSemester(
            SemesterEntity(id = sem2Id, profileScope = "COLLEGE_1", semesterNumber = 4, isActive = false, gpaEarned = 0.0)
        )

        val subOsId = UUID.randomUUID().toString()
        val subDbId = UUID.randomUUID().toString()
        val subAlgoId = UUID.randomUUID().toString()

        dao.insertSubject(SubjectEntity(id = subOsId, semesterId = sem1Id, subjectName = "Operating Systems", subjectCode = "CS301", credits = 4))
        dao.insertSubject(SubjectEntity(id = subDbId, semesterId = sem1Id, subjectName = "Database Management Systems", subjectCode = "CS302", credits = 4))
        dao.insertSubject(SubjectEntity(id = subAlgoId, semesterId = sem1Id, subjectName = "Design & Analysis of Algorithms", subjectCode = "CS303", credits = 4))

        dao.insertTopic(TopicEntity(id = UUID.randomUUID().toString(), subjectId = subOsId, topicTitle = "Process Synchronization & Semaphores", status = "DONE", weightage = "HARD", estimatedHours = 6))
        dao.insertTopic(TopicEntity(id = UUID.randomUUID().toString(), subjectId = subOsId, topicTitle = "Deadlock Avoidance & Banker's Algorithm", status = "REVISING", weightage = "MEDIUM", estimatedHours = 4))
        dao.insertTopic(TopicEntity(id = UUID.randomUUID().toString(), subjectId = subOsId, topicTitle = "Virtual Memory & Demand Paging", status = "DOING", weightage = "HARD", estimatedHours = 8))
        dao.insertTopic(TopicEntity(id = UUID.randomUUID().toString(), subjectId = subOsId, topicTitle = "File Systems & Disk Scheduling", status = "TODO", weightage = "EASY", estimatedHours = 3))

        dao.insertTopic(TopicEntity(id = UUID.randomUUID().toString(), subjectId = subDbId, topicTitle = "Relational Algebra & Tuple Calculus", status = "DONE", weightage = "MEDIUM", estimatedHours = 5))
        dao.insertTopic(TopicEntity(id = UUID.randomUUID().toString(), subjectId = subDbId, topicTitle = "Normalization (BCNF, 3NF, 4NF)", status = "DONE", weightage = "HARD", estimatedHours = 7))
        dao.insertTopic(TopicEntity(id = UUID.randomUUID().toString(), subjectId = subDbId, topicTitle = "Transaction ACID & Concurrency Control", status = "DOING", weightage = "HARD", estimatedHours = 6))

        dao.insertTopic(TopicEntity(id = UUID.randomUUID().toString(), subjectId = subAlgoId, topicTitle = "Divide & Conquer & Master Theorem", status = "DONE", weightage = "EASY", estimatedHours = 3))
        dao.insertTopic(TopicEntity(id = UUID.randomUUID().toString(), subjectId = subAlgoId, topicTitle = "Dynamic Programming & Memoization", status = "REVISING", weightage = "HARD", estimatedHours = 10))
        dao.insertTopic(TopicEntity(id = UUID.randomUUID().toString(), subjectId = subAlgoId, topicTitle = "Greedy Graph Algorithms (Dijkstra, Kruskal)", status = "DOING", weightage = "MEDIUM", estimatedHours = 5))

        // Degree 2: BS Data Science & AI (COLLEGE_2)
        val deg2SemId = UUID.randomUUID().toString()
        dao.insertSemester(
            SemesterEntity(id = deg2SemId, profileScope = "COLLEGE_2", semesterNumber = 2, isActive = true, gpaEarned = 8.8)
        )
        val subMlId = UUID.randomUUID().toString()
        val subStatsId = UUID.randomUUID().toString()
        dao.insertSubject(SubjectEntity(id = subMlId, semesterId = deg2SemId, subjectName = "Machine Learning Foundations", subjectCode = "DS201", credits = 4))
        dao.insertSubject(SubjectEntity(id = subStatsId, semesterId = deg2SemId, subjectName = "Statistical Inference & Probability", subjectCode = "DS202", credits = 3))

        dao.insertTopic(TopicEntity(id = UUID.randomUUID().toString(), subjectId = subMlId, topicTitle = "Linear Regression & Gradient Descent", status = "DONE", weightage = "EASY", estimatedHours = 4))
        dao.insertTopic(TopicEntity(id = UUID.randomUUID().toString(), subjectId = subMlId, topicTitle = "Logistic Regression & ROC Curves", status = "DONE", weightage = "MEDIUM", estimatedHours = 5))
        dao.insertTopic(TopicEntity(id = UUID.randomUUID().toString(), subjectId = subMlId, topicTitle = "Support Vector Machines & Kernels", status = "DOING", weightage = "HARD", estimatedHours = 8))
        dao.insertTopic(TopicEntity(id = UUID.randomUUID().toString(), subjectId = subStatsId, topicTitle = "Hypothesis Testing & p-values", status = "REVISING", weightage = "HARD", estimatedHours = 6))

        // 4. Dual-Degree Results & Formula Engine
        val degProfile1Id = "DEG_1"
        val degProfile2Id = "DEG_2"
        dao.insertDegreeProfile(
            DegreeProfileEntity(id = degProfile1Id, degreeName = "B.Tech Computer Science", institutionName = "National Institute of Technology", calculationFormulaString = "(CGPA * 9.5)")
        )
        dao.insertDegreeProfile(
            DegreeProfileEntity(id = degProfile2Id, degreeName = "BS Data Science & AI", institutionName = "IIT Madras Online Program", calculationFormulaString = "(CGPA * 10)")
        )

        dao.insertSemesterGrade(SemesterGradeEntity(id = UUID.randomUUID().toString(), degreeProfileId = degProfile1Id, semesterNumber = 1, sgpa = 9.1, totalCreditsEarned = 22))
        dao.insertSemesterGrade(SemesterGradeEntity(id = UUID.randomUUID().toString(), degreeProfileId = degProfile1Id, semesterNumber = 2, sgpa = 9.3, totalCreditsEarned = 24))
        dao.insertSemesterGrade(SemesterGradeEntity(id = UUID.randomUUID().toString(), degreeProfileId = degProfile2Id, semesterNumber = 1, sgpa = 8.8, totalCreditsEarned = 18))

        dao.insertSubjectMark(SubjectMarkEntity(id = UUID.randomUUID().toString(), degreeProfileId = degProfile1Id, semesterNumber = 2, subjectName = "Data Structures", marksObtained = 94.0, maxMarks = 100.0, gradePoint = 10.0, credits = 4))
        dao.insertSubjectMark(SubjectMarkEntity(id = UUID.randomUUID().toString(), degreeProfileId = degProfile1Id, semesterNumber = 2, subjectName = "Discrete Mathematics", marksObtained = 88.0, maxMarks = 100.0, gradePoint = 9.0, credits = 4))
        dao.insertSubjectMark(SubjectMarkEntity(id = UUID.randomUUID().toString(), degreeProfileId = degProfile1Id, semesterNumber = 2, subjectName = "Digital Logic", marksObtained = 90.0, maxMarks = 100.0, gradePoint = 9.0, credits = 3))

        // 5. Triple-Paper GATE Syllabus Tracker
        val gatePaper1Id = "GATE_CS"
        val gatePaper2Id = "GATE_DA"
        val gatePaper3Id = "GATE_MA"
        dao.insertGatePaper(GatePaperEntity(id = gatePaper1Id, paperCode = "CS", paperName = "Computer Science & Information Technology"))
        dao.insertGatePaper(GatePaperEntity(id = gatePaper2Id, paperCode = "DA", paperName = "Data Science & Artificial Intelligence"))
        dao.insertGatePaper(GatePaperEntity(id = gatePaper3Id, paperCode = "MA", paperName = "Engineering Mathematics & Stats"))

        dao.insertGateTopic(
            GateTopicEntity(
                id = UUID.randomUUID().toString(),
                paperId = gatePaper1Id,
                subjectCategory = "Theory of Computation",
                topicName = "Regular Expressions & DFA/NFA Equivalence",
                status = "REVISING",
                startDate = "12 Oct",
                endDate = "18 Oct",
                revisionCount = 3,
                confidenceLevel = 80
            )
        )
        dao.insertGateTopic(
            GateTopicEntity(
                id = UUID.randomUUID().toString(),
                paperId = gatePaper1Id,
                subjectCategory = "Theory of Computation",
                topicName = "Turing Machines & Decidability (Halting Problem)",
                status = "DOING",
                startDate = "19 Oct",
                endDate = "25 Oct",
                revisionCount = 1,
                confidenceLevel = 45 // weak topic flag
            )
        )
        dao.insertGateTopic(
            GateTopicEntity(
                id = UUID.randomUUID().toString(),
                paperId = gatePaper1Id,
                subjectCategory = "Computer Networks",
                topicName = "TCP Congestion Control & Window Scaling",
                status = "REVISING",
                startDate = "01 Oct",
                endDate = "08 Oct",
                revisionCount = 2,
                confidenceLevel = 75
            )
        )
        dao.insertGateTopic(
            GateTopicEntity(
                id = UUID.randomUUID().toString(),
                paperId = gatePaper2Id,
                subjectCategory = "Artificial Intelligence",
                topicName = "Search Algorithms (A*, Heuristic Consistency)",
                status = "DONE",
                startDate = "05 Oct",
                endDate = "10 Oct",
                revisionCount = 4,
                confidenceLevel = 90
            )
        )
        dao.insertGateTopic(
            GateTopicEntity(
                id = UUID.randomUUID().toString(),
                paperId = gatePaper2Id,
                subjectCategory = "Machine Learning",
                topicName = "Neural Networks Backpropagation Calculus",
                status = "DOING",
                startDate = "15 Oct",
                endDate = "22 Oct",
                revisionCount = 1,
                confidenceLevel = 50
            )
        )

        // 6. ESE UPSC Syllabus Tracker
        val sec1Id = UUID.randomUUID().toString()
        val sec2Id = UUID.randomUUID().toString()
        val sec3Id = UUID.randomUUID().toString()
        dao.insertEseSection(EseSectionEntity(id = sec1Id, stageType = "PRELIMS_GS", paperTitle = "General Studies & Engineering Aptitude"))
        dao.insertEseSection(EseSectionEntity(id = sec2Id, stageType = "PRELIMS_TECH", paperTitle = "Paper-II Technical Objectives (Electronics/Telecom/CS)"))
        dao.insertEseSection(EseSectionEntity(id = sec3Id, stageType = "MAINS_TECH", paperTitle = "Stage 2: Mains Conventional Technical Paper"))

        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), sectionId = sec1Id, topicName = "Standards and Quality Practices in Production & Services", status = "DONE", pyqSolvedCount = 45, notesAttachedFlag = true))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), sectionId = sec1Id, topicName = "Basics of Energy and Environment: Conservation & Pollution", status = "TODO", pyqSolvedCount = 18, notesAttachedFlag = false))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), sectionId = sec1Id, topicName = "Information and Communication Technologies (ICT)", status = "DONE", pyqSolvedCount = 60, notesAttachedFlag = true))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), sectionId = sec2Id, topicName = "Basic Electrical Engineering & Network Theory", status = "TODO", pyqSolvedCount = 35, notesAttachedFlag = true))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), sectionId = sec3Id, topicName = "Conventional Microprocessors and Microcontrollers Architecture", status = "TODO", pyqSolvedCount = 12, notesAttachedFlag = false))

        // 7. Digital Certificates Hub
        dao.insertCertificate(
            CertificateEntity(
                id = UUID.randomUUID().toString(),
                certificateName = "AWS Certified Solutions Architect - Associate",
                status = "WANTED",
                targetAcquisitionDate = "2026-12-15",
                issuer = "Amazon Web Services",
                tags = "Cloud, Architecture"
            )
        )
        dao.insertCertificate(
            CertificateEntity(
                id = UUID.randomUUID().toString(),
                certificateName = "TensorFlow Developer Professional Certificate",
                status = "WANTED",
                targetAcquisitionDate = "2027-02-28",
                issuer = "DeepLearning.AI",
                tags = "AI, Deep Learning"
            )
        )
        dao.insertCertificate(
            CertificateEntity(
                id = UUID.randomUUID().toString(),
                certificateName = "Google Associate Cloud Engineer",
                status = "OWNED",
                filePath = "/data/user/0/com.aistudio.academichub.vkhdza/files/certificates/gcp_ace.pdf",
                targetAcquisitionDate = "2026-08-20",
                issuer = "Google Cloud",
                tags = "Cloud, DevOps"
            )
        )
        dao.insertCertificate(
            CertificateEntity(
                id = UUID.randomUUID().toString(),
                certificateName = "Algorithms Specialization - Stanford",
                status = "OWNED",
                filePath = "/data/user/0/com.aistudio.academichub.vkhdza/files/certificates/stanford_algo.pdf",
                targetAcquisitionDate = "2026-05-10",
                issuer = "Stanford University / Coursera",
                tags = "DSA, Algorithms"
            )
        )

        // 8. Timetable Configurations
        dao.insertTimetableConfig(TimetableConfigEntity(id = UUID.randomUUID().toString(), scheduleType = "WORKING_DAY", timeSlotStart = "07:00", timeSlotEnd = "08:30", slotTitle = "Morning Formula & Weak Topic Revision", associatedModuleId = "GATE"))
        dao.insertTimetableConfig(TimetableConfigEntity(id = UUID.randomUUID().toString(), scheduleType = "WORKING_DAY", timeSlotStart = "09:00", timeSlotEnd = "13:00", slotTitle = "B.Tech College Core Lectures & Systems Lab", associatedModuleId = "COLLEGE_1"))
        dao.insertTimetableConfig(TimetableConfigEntity(id = UUID.randomUUID().toString(), scheduleType = "WORKING_DAY", timeSlotStart = "14:30", timeSlotEnd = "17:00", slotTitle = "GATE Mock Test Practice & PYQ Solving", associatedModuleId = "GATE"))
        dao.insertTimetableConfig(TimetableConfigEntity(id = UUID.randomUUID().toString(), scheduleType = "WORKING_DAY", timeSlotStart = "18:00", timeSlotEnd = "19:00", slotTitle = "Exercise / Outdoor Walk & Mindful Reset", associatedModuleId = null))
        dao.insertTimetableConfig(TimetableConfigEntity(id = UUID.randomUUID().toString(), scheduleType = "WORKING_DAY", timeSlotStart = "20:00", timeSlotEnd = "22:30", slotTitle = "BS Data Science AI Project Implementation", associatedModuleId = "COLLEGE_2"))

        dao.insertTimetableConfig(TimetableConfigEntity(id = UUID.randomUUID().toString(), scheduleType = "HOLIDAY", timeSlotStart = "08:00", timeSlotEnd = "11:00", slotTitle = "Deep Focus: ESE General Studies & Current Tech Standards", associatedModuleId = "ESE"))
        dao.insertTimetableConfig(TimetableConfigEntity(id = UUID.randomUUID().toString(), scheduleType = "HOLIDAY", timeSlotStart = "11:30", timeSlotEnd = "13:30", slotTitle = "Upskilling & Kaggle Machine Learning Exploration", associatedModuleId = "UPSKILLING"))
        dao.insertTimetableConfig(TimetableConfigEntity(id = UUID.randomUUID().toString(), scheduleType = "HOLIDAY", timeSlotStart = "15:00", timeSlotEnd = "18:00", slotTitle = "Full Length 3-Hour GATE Mock Paper Simulation", associatedModuleId = "GATE"))
        dao.insertTimetableConfig(TimetableConfigEntity(id = UUID.randomUUID().toString(), scheduleType = "HOLIDAY", timeSlotStart = "19:30", timeSlotEnd = "21:30", slotTitle = "Open Source Contributions & Resume Portfolio Polish", associatedModuleId = "RESUME"))

        // Daily Slot Logs for today
        dao.insertDailySlotLog(DailySlotLogEntity(id = UUID.randomUUID().toString(), dateString = todayStr, scheduleTypeUsed = "WORKING_DAY", slotTitle = "Morning Formula & Weak Topic Revision", isTickedDone = true))
        dao.insertDailySlotLog(DailySlotLogEntity(id = UUID.randomUUID().toString(), dateString = todayStr, scheduleTypeUsed = "WORKING_DAY", slotTitle = "B.Tech College Core Lectures & Systems Lab", isTickedDone = true))
        dao.insertDailySlotLog(DailySlotLogEntity(id = UUID.randomUUID().toString(), dateString = todayStr, scheduleTypeUsed = "WORKING_DAY", slotTitle = "GATE Mock Test Practice & PYQ Solving", isTickedDone = false))

        // 9. Front Page Priority Notepad
        dao.saveScratchpad(
            DailyScratchpadEntity(
                dateString = todayStr,
                rawMarkdownText = """
# Priority Focus for Today
- [x] Review Operating Systems Semaphores & Deadlock avoidance
- [x] Complete BS Data Science Module 3 Quiz
- [ ] Solve 25 GATE CS Previous Year Questions on Paging
- [ ] Submit Semester 3 Major Project Synopsis
- [ ] Practice 2 ESE GS mock questions on Quality Standards
- Remember: Consistency beats intensity. Keep the revision loop going!
                """.trimIndent(),
                layoutPinnedFlag = true
            )
        )

        // 10. Academic Milestones (24h Alert Engine)
        val now = System.currentTimeMillis()
        dao.insertMilestone(
            AcademicMilestoneEntity(
                id = UUID.randomUUID().toString(),
                eventTitle = "Operating Systems Midterm Written Exam",
                eventDescription = "Covers Processes, Threads, CPU Scheduling, and Semaphores. Room 402.",
                targetTimestamp = now + 86400000L * 1 - 3600000L * 2, // ~22 hours from now!
                alertTriggeredFlag = false,
                collegeProfileId = "COLLEGE_1"
            )
        )
        dao.insertMilestone(
            AcademicMilestoneEntity(
                id = UUID.randomUUID().toString(),
                eventTitle = "BS Data Science Capstone Proposal Submission",
                eventDescription = "Finalize problem statement and dataset pipeline on GitHub portal.",
                targetTimestamp = now + 86400000L * 4,
                alertTriggeredFlag = false,
                collegeProfileId = "COLLEGE_2"
            )
        )
        dao.insertMilestone(
            AcademicMilestoneEntity(
                id = UUID.randomUUID().toString(),
                eventTitle = "GATE National Mock Exam Registration Deadline",
                eventDescription = "Verify center preferences and upload updated identity proofs.",
                targetTimestamp = now + 86400000L * 10,
                alertTriggeredFlag = false,
                collegeProfileId = "GATE"
            )
        )

        // 11. Future Upskilling Checklist
        dao.insertFutureCourse(
            FutureCourseEntity(
                id = UUID.randomUUID().toString(),
                courseName = "Distributed Systems by MIT (6.824 / Raft Consensus)",
                timelineWhen = "Winter Break (Dec 2026 - Jan 2027)",
                motivationWhy = "Master high-throughput fault-tolerant backend architectures for Tier-1 systems roles.",
                costEstimate = 0.0,
                preRequisites = "Go/Rust, OS Concurrency, Networks",
                isCompleted = false
            )
        )
        dao.insertFutureCourse(
            FutureCourseEntity(
                id = UUID.randomUUID().toString(),
                courseName = "Deep Learning Specialization - Andrew Ng",
                timelineWhen = "Semester 4 Summer (May 2027)",
                motivationWhy = "Strengthen mathematical intuitions behind transformers, LLMs, and vision models.",
                costEstimate = 49.0,
                preRequisites = "Linear Algebra, Python, Basic PyTorch",
                isCompleted = false
            )
        )
        dao.insertFutureCourse(
            FutureCourseEntity(
                id = UUID.randomUUID().toString(),
                courseName = "Database Internals: Storage Engines & B-Trees",
                timelineWhen = "Completed (Aug 2026)",
                motivationWhy = "Understand LSM trees vs B+Trees for low-latency database engines.",
                costEstimate = 35.0,
                preRequisites = "C++, File Systems",
                isCompleted = true
            )
        )

        // 12. Future Career Options Strategy Matrix
        dao.insertCareerOption(
            CareerOptionEntity(
                id = UUID.randomUUID().toString(),
                title = "AI Systems & Infrastructure Engineer",
                feasibilityScore = 9,
                riskLevel = "LOW",
                targetRole = "Senior Systems Engineer / AI Platform",
                salaryRange = "\$140k - \$190k / ₹28L - ₹45L",
                actionStepsText = "1. Maintain >9.0 CGPA in both degrees.\n2. Complete MIT 6.824 Raft lab.\n3. Publish technical blog on GPU memory kernel optimizations.\n4. Build and benchmark open-source distributed cache in Go."
            )
        )
        dao.insertCareerOption(
            CareerOptionEntity(
                id = UUID.randomUUID().toString(),
                title = "Indian Engineering Services (IES / UPSC ESE)",
                feasibilityScore = 7,
                riskLevel = "MED",
                targetRole = "Assistant Executive Engineer (Group A Gazetted)",
                salaryRange = "Pay Level 10 (Govt Scale + HRA/DA + Quarters)",
                actionStepsText = "1. Finish Stage 1 General Studies syllabus by November.\n2. Solve last 15 years conventional questions.\n3. Daily 2 hours technical revisions."
            )
        )
        dao.insertCareerOption(
            CareerOptionEntity(
                id = UUID.randomUUID().toString(),
                title = "Doctoral PhD in AI & Autonomous Computing",
                feasibilityScore = 8,
                riskLevel = "MED",
                targetRole = "Research Scientist / Academic Professor",
                salaryRange = "Fully Funded Fellowship + \$35k Stipend",
                actionStepsText = "1. Score top 0.5% in GATE CS.\n2. Author research paper with university advisor.\n3. Secure 3 strong recommendation letters."
            )
        )

        // 13. Local Tabular Resume Profile
        dao.saveResumeProfile(
            ResumeProfileEntity(
                id = "DEFAULT_PROFILE",
                profileName = "Academic & Technical CV",
                fullName = "Vaibhav Jain",
                email = "vaibhav.academic@hub.edu",
                phone = "+91 98765 43210",
                githubUrl = "github.com/vaibhav-academic",
                linkedinUrl = "linkedin.com/in/vaibhav-jain",
                summary = "Dual-Degree Honors candidate in Computer Science & Engineering and Data Science. Passionate about Distributed Systems, Database Internals, and AI Algorithm Optimization with proven academic distinction.",
                educationJson = "National Institute of Technology | B.Tech CSE (CGPA: 9.25/10.0) | 2024-2028\nIIT Madras | BS Data Science & AI (GPA: 8.8/10.0) | 2024-2028",
                projectsJson = "Mini-Raft Distributed KV Store: Built fault-tolerant consensus engine with leader election and log replication.\nNeural Compiler Pass: LLVM optimization pass lowering tensor computations for custom hardware accelerators.",
                skillsJson = "Languages: Kotlin, C++, Python, Go, SQL | Systems: Linux, Docker, Room SQLite, Jetpack Compose | Core: OS, DBMS, Algorithms, ML Foundations",
                experienceJson = "Undergraduate Research Assistant - High Performance Computing Lab (NIT)\nStudent Academic Peer Mentor - Department of Computer Science"
            )
        )
    }
}
