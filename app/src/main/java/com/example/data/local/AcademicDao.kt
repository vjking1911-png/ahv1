package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AcademicDao {

    // ==========================================
    // 1. Academic Expenses
    // ==========================================
    @Query("SELECT * FROM academic_expenses ORDER BY timestamp DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Query("DELETE FROM academic_expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: String)

    // ==========================================
    // 2 & 3. Dual Syllabus
    // ==========================================
    @Query("SELECT * FROM semesters WHERE profileScope = :scope ORDER BY semesterNumber ASC")
    fun getSemestersByScope(scope: String): Flow<List<SemesterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemester(semester: SemesterEntity)

    @Query("SELECT * FROM subjects WHERE semesterId = :semesterId")
    fun getSubjectsForSemester(semesterId: String): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity)

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Query("DELETE FROM subjects WHERE id = :id")
    suspend fun deleteSubject(id: String)

    @Query("DELETE FROM topics WHERE subjectId = :subjectId")
    suspend fun deleteTopicsForSubject(subjectId: String)

    @Query("SELECT * FROM topics WHERE subjectId = :subjectId")
    fun getTopicsForSubject(subjectId: String): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics")
    fun getAllTopics(): Flow<List<TopicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: TopicEntity)

    @Update
    suspend fun updateTopic(topic: TopicEntity)

    @Query("DELETE FROM topics WHERE id = :id")
    suspend fun deleteTopic(id: String)

    @Query("UPDATE topics SET status = :status WHERE id = :topicId")
    suspend fun updateTopicStatus(topicId: String, status: String)

    // ==========================================
    // 4. Results & Formula Engine
    // ==========================================
    @Query("SELECT * FROM degree_profiles")
    fun getDegreeProfiles(): Flow<List<DegreeProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDegreeProfile(profile: DegreeProfileEntity)

    @Update
    suspend fun updateDegreeProfile(profile: DegreeProfileEntity)

    @Query("SELECT * FROM semester_grades WHERE degreeProfileId = :profileId ORDER BY semesterNumber ASC")
    fun getSemesterGrades(profileId: String): Flow<List<SemesterGradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemesterGrade(grade: SemesterGradeEntity)

    @Query("SELECT * FROM subject_marks WHERE degreeProfileId = :profileId AND semesterNumber = :semNumber")
    fun getSubjectMarks(profileId: String, semNumber: Int): Flow<List<SubjectMarkEntity>>

    @Query("SELECT * FROM subject_marks WHERE degreeProfileId = :profileId")
    fun getAllSubjectMarksForProfile(profileId: String): Flow<List<SubjectMarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjectMark(mark: SubjectMarkEntity)

    // ==========================================
    // 5. Triple-Paper GATE Syllabus Tracker
    // ==========================================
    @Query("SELECT * FROM gate_papers")
    fun getGatePapers(): Flow<List<GatePaperEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGatePaper(paper: GatePaperEntity)

    @Update
    suspend fun updateGatePaper(paper: GatePaperEntity)

    @Query("DELETE FROM gate_papers WHERE id = :id")
    suspend fun deleteGatePaper(id: String)

    @Query("DELETE FROM gate_topics WHERE paperId = :paperId")
    suspend fun deleteGateTopicsForPaper(paperId: String)

    @Query("SELECT * FROM gate_topics WHERE paperId = :paperId")
    fun getGateTopicsForPaper(paperId: String): Flow<List<GateTopicEntity>>

    @Query("SELECT * FROM gate_topics")
    fun getAllGateTopics(): Flow<List<GateTopicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGateTopic(topic: GateTopicEntity)

    @Update
    suspend fun updateGateTopic(topic: GateTopicEntity)

    @Query("DELETE FROM gate_topics WHERE id = :id")
    suspend fun deleteGateTopic(id: String)

    @Query("UPDATE gate_topics SET revisionCount = revisionCount + 1 WHERE id = :topicId")
    suspend fun incrementGateRevision(topicId: String)

    @Query("UPDATE gate_topics SET confidenceLevel = :confidence WHERE id = :topicId")
    suspend fun updateGateConfidence(topicId: String, confidence: Int)

    // ==========================================
    // 6. ESE UPSC Syllabus Tracker
    // ==========================================
    @Query("SELECT * FROM ese_sections")
    fun getEseSections(): Flow<List<EseSectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEseSection(section: EseSectionEntity)

    @Update
    suspend fun updateEseSection(section: EseSectionEntity)

    @Query("SELECT * FROM ese_topics WHERE sectionId = :sectionId")
    fun getEseTopicsForSection(sectionId: String): Flow<List<EseTopicEntity>>

    @Query("SELECT * FROM ese_topics")
    fun getAllEseTopics(): Flow<List<EseTopicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEseTopic(topic: EseTopicEntity)

    @Update
    suspend fun updateEseTopic(topic: EseTopicEntity)

    @Query("UPDATE ese_topics SET pyqSolvedCount = pyqSolvedCount + 1 WHERE id = :topicId")
    suspend fun incrementEsePyq(topicId: String)

    // ==========================================
    // 7. Digital Certificates Hub
    // ==========================================
    @Query("SELECT * FROM certificates ORDER BY targetAcquisitionDate ASC")
    fun getAllCertificates(): Flow<List<CertificateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertificate(cert: CertificateEntity)

    @Query("DELETE FROM certificates WHERE id = :id")
    suspend fun deleteCertificate(id: String)

    // ==========================================
    // 8. Holiday vs. Working Day Dynamic Timetable
    // ==========================================
    @Query("SELECT * FROM timetable_configs WHERE scheduleType = :scheduleType ORDER BY timeSlotStart ASC")
    fun getTimetableConfigs(scheduleType: String): Flow<List<TimetableConfigEntity>>

    @Query("SELECT * FROM timetable_configs")
    fun getAllTimetableConfigs(): Flow<List<TimetableConfigEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetableConfig(config: TimetableConfigEntity)

    @Update
    suspend fun updateTimetableConfig(config: TimetableConfigEntity)

    @Query("DELETE FROM timetable_configs WHERE id = :id")
    suspend fun deleteTimetableConfig(id: String)

    @Query("SELECT * FROM daily_slot_logs WHERE dateString = :dateString")
    fun getDailySlotLogs(dateString: String): Flow<List<DailySlotLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailySlotLog(log: DailySlotLogEntity)

    @Query("UPDATE daily_slot_logs SET isTickedDone = :isDone WHERE id = :id")
    suspend fun updateSlotLogStatus(id: String, isDone: Boolean)

    // ==========================================
    // 9. Front-Page Priority Notepad
    // ==========================================
    @Query("SELECT * FROM daily_scratchpad WHERE dateString = :dateString")
    fun getScratchpad(dateString: String): Flow<DailyScratchpadEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveScratchpad(scratchpad: DailyScratchpadEntity)

    @Query("SELECT * FROM daily_scratchpad ORDER BY dateString DESC")
    fun getAllScratchpads(): Flow<List<DailyScratchpadEntity>>

    // ==========================================
    // 10. Semester Writeup & Proactive Alert Engine
    // ==========================================
    @Query("SELECT * FROM academic_milestones ORDER BY targetTimestamp ASC")
    fun getAllMilestones(): Flow<List<AcademicMilestoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: AcademicMilestoneEntity)

    @Query("DELETE FROM academic_milestones WHERE id = :id")
    suspend fun deleteMilestone(id: String)

    @Query("UPDATE academic_milestones SET alertTriggeredFlag = 1 WHERE id = :id")
    suspend fun markMilestoneAlerted(id: String)

    // ==========================================
    // 11. Future Upskilling Checklist
    // ==========================================
    @Query("SELECT * FROM future_courses ORDER BY isCompleted ASC")
    fun getAllFutureCourses(): Flow<List<FutureCourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFutureCourse(course: FutureCourseEntity)

    @Update
    suspend fun updateFutureCourse(course: FutureCourseEntity)

    @Query("DELETE FROM future_courses WHERE id = :id")
    suspend fun deleteFutureCourse(id: String)

    // ==========================================
    // 12. Future Career Options Strategy Matrix
    // ==========================================
    @Query("SELECT * FROM career_options ORDER BY feasibilityScore DESC")
    fun getAllCareerOptions(): Flow<List<CareerOptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareerOption(career: CareerOptionEntity)

    @Update
    suspend fun updateCareerOption(career: CareerOptionEntity)

    @Query("DELETE FROM career_options WHERE id = :id")
    suspend fun deleteCareerOption(id: String)

    // ==========================================
    // 13. Local Tabular Resume Builder
    // ==========================================
    @Query("SELECT * FROM resume_profiles LIMIT 1")
    fun getResumeProfile(): Flow<ResumeProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveResumeProfile(profile: ResumeProfileEntity)
}
