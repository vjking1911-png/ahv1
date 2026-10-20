package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ExpenseEntity::class,
        SemesterEntity::class,
        SubjectEntity::class,
        TopicEntity::class,
        DegreeProfileEntity::class,
        SemesterGradeEntity::class,
        SubjectMarkEntity::class,
        GatePaperEntity::class,
        GateTopicEntity::class,
        EseSectionEntity::class,
        EseTopicEntity::class,
        CertificateEntity::class,
        TimetableConfigEntity::class,
        DailySlotLogEntity::class,
        DailyScratchpadEntity::class,
        AcademicMilestoneEntity::class,
        FutureCourseEntity::class,
        CareerOptionEntity::class,
        ResumeProfileEntity::class,
        SubjectExamEntity::class,
        GateSubjectEntity::class,
        EsePaperEntity::class,
        EseSubjectEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AcademicDatabase : RoomDatabase() {
    abstract fun academicDao(): AcademicDao

    companion object {
        @Volatile
        private var INSTANCE: AcademicDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AcademicDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AcademicDatabase::class.java,
                    "academic_hub.db"
                ).addCallback(AcademicDatabaseCallback(scope, context))
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AcademicDatabaseCallback(
            private val scope: CoroutineScope,
            private val context: Context
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        InitialDataSeeder.seedInitialData(database.academicDao())
                    }
                }
            }
        }
    }
}
