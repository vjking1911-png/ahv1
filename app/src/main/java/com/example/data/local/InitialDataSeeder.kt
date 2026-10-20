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
                amount = 3499.00,
                category = "Instruments & Devices",
                paymentType = "ONETIME",
                timestamp = System.currentTimeMillis() - 86400000L * 12,
                roiRating = 5,
                collegeProfileId = "COLLEGE_1",
                semesterTag = "Instruments & Devices"
            )
        )
        dao.insertExpense(
            ExpenseEntity(
                id = UUID.randomUUID().toString(),
                title = "Operating System Concepts - Silberschatz Edition",
                amount = 1850.00,
                category = "Academic",
                paymentType = "ONETIME",
                timestamp = System.currentTimeMillis() - 86400000L * 25,
                roiRating = 4,
                collegeProfileId = "COLLEGE_1",
                semesterTag = "Academic"
            )
        )
        dao.insertExpense(
            ExpenseEntity(
                id = UUID.randomUUID().toString(),
                title = "AWS Cloud Solutions Architect Exam Voucher",
                amount = 9800.00,
                category = "Certification",
                paymentType = "ONETIME",
                timestamp = System.currentTimeMillis() - 86400000L * 18,
                roiRating = 5,
                collegeProfileId = "GENERAL",
                semesterTag = "Certification"
            )
        )
        dao.insertExpense(
            ExpenseEntity(
                id = UUID.randomUUID().toString(),
                title = "Distributed Systems & Cloud Architecture Bootcamp",
                amount = 4500.00,
                category = "Skills",
                paymentType = "ONETIME",
                timestamp = System.currentTimeMillis() - 86400000L * 8,
                roiRating = 5,
                collegeProfileId = "COLLEGE_2",
                semesterTag = "Skills"
            )
        )
        dao.insertExpense(
            ExpenseEntity(
                id = UUID.randomUUID().toString(),
                title = "GATE CS & DA Official Test Series Registration",
                amount = 2200.00,
                category = "Exam Fees",
                paymentType = "ONETIME",
                timestamp = System.currentTimeMillis() - 86400000L * 2,
                roiRating = 5,
                collegeProfileId = "GENERAL",
                semesterTag = "Exam Fees"
            )
        )
        dao.insertExpense(
            ExpenseEntity(
                id = UUID.randomUUID().toString(),
                title = "IEEE Student Membership & Digital Library",
                amount = 1500.00,
                category = "Other",
                paymentType = "RECURRING",
                timestamp = System.currentTimeMillis() - 86400000L * 5,
                roiRating = 4,
                collegeProfileId = "GENERAL",
                semesterTag = "Other"
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
            DegreeProfileEntity(id = degProfile1Id, degreeName = "B.Tech Computer Science", institutionName = "National Institute of Technology", calculationFormulaString = "(CGPA * 9.5)", totalTargetCredits = 160)
        )
        dao.insertDegreeProfile(
            DegreeProfileEntity(id = degProfile2Id, degreeName = "BS Data Science & AI", institutionName = "IIT Madras Online Program", calculationFormulaString = "(CGPA * 10)", totalTargetCredits = 120)
        )

        // Seed Degree 1 - Semester 1
        val sub1Id = UUID.randomUUID().toString()
        val sub2Id = UUID.randomUUID().toString()
        val sub3Id = UUID.randomUUID().toString()
        dao.insertSubjectMark(SubjectMarkEntity(id = sub1Id, degreeProfileId = degProfile1Id, semesterNumber = 1, subjectName = "Calculus & Linear Algebra", marksObtained = 92.0, maxMarks = 100.0, gradePoint = 10.0, credits = 4, subjectCode = "MA101"))
        dao.insertSubjectMark(SubjectMarkEntity(id = sub2Id, degreeProfileId = degProfile1Id, semesterNumber = 1, subjectName = "Engineering Physics & Lab", marksObtained = 88.0, maxMarks = 100.0, gradePoint = 9.0, credits = 4, subjectCode = "PH101"))
        dao.insertSubjectMark(SubjectMarkEntity(id = sub3Id, degreeProfileId = degProfile1Id, semesterNumber = 1, subjectName = "Programming in C", marksObtained = 95.0, maxMarks = 100.0, gradePoint = 10.0, credits = 4, subjectCode = "CS101"))

        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub1Id, examName = "Internal Assessment 1", weightagePercent = 20.0, marksObtained = 19.0, maxMarks = 20.0))
        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub1Id, examName = "Mid-Term Examination", weightagePercent = 30.0, marksObtained = 27.0, maxMarks = 30.0))
        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub1Id, examName = "End-Semester Examination", weightagePercent = 50.0, marksObtained = 46.0, maxMarks = 50.0))

        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub2Id, examName = "Lab Practicals & Viva", weightagePercent = 20.0, marksObtained = 18.0, maxMarks = 20.0))
        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub2Id, examName = "Mid-Term Examination", weightagePercent = 30.0, marksObtained = 26.0, maxMarks = 30.0))
        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub2Id, examName = "End-Semester Examination", weightagePercent = 50.0, marksObtained = 44.0, maxMarks = 50.0))

        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub3Id, examName = "Coding Assignments", weightagePercent = 20.0, marksObtained = 20.0, maxMarks = 20.0))
        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub3Id, examName = "Mid-Term Examination", weightagePercent = 30.0, marksObtained = 28.0, maxMarks = 30.0))
        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub3Id, examName = "End-Semester Examination", weightagePercent = 50.0, marksObtained = 47.0, maxMarks = 50.0))

        dao.insertSemesterGrade(SemesterGradeEntity(id = UUID.randomUUID().toString(), degreeProfileId = degProfile1Id, semesterNumber = 1, sgpa = 9.67, totalCreditsEarned = 12))

        // Seed Degree 1 - Semester 2
        val sub4Id = UUID.randomUUID().toString()
        val sub5Id = UUID.randomUUID().toString()
        val sub6Id = UUID.randomUUID().toString()
        dao.insertSubjectMark(SubjectMarkEntity(id = sub4Id, degreeProfileId = degProfile1Id, semesterNumber = 2, subjectName = "Data Structures & Algorithms", marksObtained = 94.0, maxMarks = 100.0, gradePoint = 10.0, credits = 4, subjectCode = "CS201"))
        dao.insertSubjectMark(SubjectMarkEntity(id = sub5Id, degreeProfileId = degProfile1Id, semesterNumber = 2, subjectName = "Discrete Mathematics", marksObtained = 88.0, maxMarks = 100.0, gradePoint = 9.0, credits = 4, subjectCode = "MA201"))
        dao.insertSubjectMark(SubjectMarkEntity(id = sub6Id, degreeProfileId = degProfile1Id, semesterNumber = 2, subjectName = "Digital Logic Design", marksObtained = 90.0, maxMarks = 100.0, gradePoint = 9.0, credits = 3, subjectCode = "EC201"))

        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub4Id, examName = "Continuous Assessment", weightagePercent = 20.0, marksObtained = 19.0, maxMarks = 20.0))
        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub4Id, examName = "Mid-Term Test", weightagePercent = 30.0, marksObtained = 28.0, maxMarks = 30.0))
        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub4Id, examName = "End-Semester Final", weightagePercent = 50.0, marksObtained = 47.0, maxMarks = 50.0))

        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub5Id, examName = "Class Test & Quizzes", weightagePercent = 20.0, marksObtained = 17.0, maxMarks = 20.0))
        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub5Id, examName = "Midterm Examination", weightagePercent = 30.0, marksObtained = 26.0, maxMarks = 30.0))
        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub5Id, examName = "End-Semester Final", weightagePercent = 50.0, marksObtained = 45.0, maxMarks = 50.0))

        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub6Id, examName = "Lab Simulation & Viva", weightagePercent = 25.0, marksObtained = 24.0, maxMarks = 25.0))
        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub6Id, examName = "Mid-Semester Exam", weightagePercent = 25.0, marksObtained = 22.0, maxMarks = 25.0))
        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub6Id, examName = "End-Semester Theory", weightagePercent = 50.0, marksObtained = 44.0, maxMarks = 50.0))

        dao.insertSemesterGrade(SemesterGradeEntity(id = UUID.randomUUID().toString(), degreeProfileId = degProfile1Id, semesterNumber = 2, sgpa = 9.36, totalCreditsEarned = 11))

        // Seed Degree 2 - Semester 1
        val sub7Id = UUID.randomUUID().toString()
        val sub8Id = UUID.randomUUID().toString()
        dao.insertSubjectMark(SubjectMarkEntity(id = sub7Id, degreeProfileId = degProfile2Id, semesterNumber = 1, subjectName = "Foundations of Data Science", marksObtained = 88.0, maxMarks = 100.0, gradePoint = 9.0, credits = 4, subjectCode = "DS101"))
        dao.insertSubjectMark(SubjectMarkEntity(id = sub8Id, degreeProfileId = degProfile2Id, semesterNumber = 1, subjectName = "Linear Algebra & Probability", marksObtained = 86.0, maxMarks = 100.0, gradePoint = 9.0, credits = 4, subjectCode = "MA102"))

        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub7Id, examName = "Python Project & Lab", weightagePercent = 30.0, marksObtained = 28.0, maxMarks = 30.0))
        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub7Id, examName = "Mid-Term Examination", weightagePercent = 20.0, marksObtained = 18.0, maxMarks = 20.0))
        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub7Id, examName = "End-Semester Final", weightagePercent = 50.0, marksObtained = 42.0, maxMarks = 50.0))

        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub8Id, examName = "Weekly Assignments", weightagePercent = 20.0, marksObtained = 18.0, maxMarks = 20.0))
        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub8Id, examName = "Mid-Semester Exam", weightagePercent = 30.0, marksObtained = 25.0, maxMarks = 30.0))
        dao.insertSubjectExam(SubjectExamEntity(id = UUID.randomUUID().toString(), subjectMarkId = sub8Id, examName = "End-Semester Exam", weightagePercent = 50.0, marksObtained = 43.0, maxMarks = 50.0))

        dao.insertSemesterGrade(SemesterGradeEntity(id = UUID.randomUUID().toString(), degreeProfileId = degProfile2Id, semesterNumber = 1, sgpa = 9.00, totalCreditsEarned = 8))

        // 5. Triple-Paper GATE Syllabus Tracker
        val gatePaper1Id = "GATE_CS"
        val gatePaper2Id = "GATE_DA"
        val gatePaper3Id = "GATE_MA"
        dao.insertGatePaper(GatePaperEntity(id = gatePaper1Id, paperCode = "CS", paperName = "Computer Science & Information Technology"))
        dao.insertGatePaper(GatePaperEntity(id = gatePaper2Id, paperCode = "DA", paperName = "Data Science & Artificial Intelligence"))
        dao.insertGatePaper(GatePaperEntity(id = gatePaper3Id, paperCode = "MA", paperName = "Engineering Mathematics & Stats"))

        // GATE CS Subjects
        val csSub1Id = UUID.randomUUID().toString()
        val csSub2Id = UUID.randomUUID().toString()
        val csSub3Id = UUID.randomUUID().toString()
        dao.insertGateSubject(GateSubjectEntity(id = csSub1Id, paperId = gatePaper1Id, subjectName = "Theory of Computation", subjectCode = "CS-TOC", weightageMarks = 8))
        dao.insertGateSubject(GateSubjectEntity(id = csSub2Id, paperId = gatePaper1Id, subjectName = "Operating Systems", subjectCode = "CS-OS", weightageMarks = 9))
        dao.insertGateSubject(GateSubjectEntity(id = csSub3Id, paperId = gatePaper1Id, subjectName = "Computer Networks", subjectCode = "CS-CN", weightageMarks = 8))

        // GATE CS Sub-Topics
        dao.insertGateTopic(GateTopicEntity(id = UUID.randomUUID().toString(), paperId = gatePaper1Id, subjectId = csSub1Id, subjectCategory = "Theory of Computation", topicName = "Regular Expressions & DFA/NFA Equivalence", status = "REVISING", weightage = "HARD", estimatedHours = 6, revisionCount = 3, confidenceLevel = 80))
        dao.insertGateTopic(GateTopicEntity(id = UUID.randomUUID().toString(), paperId = gatePaper1Id, subjectId = csSub1Id, subjectCategory = "Theory of Computation", topicName = "Context-Free Grammars & Pushdown Automata", status = "DONE", weightage = "MEDIUM", estimatedHours = 5, revisionCount = 2, confidenceLevel = 85))
        dao.insertGateTopic(GateTopicEntity(id = UUID.randomUUID().toString(), paperId = gatePaper1Id, subjectId = csSub1Id, subjectCategory = "Theory of Computation", topicName = "Turing Machines & Decidability (Halting Problem)", status = "DOING", weightage = "HARD", estimatedHours = 8, revisionCount = 1, confidenceLevel = 45))

        dao.insertGateTopic(GateTopicEntity(id = UUID.randomUUID().toString(), paperId = gatePaper1Id, subjectId = csSub2Id, subjectCategory = "Operating Systems", topicName = "Process Synchronization & Semaphores", status = "DONE", weightage = "HARD", estimatedHours = 6, revisionCount = 3, confidenceLevel = 90))
        dao.insertGateTopic(GateTopicEntity(id = UUID.randomUUID().toString(), paperId = gatePaper1Id, subjectId = csSub2Id, subjectCategory = "Operating Systems", topicName = "Deadlock Avoidance & Banker's Algorithm", status = "REVISING", weightage = "MEDIUM", estimatedHours = 4, revisionCount = 2, confidenceLevel = 75))
        dao.insertGateTopic(GateTopicEntity(id = UUID.randomUUID().toString(), paperId = gatePaper1Id, subjectId = csSub2Id, subjectCategory = "Operating Systems", topicName = "Virtual Memory & Demand Paging", status = "DOING", weightage = "HARD", estimatedHours = 7, revisionCount = 1, confidenceLevel = 55))

        dao.insertGateTopic(GateTopicEntity(id = UUID.randomUUID().toString(), paperId = gatePaper1Id, subjectId = csSub3Id, subjectCategory = "Computer Networks", topicName = "TCP Congestion Control & Window Scaling", status = "REVISING", weightage = "HARD", estimatedHours = 5, revisionCount = 2, confidenceLevel = 75))
        dao.insertGateTopic(GateTopicEntity(id = UUID.randomUUID().toString(), paperId = gatePaper1Id, subjectId = csSub3Id, subjectCategory = "Computer Networks", topicName = "IP Addressing, Subnetting & CIDR", status = "DONE", weightage = "EASY", estimatedHours = 3, revisionCount = 4, confidenceLevel = 95))

        // GATE DA Subjects
        val daSub1Id = UUID.randomUUID().toString()
        val daSub2Id = UUID.randomUUID().toString()
        dao.insertGateSubject(GateSubjectEntity(id = daSub1Id, paperId = gatePaper2Id, subjectName = "Artificial Intelligence", subjectCode = "DA-AI", weightageMarks = 10))
        dao.insertGateSubject(GateSubjectEntity(id = daSub2Id, paperId = gatePaper2Id, subjectName = "Machine Learning", subjectCode = "DA-ML", weightageMarks = 12))

        // GATE DA Sub-Topics
        dao.insertGateTopic(GateTopicEntity(id = UUID.randomUUID().toString(), paperId = gatePaper2Id, subjectId = daSub1Id, subjectCategory = "Artificial Intelligence", topicName = "Search Algorithms (A*, Heuristic Consistency)", status = "DONE", weightage = "MEDIUM", estimatedHours = 5, revisionCount = 4, confidenceLevel = 90))
        dao.insertGateTopic(GateTopicEntity(id = UUID.randomUUID().toString(), paperId = gatePaper2Id, subjectId = daSub1Id, subjectCategory = "Artificial Intelligence", topicName = "Adversarial Search & Minimax Alpha-Beta", status = "REVISING", weightage = "HARD", estimatedHours = 6, revisionCount = 2, confidenceLevel = 70))

        dao.insertGateTopic(GateTopicEntity(id = UUID.randomUUID().toString(), paperId = gatePaper2Id, subjectId = daSub2Id, subjectCategory = "Machine Learning", topicName = "Neural Networks Backpropagation Calculus", status = "DOING", weightage = "HARD", estimatedHours = 8, revisionCount = 1, confidenceLevel = 50))
        dao.insertGateTopic(GateTopicEntity(id = UUID.randomUUID().toString(), paperId = gatePaper2Id, subjectId = daSub2Id, subjectCategory = "Machine Learning", topicName = "Support Vector Machines & Kernel Trick", status = "TODO", weightage = "HARD", estimatedHours = 7, revisionCount = 0, confidenceLevel = 45))

        // GATE MA Subjects
        val maSub1Id = UUID.randomUUID().toString()
        dao.insertGateSubject(GateSubjectEntity(id = maSub1Id, paperId = gatePaper3Id, subjectName = "Linear Algebra", subjectCode = "MA-LA", weightageMarks = 8))

        // GATE MA Sub-Topics
        dao.insertGateTopic(GateTopicEntity(id = UUID.randomUUID().toString(), paperId = gatePaper3Id, subjectId = maSub1Id, subjectCategory = "Linear Algebra", topicName = "Eigenvalues, Eigenvectors & Cayley-Hamilton", status = "DONE", weightage = "MEDIUM", estimatedHours = 5, revisionCount = 3, confidenceLevel = 85))
        dao.insertGateTopic(GateTopicEntity(id = UUID.randomUUID().toString(), paperId = gatePaper3Id, subjectId = maSub1Id, subjectCategory = "Linear Algebra", topicName = "Vector Spaces, Basis & Dimension", status = "REVISING", weightage = "HARD", estimatedHours = 6, revisionCount = 2, confidenceLevel = 70))

        // 6. ESE UPSC Syllabus Tracker (Electronics & Telecommunication)
        seedEseEtData(dao)

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

    suspend fun seedEseEtData(dao: AcademicDao) {
        // Compatibility sections for legacy code
        val sec1Id = "ESE_SEC_GS"
        val sec2Id = "ESE_SEC_ET1"
        val sec3Id = "ESE_SEC_MAINS"
        dao.insertEseSection(EseSectionEntity(id = sec1Id, stageType = "PRELIMS_GS", paperTitle = "General Studies & Engineering Aptitude"))
        dao.insertEseSection(EseSectionEntity(id = sec2Id, stageType = "PRELIMS_TECH", paperTitle = "Paper-II E&T Technical (Electronics & Telecom)"))
        dao.insertEseSection(EseSectionEntity(id = sec3Id, stageType = "MAINS_TECH", paperTitle = "Stage 2: Mains Conventional Technical Papers"))

        // --- PAPER 1: ESE ET Paper 1 (Circuits, Devices & Materials) ---
        val paper1Id = "ESE_ET_P1"
        dao.insertEsePaper(
            EsePaperEntity(
                id = paper1Id,
                paperCode = "ET-1",
                paperName = "Paper-I: Circuits, Devices & Materials",
                stageType = "PRELIMS_TECH",
                totalMarks = 300,
                targetScore = 210,
                displayOrder = 1
            )
        )

        // ET-1 Subject: Basic Electronics Engineering
        val subBeeId = "ET_SUB_BEE"
        dao.insertEseSubject(EseSubjectEntity(id = subBeeId, paperId = paper1Id, subjectName = "Basic Electronics Engineering", subjectCode = "ET-BEE", weightageMarks = 45, displayOrder = 1))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subBeeId, sectionId = sec2Id, subjectCategory = "Basic Electronics Engineering", topicName = "Semiconductor Physics & PN Junction Diode Characteristics", status = "DONE", pyqSolvedCount = 45, notesAttachedFlag = true, revisionCount = 3, confidenceLevel = 85, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subBeeId, sectionId = sec2Id, subjectCategory = "Basic Electronics Engineering", topicName = "BJT Biasing, Small-Signal CE/CB/CC Models & High Freq Response", status = "DOING", pyqSolvedCount = 38, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 70, weightage = "HARD", estimatedHours = 6))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subBeeId, sectionId = sec2Id, subjectCategory = "Basic Electronics Engineering", topicName = "JFET & MOSFET Characteristics, Biasing & Amplifiers", status = "TODO", pyqSolvedCount = 28, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 60, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subBeeId, sectionId = sec2Id, subjectCategory = "Basic Electronics Engineering", topicName = "Zener, Tunnel, Varactor & Photodiode Applications", status = "DONE", pyqSolvedCount = 20, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 80, weightage = "EASY", estimatedHours = 4))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subBeeId, sectionId = sec2Id, subjectCategory = "Basic Electronics Engineering", topicName = "Integrated Circuit (IC) Fabrication & CMOS Basics", status = "TODO", pyqSolvedCount = 15, notesAttachedFlag = false, revisionCount = 0, confidenceLevel = 50, weightage = "MEDIUM", estimatedHours = 4))

        // ET-1 Subject: Network Theory & Circuit Analysis
        val subNetId = "ET_SUB_NET"
        dao.insertEseSubject(EseSubjectEntity(id = subNetId, paperId = paper1Id, subjectName = "Network Theory & Circuit Analysis", subjectCode = "ET-NET", weightageMarks = 45, displayOrder = 2))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subNetId, sectionId = sec2Id, subjectCategory = "Network Theory & Circuit Analysis", topicName = "Mesh & Nodal Circuit Solving & Star-Delta Conversion", status = "DONE", pyqSolvedCount = 52, notesAttachedFlag = true, revisionCount = 3, confidenceLevel = 90, weightage = "EASY", estimatedHours = 4))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subNetId, sectionId = sec2Id, subjectCategory = "Network Theory & Circuit Analysis", topicName = "Network Theorems (Thevenin, Norton, Superposition, Max Power)", status = "DONE", pyqSolvedCount = 65, notesAttachedFlag = true, revisionCount = 4, confidenceLevel = 92, weightage = "MEDIUM", estimatedHours = 6))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subNetId, sectionId = sec2Id, subjectCategory = "Network Theory & Circuit Analysis", topicName = "Transient & Steady-State Analysis (RL, RC, RLC Circuits)", status = "REVISING", pyqSolvedCount = 44, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 72, weightage = "HARD", estimatedHours = 7))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subNetId, sectionId = sec2Id, subjectCategory = "Network Theory & Circuit Analysis", topicName = "Resonance (Series & Parallel), Q-Factor & Selectivity", status = "DONE", pyqSolvedCount = 35, notesAttachedFlag = true, revisionCount = 3, confidenceLevel = 88, weightage = "EASY", estimatedHours = 4))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subNetId, sectionId = sec2Id, subjectCategory = "Network Theory & Circuit Analysis", topicName = "Two-Port Network Parameters (Z, Y, ABCD, h parameters)", status = "TODO", pyqSolvedCount = 30, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 65, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subNetId, sectionId = sec2Id, subjectCategory = "Network Theory & Circuit Analysis", topicName = "Graph Theory, Tie-set & Cut-set Matrices, Duality", status = "TODO", pyqSolvedCount = 18, notesAttachedFlag = false, revisionCount = 0, confidenceLevel = 45, weightage = "HARD", estimatedHours = 5))

        // ET-1 Subject: Analog Circuits
        val subAnaId = "ET_SUB_ANA"
        dao.insertEseSubject(EseSubjectEntity(id = subAnaId, paperId = paper1Id, subjectName = "Analog Circuits & Op-Amps", subjectCode = "ET-ANA", weightageMarks = 50, displayOrder = 3))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subAnaId, sectionId = sec2Id, subjectCategory = "Analog Circuits & Op-Amps", topicName = "Operational Amplifiers: Inverting, Non-Inverting & Integrator", status = "DONE", pyqSolvedCount = 48, notesAttachedFlag = true, revisionCount = 3, confidenceLevel = 85, weightage = "EASY", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subAnaId, sectionId = sec2Id, subjectCategory = "Analog Circuits & Op-Amps", topicName = "Negative & Positive Feedback Amplifiers & Stability Margins", status = "DOING", pyqSolvedCount = 36, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 70, weightage = "HARD", estimatedHours = 6))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subAnaId, sectionId = sec2Id, subjectCategory = "Analog Circuits & Op-Amps", topicName = "Sinusoidal Oscillators (Wien Bridge, Colpitts, Hartley, Crystal)", status = "DONE", pyqSolvedCount = 26, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 78, weightage = "MEDIUM", estimatedHours = 4))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subAnaId, sectionId = sec2Id, subjectCategory = "Analog Circuits & Op-Amps", topicName = "Active Filters (Butterworth & Chebyshev LPF/HPF/BPF)", status = "TODO", pyqSolvedCount = 22, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 55, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subAnaId, sectionId = sec2Id, subjectCategory = "Analog Circuits & Op-Amps", topicName = "Comparators, Schmitt Trigger & 555 Multivibrators", status = "TODO", pyqSolvedCount = 24, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 62, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subAnaId, sectionId = sec2Id, subjectCategory = "Analog Circuits & Op-Amps", topicName = "Power Amplifiers (Class A, B, AB, C) & Heat Sinks", status = "TODO", pyqSolvedCount = 16, notesAttachedFlag = false, revisionCount = 0, confidenceLevel = 50, weightage = "MEDIUM", estimatedHours = 4))

        // ET-1 Subject: Digital Circuits
        val subDigId = "ET_SUB_DIG"
        dao.insertEseSubject(EseSubjectEntity(id = subDigId, paperId = paper1Id, subjectName = "Digital Circuits & Systems", subjectCode = "ET-DIG", weightageMarks = 45, displayOrder = 4))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subDigId, sectionId = sec2Id, subjectCategory = "Digital Circuits & Systems", topicName = "Boolean Algebra, Logic Gates & K-Map Minimization", status = "DONE", pyqSolvedCount = 40, notesAttachedFlag = true, revisionCount = 3, confidenceLevel = 95, weightage = "EASY", estimatedHours = 4))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subDigId, sectionId = sec2Id, subjectCategory = "Digital Circuits & Systems", topicName = "Combinational Logic: Mux, Demux, Encoders, Decoders & Adders", status = "DONE", pyqSolvedCount = 42, notesAttachedFlag = true, revisionCount = 3, confidenceLevel = 88, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subDigId, sectionId = sec2Id, subjectCategory = "Digital Circuits & Systems", topicName = "Sequential Circuits: Flip-Flops, Setup/Hold Time & Metastability", status = "DOING", pyqSolvedCount = 35, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 68, weightage = "HARD", estimatedHours = 6))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subDigId, sectionId = sec2Id, subjectCategory = "Digital Circuits & Systems", topicName = "Synchronous & Asynchronous Counters & Shift Registers", status = "DONE", pyqSolvedCount = 28, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 80, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subDigId, sectionId = sec2Id, subjectCategory = "Digital Circuits & Systems", topicName = "Semiconductor Memories (SRAM, DRAM, ROM, Flash)", status = "TODO", pyqSolvedCount = 14, notesAttachedFlag = false, revisionCount = 0, confidenceLevel = 55, weightage = "EASY", estimatedHours = 3))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subDigId, sectionId = sec2Id, subjectCategory = "Digital Circuits & Systems", topicName = "ADC & DAC Architectures (Flash, SAR, Dual Slope, R-2R)", status = "TODO", pyqSolvedCount = 20, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 60, weightage = "MEDIUM", estimatedHours = 5))

        // ET-1 Subject: Materials Science
        val subMatId = "ET_SUB_MAT"
        dao.insertEseSubject(EseSubjectEntity(id = subMatId, paperId = paper1Id, subjectName = "Materials Science & Nanomaterials", subjectCode = "ET-MAT", weightageMarks = 35, displayOrder = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subMatId, sectionId = sec2Id, subjectCategory = "Materials Science & Nanomaterials", topicName = "Crystalline Structures, Imperfections & Band Theory", status = "DONE", pyqSolvedCount = 24, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 75, weightage = "MEDIUM", estimatedHours = 4))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subMatId, sectionId = sec2Id, subjectCategory = "Materials Science & Nanomaterials", topicName = "Dielectrics, Polarization & Piezoelectric Materials", status = "TODO", pyqSolvedCount = 18, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 58, weightage = "MEDIUM", estimatedHours = 4))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subMatId, sectionId = sec2Id, subjectCategory = "Materials Science & Nanomaterials", topicName = "Magnetic Materials: Ferromagnetism, Ferrites & B-H Loop", status = "TODO", pyqSolvedCount = 22, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 65, weightage = "MEDIUM", estimatedHours = 4))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subMatId, sectionId = sec2Id, subjectCategory = "Materials Science & Nanomaterials", topicName = "Superconductivity, Meissner Effect & Josephson Junctions", status = "TODO", pyqSolvedCount = 15, notesAttachedFlag = false, revisionCount = 0, confidenceLevel = 50, weightage = "MEDIUM", estimatedHours = 3))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subMatId, sectionId = sec2Id, subjectCategory = "Materials Science & Nanomaterials", topicName = "Nanomaterials, Carbon Nanotubes & Quantum Structures", status = "TODO", pyqSolvedCount = 10, notesAttachedFlag = false, revisionCount = 0, confidenceLevel = 40, weightage = "HARD", estimatedHours = 3))

        // ET-1 Subject: Measurements & Instrumentation
        val subMeasId = "ET_SUB_MEAS"
        dao.insertEseSubject(EseSubjectEntity(id = subMeasId, paperId = paper1Id, subjectName = "Electronic Measurements & Instrumentation", subjectCode = "ET-MEAS", weightageMarks = 40, displayOrder = 6))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subMeasId, sectionId = sec2Id, subjectCategory = "Electronic Measurements & Instrumentation", topicName = "Measurement of R, L, C via AC & DC Bridge Methods", status = "DONE", pyqSolvedCount = 32, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 82, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subMeasId, sectionId = sec2Id, subjectCategory = "Electronic Measurements & Instrumentation", topicName = "Digital Multimeters, True RMS Meters & DSO Operation", status = "DONE", pyqSolvedCount = 25, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 80, weightage = "EASY", estimatedHours = 4))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subMeasId, sectionId = sec2Id, subjectCategory = "Electronic Measurements & Instrumentation", topicName = "Transducers: Strain Gauges, LVDT, Thermocouples, RTD", status = "DOING", pyqSolvedCount = 28, notesAttachedFlag = true, revisionCount = 1, confidenceLevel = 65, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subMeasId, sectionId = sec2Id, subjectCategory = "Electronic Measurements & Instrumentation", topicName = "Signal Generators, Distortion Meters & Spectrum Analyzers", status = "TODO", pyqSolvedCount = 16, notesAttachedFlag = false, revisionCount = 0, confidenceLevel = 52, weightage = "MEDIUM", estimatedHours = 4))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subMeasId, sectionId = sec2Id, subjectCategory = "Electronic Measurements & Instrumentation", topicName = "Error Analysis, Standard Deviations & Instrument Limiting Errors", status = "DONE", pyqSolvedCount = 22, notesAttachedFlag = true, revisionCount = 3, confidenceLevel = 90, weightage = "EASY", estimatedHours = 3))

        // ET-1 Subject: Basic Electrical Engineering
        val subElecId = "ET_SUB_ELEC"
        dao.insertEseSubject(EseSubjectEntity(id = subElecId, paperId = paper1Id, subjectName = "Basic Electrical Engineering", subjectCode = "ET-ELEC", weightageMarks = 40, displayOrder = 7))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subElecId, sectionId = sec2Id, subjectCategory = "Basic Electrical Engineering", topicName = "Single-Phase & 3-Phase AC Circuits (Star/Delta Power)", status = "DONE", pyqSolvedCount = 30, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 80, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subElecId, sectionId = sec2Id, subjectCategory = "Basic Electrical Engineering", topicName = "Transformers: Equivalent Circuit, Regulation & Efficiency", status = "DOING", pyqSolvedCount = 26, notesAttachedFlag = true, revisionCount = 1, confidenceLevel = 64, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subElecId, sectionId = sec2Id, subjectCategory = "Basic Electrical Engineering", topicName = "DC Machines: Series/Shunt Motors, Back EMF & Speed Control", status = "TODO", pyqSolvedCount = 20, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 58, weightage = "HARD", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper1Id, subjectId = subElecId, sectionId = sec2Id, subjectCategory = "Basic Electrical Engineering", topicName = "3-Phase Induction Motors: Torque-Slip Curve & Starting", status = "TODO", pyqSolvedCount = 18, notesAttachedFlag = false, revisionCount = 0, confidenceLevel = 48, weightage = "HARD", estimatedHours = 6))

        // --- PAPER 2: ESE ET Paper 2 (Systems, Comms & Control) ---
        val paper2Id = "ESE_ET_P2"
        dao.insertEsePaper(
            EsePaperEntity(
                id = paper2Id,
                paperCode = "ET-2",
                paperName = "Paper-II: Systems, Comms & Control",
                stageType = "PRELIMS_TECH",
                totalMarks = 300,
                targetScore = 220,
                displayOrder = 2
            )
        )

        // ET-2 Subject: Signals and Systems
        val subSigId = "ET_SUB_SIG"
        dao.insertEseSubject(EseSubjectEntity(id = subSigId, paperId = paper2Id, subjectName = "Signals and Systems", subjectCode = "ET-SIG", weightageMarks = 45, displayOrder = 1))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subSigId, sectionId = sec2Id, subjectCategory = "Signals and Systems", topicName = "Continuous & Discrete LTI Systems, Impulse Response & Convolution", status = "DONE", pyqSolvedCount = 45, notesAttachedFlag = true, revisionCount = 3, confidenceLevel = 85, weightage = "MEDIUM", estimatedHours = 6))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subSigId, sectionId = sec2Id, subjectCategory = "Signals and Systems", topicName = "Fourier Series, CTFT & Frequency Domain Properties", status = "DOING", pyqSolvedCount = 36, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 72, weightage = "HARD", estimatedHours = 6))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subSigId, sectionId = sec2Id, subjectCategory = "Signals and Systems", topicName = "Laplace Transform, Region of Convergence (ROC) & System Poles", status = "DONE", pyqSolvedCount = 38, notesAttachedFlag = true, revisionCount = 3, confidenceLevel = 88, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subSigId, sectionId = sec2Id, subjectCategory = "Signals and Systems", topicName = "Z-Transform, Inverse ZT, Stability & Causality of DT Systems", status = "REVISING", pyqSolvedCount = 32, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 74, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subSigId, sectionId = sec2Id, subjectCategory = "Signals and Systems", topicName = "Sampling Theorem, Nyquist Rate, Aliasing & Reconstruction", status = "DONE", pyqSolvedCount = 28, notesAttachedFlag = true, revisionCount = 3, confidenceLevel = 92, weightage = "EASY", estimatedHours = 4))

        // ET-2 Subject: Control Systems
        val subCtrlId = "ET_SUB_CTRL"
        dao.insertEseSubject(EseSubjectEntity(id = subCtrlId, paperId = paper2Id, subjectName = "Control Systems", subjectCode = "ET-CTRL", weightageMarks = 45, displayOrder = 2))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subCtrlId, sectionId = sec2Id, subjectCategory = "Control Systems", topicName = "Transfer Functions, Block Diagrams & Mason's Gain Formula", status = "DONE", pyqSolvedCount = 50, notesAttachedFlag = true, revisionCount = 3, confidenceLevel = 90, weightage = "EASY", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subCtrlId, sectionId = sec2Id, subjectCategory = "Control Systems", topicName = "Time-Domain Transient Response: Peak Overshoot & Settling Time", status = "DONE", pyqSolvedCount = 40, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 82, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subCtrlId, sectionId = sec2Id, subjectCategory = "Control Systems", topicName = "Routh-Hurwitz Stability Criterion & Special Cases", status = "DONE", pyqSolvedCount = 35, notesAttachedFlag = true, revisionCount = 3, confidenceLevel = 92, weightage = "EASY", estimatedHours = 4))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subCtrlId, sectionId = sec2Id, subjectCategory = "Control Systems", topicName = "Root Locus Technique: Asymptotes, Breakaway & Angle of Arrival", status = "DOING", pyqSolvedCount = 32, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 68, weightage = "HARD", estimatedHours = 6))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subCtrlId, sectionId = sec2Id, subjectCategory = "Control Systems", topicName = "Frequency Response: Nyquist Criterion & Stability Encirclements", status = "TODO", pyqSolvedCount = 28, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 54, weightage = "HARD", estimatedHours = 7))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subCtrlId, sectionId = sec2Id, subjectCategory = "Control Systems", topicName = "Bode Plots: Gain Margin, Phase Margin & Lead-Lag Compensators", status = "REVISING", pyqSolvedCount = 30, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 70, weightage = "HARD", estimatedHours = 6))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subCtrlId, sectionId = sec2Id, subjectCategory = "Control Systems", topicName = "State Space Analysis: State Transition Matrix, Controllability", status = "TODO", pyqSolvedCount = 22, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 58, weightage = "HARD", estimatedHours = 6))

        // ET-2 Subject: Analog & Digital Communication Systems
        val subCommId = "ET_SUB_COMM"
        dao.insertEseSubject(EseSubjectEntity(id = subCommId, paperId = paper2Id, subjectName = "Analog & Digital Communication", subjectCode = "ET-COMM", weightageMarks = 50, displayOrder = 3))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subCommId, sectionId = sec2Id, subjectCategory = "Analog & Digital Communication", topicName = "Random Variables, Noise Spectral Density & Noise Figure", status = "DOING", pyqSolvedCount = 34, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 68, weightage = "HARD", estimatedHours = 6))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subCommId, sectionId = sec2Id, subjectCategory = "Analog & Digital Communication", topicName = "Analog Modulation: AM, DSB-SC, SSB, VSB & Superheterodyne", status = "DONE", pyqSolvedCount = 42, notesAttachedFlag = true, revisionCount = 3, confidenceLevel = 86, weightage = "MEDIUM", estimatedHours = 6))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subCommId, sectionId = sec2Id, subjectCategory = "Analog & Digital Communication", topicName = "Frequency Modulation (FM), Carson's Rule, PLL Demodulator", status = "DONE", pyqSolvedCount = 38, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 80, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subCommId, sectionId = sec2Id, subjectCategory = "Analog & Digital Communication", topicName = "Sampling, Quantization Noise & Pulse Code Modulation (PCM)", status = "DONE", pyqSolvedCount = 35, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 84, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subCommId, sectionId = sec2Id, subjectCategory = "Analog & Digital Communication", topicName = "Differential PCM, Delta Modulation & Slope Overload Distortion", status = "TODO", pyqSolvedCount = 24, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 62, weightage = "MEDIUM", estimatedHours = 4))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subCommId, sectionId = sec2Id, subjectCategory = "Analog & Digital Communication", topicName = "Digital Carrier Modulation: BPSK, QPSK, QAM, Constellations & BER", status = "REVISING", pyqSolvedCount = 30, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 70, weightage = "HARD", estimatedHours = 7))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subCommId, sectionId = sec2Id, subjectCategory = "Analog & Digital Communication", topicName = "Information Theory: Entropy, Mutual Info & Shannon Channel Capacity", status = "TODO", pyqSolvedCount = 20, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 58, weightage = "MEDIUM", estimatedHours = 5))

        // ET-2 Subject: Electromagnetics & Antennas
        val subEmtId = "ET_SUB_EMT"
        dao.insertEseSubject(EseSubjectEntity(id = subEmtId, paperId = paper2Id, subjectName = "Electromagnetics, Waveguides & Antennas", subjectCode = "ET-EMT", weightageMarks = 45, displayOrder = 4))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subEmtId, sectionId = sec2Id, subjectCategory = "Electromagnetics, Waveguides & Antennas", topicName = "Maxwell's Equations, Boundary Conditions & Wave Equation", status = "DONE", pyqSolvedCount = 36, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 80, weightage = "MEDIUM", estimatedHours = 6))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subEmtId, sectionId = sec2Id, subjectCategory = "Electromagnetics, Waveguides & Antennas", topicName = "Plane Wave Propagation, Attenuation, Skin Depth & Poynting Vector", status = "DOING", pyqSolvedCount = 28, notesAttachedFlag = true, revisionCount = 1, confidenceLevel = 64, weightage = "HARD", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subEmtId, sectionId = sec2Id, subjectCategory = "Electromagnetics, Waveguides & Antennas", topicName = "Transmission Lines: Characteristic Impedance, Reflection & VSWR", status = "REVISING", pyqSolvedCount = 38, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 76, weightage = "MEDIUM", estimatedHours = 6))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subEmtId, sectionId = sec2Id, subjectCategory = "Electromagnetics, Waveguides & Antennas", topicName = "Smith Chart Impedance Matching: Single & Double Stub Tuners", status = "TODO", pyqSolvedCount = 22, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 52, weightage = "HARD", estimatedHours = 6))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subEmtId, sectionId = sec2Id, subjectCategory = "Electromagnetics, Waveguides & Antennas", topicName = "Rectangular & Circular Waveguides: TE/TM Modes & Cutoff Frequency", status = "TODO", pyqSolvedCount = 25, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 56, weightage = "HARD", estimatedHours = 6))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subEmtId, sectionId = sec2Id, subjectCategory = "Electromagnetics, Waveguides & Antennas", topicName = "Antenna Fundamentals: Radiation Resistance, Gain, Directivity & Dipoles", status = "TODO", pyqSolvedCount = 26, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 60, weightage = "MEDIUM", estimatedHours = 5))

        // ET-2 Subject: Computer Organization & Architecture
        val subCoaId = "ET_SUB_COA"
        dao.insertEseSubject(EseSubjectEntity(id = subCoaId, paperId = paper2Id, subjectName = "Computer Organization & Architecture", subjectCode = "ET-COA", weightageMarks = 35, displayOrder = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subCoaId, sectionId = sec2Id, subjectCategory = "Computer Organization & Architecture", topicName = "Basic Machine Organization, Instruction Cycles & Addressing Modes", status = "DONE", pyqSolvedCount = 28, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 82, weightage = "EASY", estimatedHours = 4))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subCoaId, sectionId = sec2Id, subjectCategory = "Computer Organization & Architecture", topicName = "CPU Datapath, Control Unit Design (Hardwired vs Microprogrammed)", status = "TODO", pyqSolvedCount = 18, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 60, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subCoaId, sectionId = sec2Id, subjectCategory = "Computer Organization & Architecture", topicName = "Memory Hierarchy: Cache Mapping (Direct, Set-Associative) & Cache Misses", status = "DOING", pyqSolvedCount = 26, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 74, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subCoaId, sectionId = sec2Id, subjectCategory = "Computer Organization & Architecture", topicName = "Pipelining: Pipeline Hazards (Data, Control, Structural) & Speedup", status = "TODO", pyqSolvedCount = 24, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 62, weightage = "HARD", estimatedHours = 6))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subCoaId, sectionId = sec2Id, subjectCategory = "Computer Organization & Architecture", topicName = "Input/Output Systems, Interrupt Handling & DMA Controller", status = "TODO", pyqSolvedCount = 16, notesAttachedFlag = false, revisionCount = 0, confidenceLevel = 55, weightage = "MEDIUM", estimatedHours = 4))

        // ET-2 Subject: Microprocessors & Microcontrollers
        val subMicroId = "ET_SUB_MICRO"
        dao.insertEseSubject(EseSubjectEntity(id = subMicroId, paperId = paper2Id, subjectName = "Microprocessors & Embedded Systems", subjectCode = "ET-MICRO", weightageMarks = 40, displayOrder = 6))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subMicroId, sectionId = sec2Id, subjectCategory = "Microprocessors & Embedded Systems", topicName = "8085 Architecture, Register Set, Instruction Set & Timing Diagrams", status = "DONE", pyqSolvedCount = 35, notesAttachedFlag = true, revisionCount = 3, confidenceLevel = 85, weightage = "MEDIUM", estimatedHours = 6))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subMicroId, sectionId = sec2Id, subjectCategory = "Microprocessors & Embedded Systems", topicName = "8085 Assembly Language Programming & Delay Routines", status = "DONE", pyqSolvedCount = 26, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 80, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subMicroId, sectionId = sec2Id, subjectCategory = "Microprocessors & Embedded Systems", topicName = "Peripheral Interfacing: 8255 Programmable Peripheral Interface", status = "DOING", pyqSolvedCount = 22, notesAttachedFlag = true, revisionCount = 1, confidenceLevel = 64, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subMicroId, sectionId = sec2Id, subjectCategory = "Microprocessors & Embedded Systems", topicName = "8086 Architecture: Bus Interface Unit, Execution Unit & Segmentation", status = "TODO", pyqSolvedCount = 20, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 58, weightage = "HARD", estimatedHours = 6))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subMicroId, sectionId = sec2Id, subjectCategory = "Microprocessors & Embedded Systems", topicName = "Microcontrollers (8051 / ARM Cortex) Architecture & Timers", status = "TODO", pyqSolvedCount = 15, notesAttachedFlag = false, revisionCount = 0, confidenceLevel = 48, weightage = "HARD", estimatedHours = 5))

        // ET-2 Subject: Advanced Electronics & Communication
        val subAdvId = "ET_SUB_ADV"
        dao.insertEseSubject(EseSubjectEntity(id = subAdvId, paperId = paper2Id, subjectName = "Advanced Electronics & Communication", subjectCode = "ET-ADV", weightageMarks = 40, displayOrder = 7))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subAdvId, sectionId = sec2Id, subjectCategory = "Advanced Electronics & Communication", topicName = "Cellular Mobile Systems: Frequency Reuse, Handoff, GSM, CDMA & 5G NR", status = "DOING", pyqSolvedCount = 24, notesAttachedFlag = true, revisionCount = 1, confidenceLevel = 66, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subAdvId, sectionId = sec2Id, subjectCategory = "Advanced Electronics & Communication", topicName = "Satellite Communications: Kepler's Laws, Transponders & Link Budgets", status = "TODO", pyqSolvedCount = 18, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 58, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subAdvId, sectionId = sec2Id, subjectCategory = "Advanced Electronics & Communication", topicName = "Optical Fiber Communication: Dispersion, Losses, EDFA & Photodetectors", status = "DONE", pyqSolvedCount = 22, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 78, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subAdvId, sectionId = sec2Id, subjectCategory = "Advanced Electronics & Communication", topicName = "Radar Engineering: Radar Range Equation, MTI & CW Doppler Radar", status = "TODO", pyqSolvedCount = 14, notesAttachedFlag = false, revisionCount = 0, confidenceLevel = 52, weightage = "MEDIUM", estimatedHours = 4))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paper2Id, subjectId = subAdvId, sectionId = sec2Id, subjectCategory = "Advanced Electronics & Communication", topicName = "VLSI Design Principles: MOS Scaling, CMOS Inverter DC Characteristics", status = "TODO", pyqSolvedCount = 16, notesAttachedFlag = false, revisionCount = 0, confidenceLevel = 50, weightage = "HARD", estimatedHours = 4))

        // --- PAPER 3: ESE General Studies & Engineering Aptitude ---
        val paperGsId = "ESE_GS"
        dao.insertEsePaper(
            EsePaperEntity(
                id = paperGsId,
                paperCode = "GS-EA",
                paperName = "Paper-I: General Studies & Engg Aptitude",
                stageType = "PRELIMS_GS",
                totalMarks = 200,
                targetScore = 135,
                displayOrder = 3
            )
        )

        // GS Subjects
        val subAptId = "GS_SUB_APT"
        dao.insertEseSubject(EseSubjectEntity(id = subAptId, paperId = paperGsId, subjectName = "Engineering Aptitude & Reasoning", subjectCode = "GS-APT", weightageMarks = 25, displayOrder = 1))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperGsId, subjectId = subAptId, sectionId = sec1Id, subjectCategory = "Engineering Aptitude & Reasoning", topicName = "Syllogisms, Critical Thinking & Pattern Recognition", status = "DONE", pyqSolvedCount = 35, notesAttachedFlag = true, revisionCount = 3, confidenceLevel = 90, weightage = "EASY", estimatedHours = 4))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperGsId, subjectId = subAptId, sectionId = sec1Id, subjectCategory = "Engineering Aptitude & Reasoning", topicName = "Data Interpretation, Tables, Bar Charts & Pie Graphs", status = "DONE", pyqSolvedCount = 30, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 88, weightage = "EASY", estimatedHours = 4))

        val subMathId = "GS_SUB_MATH"
        dao.insertEseSubject(EseSubjectEntity(id = subMathId, paperId = paperGsId, subjectName = "Engineering Mathematics", subjectCode = "GS-MATH", weightageMarks = 25, displayOrder = 2))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperGsId, subjectId = subMathId, sectionId = sec1Id, subjectCategory = "Engineering Mathematics", topicName = "Matrix Algebra, Eigenvalues & Systems of Linear Equations", status = "DONE", pyqSolvedCount = 32, notesAttachedFlag = true, revisionCount = 3, confidenceLevel = 92, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperGsId, subjectId = subMathId, sectionId = sec1Id, subjectCategory = "Engineering Mathematics", topicName = "Numerical Methods: Newton-Raphson, Trapezoidal & Simpson's Rule", status = "DOING", pyqSolvedCount = 24, notesAttachedFlag = true, revisionCount = 1, confidenceLevel = 70, weightage = "MEDIUM", estimatedHours = 5))

        val subEnvId = "GS_SUB_ENV"
        dao.insertEseSubject(EseSubjectEntity(id = subEnvId, paperId = paperGsId, subjectName = "Energy & Environment", subjectCode = "GS-ENV", weightageMarks = 25, displayOrder = 3))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperGsId, subjectId = subEnvId, sectionId = sec1Id, subjectCategory = "Energy & Environment", topicName = "Solar, Wind, Biomass & Nuclear Energy Technologies", status = "DONE", pyqSolvedCount = 28, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 82, weightage = "EASY", estimatedHours = 4))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperGsId, subjectId = subEnvId, sectionId = sec1Id, subjectCategory = "Energy & Environment", topicName = "Global Warming, Climate Protocols (Kyoto, Paris) & Pollution Standards", status = "DOING", pyqSolvedCount = 25, notesAttachedFlag = true, revisionCount = 1, confidenceLevel = 72, weightage = "MEDIUM", estimatedHours = 4))

        val subQualId = "GS_SUB_QUAL"
        dao.insertEseSubject(EseSubjectEntity(id = subQualId, paperId = paperGsId, subjectName = "Standards & Quality Practices", subjectCode = "GS-QUAL", weightageMarks = 20, displayOrder = 4))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperGsId, subjectId = subQualId, sectionId = sec1Id, subjectCategory = "Standards & Quality Practices", topicName = "ISO Quality Frameworks (ISO 9001, 14001) & Six Sigma DMAIC", status = "DONE", pyqSolvedCount = 45, notesAttachedFlag = true, revisionCount = 3, confidenceLevel = 86, weightage = "EASY", estimatedHours = 4))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperGsId, subjectId = subQualId, sectionId = sec1Id, subjectCategory = "Standards & Quality Practices", topicName = "Statistical Process Control (SPC), Control Charts & TQM", status = "DOING", pyqSolvedCount = 22, notesAttachedFlag = true, revisionCount = 1, confidenceLevel = 65, weightage = "MEDIUM", estimatedHours = 4))

        val subProjId = "GS_SUB_PROJ"
        dao.insertEseSubject(EseSubjectEntity(id = subProjId, paperId = paperGsId, subjectName = "Project Management Basics", subjectCode = "GS-PROJ", weightageMarks = 20, displayOrder = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperGsId, subjectId = subProjId, sectionId = sec1Id, subjectCategory = "Project Management Basics", topicName = "Project Formulation, CPM/PERT Critical Path Calculations", status = "DONE", pyqSolvedCount = 30, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 84, weightage = "MEDIUM", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperGsId, subjectId = subProjId, sectionId = sec1Id, subjectCategory = "Project Management Basics", topicName = "Earned Value Management, Cost Variance & Resource Leveling", status = "TODO", pyqSolvedCount = 18, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 60, weightage = "MEDIUM", estimatedHours = 4))

        val subIctId = "GS_SUB_ICT"
        dao.insertEseSubject(EseSubjectEntity(id = subIctId, paperId = paperGsId, subjectName = "Information & Communication Technologies", subjectCode = "GS-ICT", weightageMarks = 25, displayOrder = 6))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperGsId, subjectId = subIctId, sectionId = sec1Id, subjectCategory = "Information & Communication Technologies", topicName = "Networking Protocols (OSI Layers, TCP/IP, DNS, IPv4/v6)", status = "DONE", pyqSolvedCount = 60, notesAttachedFlag = true, revisionCount = 3, confidenceLevel = 92, weightage = "EASY", estimatedHours = 5))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperGsId, subjectId = subIctId, sectionId = sec1Id, subjectCategory = "Information & Communication Technologies", topicName = "Cyber Security, Cryptography & e-Governance Initiatives", status = "DONE", pyqSolvedCount = 28, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 80, weightage = "MEDIUM", estimatedHours = 4))

        val subEthId = "GS_SUB_ETH"
        dao.insertEseSubject(EseSubjectEntity(id = subEthId, paperId = paperGsId, subjectName = "Ethics & Values in Engineering", subjectCode = "GS-ETH", weightageMarks = 20, displayOrder = 7))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperGsId, subjectId = subEthId, sectionId = sec1Id, subjectCategory = "Ethics & Values in Engineering", topicName = "Code of Ethics, Professional Moral Dilemmas & Whistleblowing", status = "DONE", pyqSolvedCount = 25, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 85, weightage = "EASY", estimatedHours = 3))

        val subDesId = "GS_SUB_DES"
        dao.insertEseSubject(EseSubjectEntity(id = subDesId, paperId = paperGsId, subjectName = "Design, Drawing & Safety", subjectCode = "GS-DES", weightageMarks = 20, displayOrder = 8))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperGsId, subjectId = subDesId, sectionId = sec1Id, subjectCategory = "Design, Drawing & Safety", topicName = "Orthographic Projections, GD&T Basics & Safety Audits", status = "TODO", pyqSolvedCount = 16, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 58, weightage = "MEDIUM", estimatedHours = 4))

        val subCaId = "GS_SUB_CA"
        dao.insertEseSubject(EseSubjectEntity(id = subCaId, paperId = paperGsId, subjectName = "Current Affairs & Development", subjectCode = "GS-CA", weightageMarks = 20, displayOrder = 9))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperGsId, subjectId = subCaId, sectionId = sec1Id, subjectCategory = "Current Affairs & Development", topicName = "National Infrastructure Initiatives, Energy Transitions & Tech Policies", status = "DOING", pyqSolvedCount = 20, notesAttachedFlag = true, revisionCount = 1, confidenceLevel = 65, weightage = "MEDIUM", estimatedHours = 4))

        // --- PAPER 4: ESE Mains Conventional E&T ---
        val paperMainsId = "ESE_MAINS"
        dao.insertEsePaper(
            EsePaperEntity(
                id = paperMainsId,
                paperCode = "MAINS-ET",
                paperName = "Stage 2: Mains Conventional E&T",
                stageType = "MAINS_TECH",
                totalMarks = 600,
                targetScore = 420,
                displayOrder = 4
            )
        )

        val subM1Id = "M_SUB_ET1"
        dao.insertEseSubject(EseSubjectEntity(id = subM1Id, paperId = paperMainsId, subjectName = "Conventional Paper-I (Devices, Circuits & Systems)", subjectCode = "M-ET1", weightageMarks = 300, displayOrder = 1))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperMainsId, subjectId = subM1Id, sectionId = sec3Id, subjectCategory = "Conventional Paper-I (Devices, Circuits & Systems)", topicName = "Transistor Biasing & Multi-Stage Small-Signal Amplifiers Derivation", status = "DOING", pyqSolvedCount = 16, notesAttachedFlag = true, revisionCount = 1, confidenceLevel = 62, weightage = "HARD", estimatedHours = 8))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperMainsId, subjectId = subM1Id, sectionId = sec3Id, subjectCategory = "Conventional Paper-I (Devices, Circuits & Systems)", topicName = "Network Synthesis: Foster & Cauer Realizations & State Equations", status = "TODO", pyqSolvedCount = 12, notesAttachedFlag = false, revisionCount = 1, confidenceLevel = 54, weightage = "HARD", estimatedHours = 8))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperMainsId, subjectId = subM1Id, sectionId = sec3Id, subjectCategory = "Conventional Paper-I (Devices, Circuits & Systems)", topicName = "Precision Op-Amp Circuits, Instrumentation Amplifiers & Active Filters", status = "DONE", pyqSolvedCount = 20, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 76, weightage = "MEDIUM", estimatedHours = 7))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperMainsId, subjectId = subM1Id, sectionId = sec3Id, subjectCategory = "Conventional Paper-I (Devices, Circuits & Systems)", topicName = "Sequential Machine Design, State Reduction & Minimization", status = "TODO", pyqSolvedCount = 14, notesAttachedFlag = false, revisionCount = 0, confidenceLevel = 50, weightage = "HARD", estimatedHours = 7))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperMainsId, subjectId = subM1Id, sectionId = sec3Id, subjectCategory = "Conventional Paper-I (Devices, Circuits & Systems)", topicName = "AC Bridges & Instrumental Derivations with Error Bounds", status = "DONE", pyqSolvedCount = 18, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 80, weightage = "MEDIUM", estimatedHours = 6))

        val subM2Id = "M_SUB_ET2"
        dao.insertEseSubject(EseSubjectEntity(id = subM2Id, paperId = paperMainsId, subjectName = "Conventional Paper-II (Comms, Control & Electromagnetics)", subjectCode = "M-ET2", weightageMarks = 300, displayOrder = 2))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperMainsId, subjectId = subM2Id, sectionId = sec3Id, subjectCategory = "Conventional Paper-II (Comms, Control & Electromagnetics)", topicName = "State Space Design: Pole Placement via Ackermann's & State Observer", status = "DOING", pyqSolvedCount = 14, notesAttachedFlag = true, revisionCount = 1, confidenceLevel = 60, weightage = "HARD", estimatedHours = 8))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperMainsId, subjectId = subM2Id, sectionId = sec3Id, subjectCategory = "Conventional Paper-II (Comms, Control & Electromagnetics)", topicName = "Digital Communication Link Budgets & BER Calculation for M-ary PSK", status = "TODO", pyqSolvedCount = 12, notesAttachedFlag = false, revisionCount = 0, confidenceLevel = 52, weightage = "HARD", estimatedHours = 8))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperMainsId, subjectId = subM2Id, sectionId = sec3Id, subjectCategory = "Conventional Paper-II (Comms, Control & Electromagnetics)", topicName = "Waveguide Mode Solutions & Smith Chart Impedance Matching", status = "TODO", pyqSolvedCount = 10, notesAttachedFlag = false, revisionCount = 0, confidenceLevel = 48, weightage = "HARD", estimatedHours = 8))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperMainsId, subjectId = subM2Id, sectionId = sec3Id, subjectCategory = "Conventional Paper-II (Comms, Control & Electromagnetics)", topicName = "8085/8086 Assembly Routines & 8255/8259 Interfacing Schematics", status = "DONE", pyqSolvedCount = 15, notesAttachedFlag = true, revisionCount = 2, confidenceLevel = 74, weightage = "MEDIUM", estimatedHours = 7))
        dao.insertEseTopic(EseTopicEntity(id = UUID.randomUUID().toString(), paperId = paperMainsId, subjectId = subM2Id, sectionId = sec3Id, subjectCategory = "Conventional Paper-II (Comms, Control & Electromagnetics)", topicName = "Optical Fiber Link Power Budget & Satellite Link Attenuation", status = "TODO", pyqSolvedCount = 10, notesAttachedFlag = false, revisionCount = 0, confidenceLevel = 50, weightage = "HARD", estimatedHours = 6))
    }
}
