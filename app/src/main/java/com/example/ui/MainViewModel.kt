package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AcademicDatabase
import com.example.data.local.InitialDataSeeder
import com.example.data.model.*
import com.example.data.receiver.AcademicAlertReceiver
import com.example.data.repository.AcademicRepository
import com.example.engine.FormulaEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AcademicDatabase.getDatabase(application, viewModelScope)
    val repository = AcademicRepository(database.academicDao())

    val todayDateStr: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // 1. Expenses
    val expenses = repository.allExpenses.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 2 & 3. Dual Syllabus
    private val _selectedCollegeScope = MutableStateFlow("COLLEGE_1")
    val selectedCollegeScope = _selectedCollegeScope.asStateFlow()

    val semestersCollege1 = repository.getSemesters("COLLEGE_1").stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val semestersCollege2 = repository.getSemesters("COLLEGE_2").stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubjects = repository.allSubjects.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allTopics = repository.allTopics.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 4. Results & Formula Engine
    val degreeProfiles = repository.degreeProfiles.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val _selectedDegreeProfileId = MutableStateFlow("DEG_1")
    val selectedDegreeProfileId = _selectedDegreeProfileId.asStateFlow()

    private val _sandboxFormula = MutableStateFlow("(CGPA * 9.5)")
    val sandboxFormula = _sandboxFormula.asStateFlow()

    private val _sandboxCgpaInput = MutableStateFlow("9.25")
    val sandboxCgpaInput = _sandboxCgpaInput.asStateFlow()

    val sandboxResult = combine(_sandboxFormula, _sandboxCgpaInput) { formula, cgpaStr ->
        val cgpa = cgpaStr.toDoubleOrNull() ?: 0.0
        FormulaEngine.evaluate(formula, cgpa)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.success(87.875))

    // 5. Triple-Paper GATE Tracker
    val gatePapers = repository.gatePapers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val _selectedGatePaperId = MutableStateFlow("GATE_CS")
    val selectedGatePaperId = _selectedGatePaperId.asStateFlow()
    val allGateTopics = repository.allGateTopics.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 6. ESE UPSC Tracker
    val eseSections = repository.eseSections.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val _selectedEseStage = MutableStateFlow("PRELIMS_GS")
    val selectedEseStage = _selectedEseStage.asStateFlow()
    val allEseTopics = repository.allEseTopics.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 7. Certificates Hub
    val allCertificates = repository.allCertificates.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 8. Timetable
    private val _activeScheduleType = MutableStateFlow("WORKING_DAY")
    val activeScheduleType = _activeScheduleType.asStateFlow()
    val timetableConfigs = repository.allTimetableConfigs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val dailySlotLogs = repository.getDailySlotLogs(todayDateStr).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 9. Front Page Priority Notepad
    val scratchpad = repository.getScratchpad(todayDateStr).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // 10. Semester Milestones
    val allMilestones = repository.allMilestones.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 11. Future Upskilling
    val allFutureCourses = repository.allFutureCourses.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 12. Career Strategy Matrix
    val allCareerOptions = repository.allCareerOptions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 13. Resume Profile
    val resumeProfile = repository.resumeProfile.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        // Ensure initial seeding if empty
        viewModelScope.launch(Dispatchers.IO) {
            val scratch = database.academicDao().getAllScratchpads().firstOrNull()
            if (scratch.isNullOrEmpty()) {
                InitialDataSeeder.seedInitialData(database.academicDao())
            }
        }
    }

    // --- Action Methods ---

    // Scope selection
    fun setCollegeScope(scope: String) { _selectedCollegeScope.value = scope }
    fun setDegreeProfile(id: String) { _selectedDegreeProfileId.value = id }
    fun setGatePaper(id: String) { _selectedGatePaperId.value = id }
    fun setEseStage(stage: String) { _selectedEseStage.value = stage }
    fun setScheduleType(type: String) { _activeScheduleType.value = type }
    fun setSandboxFormula(f: String) { _sandboxFormula.value = f }
    fun setSandboxCgpaInput(cgpa: String) { _sandboxCgpaInput.value = cgpa }

    // Expenses
    fun addExpense(title: String, amount: Double, category: String, paymentType: String, roiRating: Int?, collegeScope: String, semester: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertExpense(
                ExpenseEntity(
                    title = title,
                    amount = amount,
                    category = category,
                    paymentType = paymentType,
                    roiRating = roiRating,
                    collegeProfileId = collegeScope,
                    semesterTag = semester
                )
            )
        }
    }

    fun deleteExpense(id: String) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteExpense(id) }
    }

    // Syllabus
    fun updateTopicStatus(topicId: String, status: String) {
        viewModelScope.launch(Dispatchers.IO) { repository.updateTopicStatus(topicId, status) }
    }

    fun addTopic(subjectId: String, title: String, weightage: String, hours: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTopic(
                TopicEntity(subjectId = subjectId, topicTitle = title, weightage = weightage, estimatedHours = hours)
            )
        }
    }

    fun addSubject(semesterId: String, name: String, code: String, credits: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertSubject(
                SubjectEntity(semesterId = semesterId, subjectName = name, subjectCode = code, credits = credits)
            )
        }
    }

    fun updateSubject(subject: SubjectEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSubject(subject)
        }
    }

    fun deleteSubject(subjectId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSubject(subjectId)
        }
    }

    fun deleteTopic(topicId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTopic(topicId)
        }
    }

    // Results & Formula & Degree Customization
    fun updateDegreeName(profileId: String, newName: String, newInstitution: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = degreeProfiles.value.find { it.id == profileId }
            if (profile != null) {
                repository.updateDegreeProfile(profile.copy(degreeName = newName, institutionName = newInstitution))
            }
        }
    }

    fun saveDegreeFormula(profileId: String, formula: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = degreeProfiles.value.find { it.id == profileId }
            if (profile != null) {
                repository.updateDegreeProfile(profile.copy(calculationFormulaString = formula))
            }
        }
    }

    // GATE
    fun incrementGateRevision(topicId: String) {
        viewModelScope.launch(Dispatchers.IO) { repository.incrementGateRevision(topicId) }
    }

    fun updateGateConfidence(topicId: String, confidence: Int) {
        viewModelScope.launch(Dispatchers.IO) { repository.updateGateConfidence(topicId, confidence) }
    }

    fun addGatePaper(code: String, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newId = UUID.randomUUID().toString()
            repository.insertGatePaper(GatePaperEntity(id = newId, paperCode = code, paperName = name))
            _selectedGatePaperId.value = newId
        }
    }

    fun updateGatePaper(paperId: String, code: String, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateGatePaper(GatePaperEntity(id = paperId, paperCode = code, paperName = name))
        }
    }

    fun deleteGatePaper(paperId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteGatePaper(paperId)
            val remaining = gatePapers.value.filter { it.id != paperId }
            if (remaining.isNotEmpty()) {
                _selectedGatePaperId.value = remaining.first().id
            }
        }
    }

    fun deleteGateTopic(topicId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteGateTopic(topicId)
        }
    }

    fun addGateTopic(paperId: String, category: String, topicName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertGateTopic(
                GateTopicEntity(paperId = paperId, subjectCategory = category, topicName = topicName)
            )
        }
    }

    // ESE
    fun updateEseSection(sectionId: String, newTitle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = eseSections.value.find { it.id == sectionId }
            if (existing != null) {
                repository.updateEseSection(existing.copy(paperTitle = newTitle))
            }
        }
    }

    fun incrementEsePyq(topicId: String) {
        viewModelScope.launch(Dispatchers.IO) { repository.incrementEsePyq(topicId) }
    }

    fun toggleEseTopicStatus(topic: EseTopicEntity) {
        val newStatus = if (topic.status == "DONE") "TODO" else "DONE"
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateEseTopic(topic.copy(status = newStatus))
        }
    }

    // Certificates
    fun addCertificate(name: String, status: String, targetDate: String, issuer: String, tags: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val filePath = if (status == "OWNED") {
                "/data/user/0/com.aistudio.academichub.vkhdza/files/certificates/${name.replace(" ", "_").lowercase()}.pdf"
            } else null
            repository.insertCertificate(
                CertificateEntity(
                    certificateName = name,
                    status = status,
                    filePath = filePath,
                    targetAcquisitionDate = targetDate,
                    issuer = issuer,
                    tags = tags
                )
            )
        }
    }

    fun deleteCertificate(id: String) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteCertificate(id) }
    }

    // Timetable
    fun toggleSlotCheck(slotTitle: String, currentDone: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = dailySlotLogs.value.find { it.slotTitle == slotTitle }
            if (existing != null) {
                repository.updateSlotLogStatus(existing.id, !currentDone)
            } else {
                repository.insertDailySlotLog(
                    DailySlotLogEntity(
                        dateString = todayDateStr,
                        scheduleTypeUsed = activeScheduleType.value,
                        slotTitle = slotTitle,
                        isTickedDone = true
                    )
                )
            }
        }
    }

    fun addTimetableSlot(type: String, start: String, end: String, title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTimetableConfig(
                TimetableConfigEntity(scheduleType = type, timeSlotStart = start, timeSlotEnd = end, slotTitle = title)
            )
        }
    }

    fun deleteTimetableSlot(slotId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTimetableConfig(slotId)
        }
    }

    fun updateTimetableSlot(slot: TimetableConfigEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTimetableConfig(slot)
        }
    }

    // Priority Notepad
    fun updateScratchpadText(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveScratchpad(
                DailyScratchpadEntity(
                    dateString = todayDateStr,
                    rawMarkdownText = text,
                    layoutPinnedFlag = true,
                    lastUpdated = System.currentTimeMillis()
                )
            )
        }
    }

    fun toggleMarkdownCheckbox(lineIndex: Int) {
        val currentText = scratchpad.value?.rawMarkdownText ?: return
        val lines = currentText.lines().toMutableList()
        if (lineIndex in lines.indices) {
            val line = lines[lineIndex]
            val updatedLine = when {
                line.contains("- [ ]") -> line.replace("- [ ]", "- [x]")
                line.contains("- [x]") -> line.replace("- [x]", "- [ ]")
                line.contains("- [X]") -> line.replace("- [X]", "- [ ]")
                else -> line
            }
            lines[lineIndex] = updatedLine
            updateScratchpadText(lines.joinToString("\n"))
        }
    }

    // Milestones & Alert Engine
    fun addMilestone(title: String, desc: String, targetEpoch: Long, collegeProfile: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertMilestone(
                AcademicMilestoneEntity(
                    eventTitle = title,
                    eventDescription = desc,
                    targetTimestamp = targetEpoch,
                    alertTriggeredFlag = false,
                    collegeProfileId = collegeProfile
                )
            )
        }
    }

    fun deleteMilestone(id: String) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteMilestone(id) }
    }

    fun triggerTestAlert(milestone: AcademicMilestoneEntity) {
        AcademicAlertReceiver.showNotification(
            getApplication(),
            "⚠️ 24h Academic Deadline Alert: ${milestone.eventTitle}",
            "${milestone.eventDescription} | Degree: ${milestone.collegeProfileId}"
        )
    }

    // Upskilling
    fun addCourse(name: String, whenTimeline: String, why: String, cost: Double, prereqs: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertFutureCourse(
                FutureCourseEntity(
                    courseName = name,
                    timelineWhen = whenTimeline,
                    motivationWhy = why,
                    costEstimate = cost,
                    preRequisites = prereqs,
                    isCompleted = false
                )
            )
        }
    }

    fun toggleCourseCompleted(course: FutureCourseEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFutureCourse(course.copy(isCompleted = !course.isCompleted))
        }
    }

    fun deleteCourse(id: String) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteFutureCourse(id) }
    }

    // Career Matrix
    fun addCareerOption(title: String, feasibility: Int, risk: String, steps: String, role: String, salary: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCareerOption(
                CareerOptionEntity(
                    title = title,
                    feasibilityScore = feasibility,
                    riskLevel = risk,
                    actionStepsText = steps,
                    targetRole = role,
                    salaryRange = salary
                )
            )
        }
    }

    fun updateCareerFeasibility(id: String, newScore: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = allCareerOptions.value.find { it.id == id }
            if (existing != null) {
                repository.updateCareerOption(existing.copy(feasibilityScore = newScore))
            }
        }
    }

    fun deleteCareerOption(id: String) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteCareerOption(id) }
    }

    // Resume Builder
    fun saveResumeProfile(profile: ResumeProfileEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveResumeProfile(profile)
        }
    }
}
