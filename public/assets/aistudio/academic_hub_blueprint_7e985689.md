# Academic Hub App - Comprehensive Offline Architecture Blueprint
This technical documentation maps out the modular, offline-first design for the unified Academic Hub application.

## 🏛️ Core Architecture Design
To maintain complete isolation between features while enabling shared layout and configuration paradigms, the app leverages a **Core-Shell Micro-Frontend Architecture**.

### 1. Unified Storage Strategy
- **Relational Data (Modules 1-6, 8-12):** Single SQLite database instance managing distinct tables with dynamic scoping.
- **Unstructured Files (Modules 7 & 13):** Device standard app-private Scoped Storage (`/data/user/0/app.package/files/`).
- **Keys & Configuration:** Local Key-Value store (Jetpack Preferences DataStore / Hive) for app-level configurations (e.g., active timetable choice).

---

## 📦 Detailed Module Specifications & Custom Tracking Enhancements

### Module 1: Academic Expense Tracker
- **Tinkered Enhancements:** Added an **"Impact Factor"** (`RETURN_ON_INVESTMENT_RATING`) to log if an expense directly contributed to passing/failing or scoring higher in a subject, alongside a **"Payment Plan Type"** (One-time vs subscription) to gauge ongoing financial drains during a semester.
- **Schema:**
  - `id`: TEXT (UUID, Primary Key)
  - `title`: TEXT
  - `amount`: REAL
  - `category`: TEXT (Enum: BOOKS, SUBSCRIPTION, FEES, STATIONERY, EXAM_REGISTRATION)
  - `payment_type`: TEXT (ONETIME, RECURRING)
  - `timestamp`: INTEGER (Epoch milliseconds)
  - `roi_rating`: INTEGER (1 to 5 stars or null)
  - `college_profile_id`: TEXT (Nullable, links expenses directly to Degree 1 or Degree 2)

### Modules 2 & 3: Dual College Syllabus Trackers
- **Tinkered Enhancements:** Added **"Weightage Coefficient"** and **"Estimated Hours Required"** for each topic to enable local smart calculation of metrics like *Syllabus Backlog Index* or *Velocity*.
- **Schema Shared Context:** Separated cleanly via a `profile_scope` flag ('COLLEGE_1' or 'COLLEGE_2').
- **Schema Tables:**
  - `semesters`: `[id (PK), profile_scope, semester_number, is_active, gpa_earned]`
  - `subjects`: `[id (PK), semester_id, subject_name, subject_code, credits]`
  - `topics`: `[id (PK), subject_id, topic_title, status (TODO, DOING, REVISING, DONE), weightage, estimated_hours]`

### Module 4: Dual-Degree Unified Result & CGPA Tracker
- **Tinkered Enhancements:** Added an explicit **"Formula Engine Evaluation Meta"** column. This evaluates dynamic user expressions strings securely at runtime using parsing logic without script execution vectors.
- **Schema:**
  - `degree_profiles`: `[id (PK), degree_name, calculation_formula_string]` (e.g., `"CGPA * 9.5"`, `"CGPA * 10"`)
  - `semester_grades`: `[id (PK), degree_profile_id, semester_number, sgpa, total_credits_earned]`
  - `subject_marks`: `[id (PK), semester_id, subject_name, marks_obtained, max_marks, grade_point, credits]`
- **Local Engine Computation Logic:**
  $$\text{CGPA} = \frac{\sum (\text{Grade Point} \times \text{Credits})}{\sum \text{Credits}}$$
  $$\text{Final Score Percentage} = \text{Eval}(\text{calculation\_formula\_string}, \text{replacing 'CGPA' with calculated metric})$$

### Module 5: GATE Exam Syllabus Tracker (3 Papers)
- **Tinkered Enhancements:** Added a **"Revision Count Tracker"** and a **"Confidence Score"** metric since competitive exams require repeated cycles over strict schedules.
- **Schema:**
  - `gate_papers`: `[id (PK), paper_code, paper_name]` (e.g., CS, EE, ME)
  - `gate_topics`: `[id (PK), paper_id, subject_category, topic_name, status, start_date (TEXT/ISO8601), end_date (TEXT/ISO8601), revision_count (INTEGER), confidence_level (1-5)]`

### Module 6: ESE UPSC Syllabus Tracker
- **Tinkered Enhancements:** Segregated by **Stage 1 (Prelims: General Studies + Technical)** and **Stage 2 (Mains: Conventional Technical)** to accurately reflect UPSC's competitive pipeline schema.
- **Schema:**
  - `ese_sections`: `[id (PK), stage_type (PRELIMS_GS, PRELIMS_TECH, MAINS_TECH), paper_title]`
  - `ese_topics`: `[id (PK), section_id, topic_name, status, pyq_solved_count, notes_attached_flag]`

### Module 7: Unified Certificates Digital Hub
- **Tinkered Enhancements:** Integrated metadata links separating missing checklist assets from actual stored physical files, mapping directly to physical absolute app paths.
- **Schema:**
  - `certificates`: `[id (PK), certificate_name, status (WANTED, OWNED), file_path (TEXT, Nullable), target_acquisition_date, tags]`

### Module 8: Flexible Holiday/Working-Day Dual Timetable
- **Tinkered Enhancements:** Added a **"Completion Streak Counter"** and local time window restrictions to enforce focus routines.
- **Schema:**
  - `timetable_configs`: `[id (PK), schedule_type (WORKING_DAY, HOLIDAY), time_slot_start, time_slot_end, slot_title, associated_module_id (Nullable)]`
  - `daily_slot_logs`: `[id (PK), date_string (YYYY-MM-DD), schedule_type_used, slot_title, is_ticked_done (INTEGER: 0/1)]`

### Module 9: Front Page Priority Notepad
- **Schema:**
  - `daily_scratchpad`: `[date_string (PK), raw_markdown_text, layout_pinned_flag]`

### Module 10: Academic Semester Writeup & Proactive Alert Engine
- **Tinkered Enhancements:** Implemented an off-line localized cron schedule architecture using **Android WorkManager APIs** checking tasks exactly 24 hours prior to deadline time stamps.
- **Schema:**
  - `academic_milestones`: `[id (PK), event_title, event_description, target_timestamp, alert_triggered_flag, college_profile_id]`

### Module 11: Future Upskilling Checklist
- **Schema:**
  - `future_courses`: `[id (PK), course_name, timeline_when, motivation_why, cost_estimate, pre_requisites]`

### Module 12: Feasible Career Options Matrix
- **Tinkered Enhancements:** Added **"Feasibility Index"** and **"Risk Level"** metrics to allow sorting choices logically.
- **Schema:**
  - `career_options`: `[id (PK), title, feasibility_score (1-10), risk_level (LOW, MED, HIGH), action_steps_text]`

### Module 13: Local Tabular Resume Builder
- **Schema:**
  - `resume_profiles`: `[id (PK), profile_name, layout_template_type]`
  - `resume_sections`: `[id (PK), resume_profile_id, heading, bullet_points_json, section_order]`

---

## 🛠️ App-Wide Offline Implementation Tech Stack Guidance
1. **Frontend:** **Flutter** (for absolute package modularity via clean pubspec structures) or **Native Kotlin (Jetpack Compose)**.
2. **Local Database:** SQLite optimized with **Room Database** (Android Native) or **Drift** (Flutter).
3. **Notifications:** Local Broadcast Receiver paired via Android AlarmManager or WorkManager.
