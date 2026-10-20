package com.example.data.repository

import com.example.data.local.AcademicDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class AcademicRepository(private val dao: AcademicDao) {

    // 1. Expenses
    val allExpenses: Flow<List<ExpenseEntity>> = dao.getAllExpenses()
    suspend fun insertExpense(expense: ExpenseEntity) = dao.insertExpense(expense)
    suspend fun deleteExpense(id: String) = dao.deleteExpenseById(id)

    // 2 & 3. Dual Syllabus
    fun getSemesters(scope: String): Flow<List<SemesterEntity>> = dao.getSemestersByScope(scope)
    suspend fun insertSemester(semester: SemesterEntity) = dao.insertSemester(semester)
    val allSubjects: Flow<List<SubjectEntity>> = dao.getAllSubjects()
    fun getSubjectsForSemester(semesterId: String): Flow<List<SubjectEntity>> = dao.getSubjectsForSemester(semesterId)
    suspend fun insertSubject(subject: SubjectEntity) = dao.insertSubject(subject)
    suspend fun updateSubject(subject: SubjectEntity) = dao.updateSubject(subject)
    suspend fun deleteSubject(id: String) {
        dao.deleteTopicsForSubject(id)
        dao.deleteSubject(id)
    }

    val allTopics: Flow<List<TopicEntity>> = dao.getAllTopics()
    fun getTopicsForSubject(subjectId: String): Flow<List<TopicEntity>> = dao.getTopicsForSubject(subjectId)
    suspend fun insertTopic(topic: TopicEntity) = dao.insertTopic(topic)
    suspend fun updateTopic(topic: TopicEntity) = dao.updateTopic(topic)
    suspend fun deleteTopic(id: String) = dao.deleteTopic(id)
    suspend fun updateTopicStatus(topicId: String, status: String) = dao.updateTopicStatus(topicId, status)

    // 4. Results & Formula Engine
    val degreeProfiles: Flow<List<DegreeProfileEntity>> = dao.getDegreeProfiles()
    suspend fun insertDegreeProfile(profile: DegreeProfileEntity) = dao.insertDegreeProfile(profile)
    suspend fun updateDegreeProfile(profile: DegreeProfileEntity) = dao.updateDegreeProfile(profile)
    fun getSemesterGrades(profileId: String): Flow<List<SemesterGradeEntity>> = dao.getSemesterGrades(profileId)
    suspend fun insertSemesterGrade(grade: SemesterGradeEntity) = dao.insertSemesterGrade(grade)
    fun getSubjectMarks(profileId: String, semNumber: Int): Flow<List<SubjectMarkEntity>> = dao.getSubjectMarks(profileId, semNumber)
    fun getAllSubjectMarks(profileId: String): Flow<List<SubjectMarkEntity>> = dao.getAllSubjectMarksForProfile(profileId)
    suspend fun insertSubjectMark(mark: SubjectMarkEntity) = dao.insertSubjectMark(mark)

    // 5. Triple-Paper GATE Tracker
    val gatePapers: Flow<List<GatePaperEntity>> = dao.getGatePapers()
    suspend fun insertGatePaper(paper: GatePaperEntity) = dao.insertGatePaper(paper)
    suspend fun updateGatePaper(paper: GatePaperEntity) = dao.updateGatePaper(paper)
    suspend fun deleteGatePaper(id: String) {
        dao.deleteGateTopicsForPaper(id)
        dao.deleteGatePaper(id)
    }

    val allGateTopics: Flow<List<GateTopicEntity>> = dao.getAllGateTopics()
    fun getGateTopicsForPaper(paperId: String): Flow<List<GateTopicEntity>> = dao.getGateTopicsForPaper(paperId)
    suspend fun insertGateTopic(topic: GateTopicEntity) = dao.insertGateTopic(topic)
    suspend fun updateGateTopic(topic: GateTopicEntity) = dao.updateGateTopic(topic)
    suspend fun deleteGateTopic(id: String) = dao.deleteGateTopic(id)
    suspend fun incrementGateRevision(topicId: String) = dao.incrementGateRevision(topicId)
    suspend fun updateGateConfidence(topicId: String, confidence: Int) = dao.updateGateConfidence(topicId, confidence)

    // 6. ESE UPSC Tracker
    val eseSections: Flow<List<EseSectionEntity>> = dao.getEseSections()
    suspend fun insertEseSection(section: EseSectionEntity) = dao.insertEseSection(section)
    suspend fun updateEseSection(section: EseSectionEntity) = dao.updateEseSection(section)
    val allEseTopics: Flow<List<EseTopicEntity>> = dao.getAllEseTopics()
    fun getEseTopicsForSection(sectionId: String): Flow<List<EseTopicEntity>> = dao.getEseTopicsForSection(sectionId)
    suspend fun insertEseTopic(topic: EseTopicEntity) = dao.insertEseTopic(topic)
    suspend fun updateEseTopic(topic: EseTopicEntity) = dao.updateEseTopic(topic)
    suspend fun incrementEsePyq(topicId: String) = dao.incrementEsePyq(topicId)

    // 7. Certificates Hub
    val allCertificates: Flow<List<CertificateEntity>> = dao.getAllCertificates()
    suspend fun insertCertificate(cert: CertificateEntity) = dao.insertCertificate(cert)
    suspend fun deleteCertificate(id: String) = dao.deleteCertificate(id)

    // 8. Timetable
    val allTimetableConfigs: Flow<List<TimetableConfigEntity>> = dao.getAllTimetableConfigs()
    fun getTimetableConfigs(scheduleType: String): Flow<List<TimetableConfigEntity>> = dao.getTimetableConfigs(scheduleType)
    suspend fun insertTimetableConfig(config: TimetableConfigEntity) = dao.insertTimetableConfig(config)
    suspend fun updateTimetableConfig(config: TimetableConfigEntity) = dao.updateTimetableConfig(config)
    suspend fun deleteTimetableConfig(id: String) = dao.deleteTimetableConfig(id)
    fun getDailySlotLogs(dateString: String): Flow<List<DailySlotLogEntity>> = dao.getDailySlotLogs(dateString)
    suspend fun insertDailySlotLog(log: DailySlotLogEntity) = dao.insertDailySlotLog(log)
    suspend fun updateSlotLogStatus(id: String, isDone: Boolean) = dao.updateSlotLogStatus(id, isDone)

    // 9. Priority Notepad
    fun getScratchpad(dateString: String): Flow<DailyScratchpadEntity?> = dao.getScratchpad(dateString)
    suspend fun saveScratchpad(scratchpad: DailyScratchpadEntity) = dao.saveScratchpad(scratchpad)
    val allScratchpads: Flow<List<DailyScratchpadEntity>> = dao.getAllScratchpads()

    // 10. Semester Milestones
    val allMilestones: Flow<List<AcademicMilestoneEntity>> = dao.getAllMilestones()
    suspend fun insertMilestone(milestone: AcademicMilestoneEntity) = dao.insertMilestone(milestone)
    suspend fun deleteMilestone(id: String) = dao.deleteMilestone(id)
    suspend fun markMilestoneAlerted(id: String) = dao.markMilestoneAlerted(id)

    // 11. Future Upskilling
    val allFutureCourses: Flow<List<FutureCourseEntity>> = dao.getAllFutureCourses()
    suspend fun insertFutureCourse(course: FutureCourseEntity) = dao.insertFutureCourse(course)
    suspend fun updateFutureCourse(course: FutureCourseEntity) = dao.updateFutureCourse(course)
    suspend fun deleteFutureCourse(id: String) = dao.deleteFutureCourse(id)

    // 12. Career Matrix
    val allCareerOptions: Flow<List<CareerOptionEntity>> = dao.getAllCareerOptions()
    suspend fun insertCareerOption(career: CareerOptionEntity) = dao.insertCareerOption(career)
    suspend fun updateCareerOption(career: CareerOptionEntity) = dao.updateCareerOption(career)
    suspend fun deleteCareerOption(id: String) = dao.deleteCareerOption(id)

    // 13. Resume Builder
    val resumeProfile: Flow<ResumeProfileEntity?> = dao.getResumeProfile()
    suspend fun saveResumeProfile(profile: ResumeProfileEntity) = dao.saveResumeProfile(profile)
}
