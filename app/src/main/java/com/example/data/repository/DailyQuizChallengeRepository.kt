package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.DailyQuizAssignmentEntity
import com.example.data.model.DailyQuizChallengeProgressEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class DailyQuizChallengeRepository(private val database: AppDatabase) {
    private val dao = database.dailyQuizChallengeDao()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    suspend fun getDailyChallengeProgress(userId: String): DailyQuizChallengeProgressEntity = withContext(Dispatchers.IO) {
        dao.getProgress(userId) ?: DailyQuizChallengeProgressEntity(userId, dateFormat.format(Date()))
    }

    suspend fun getOrCreateTodayAssignment(userId: String, questionIds: List<String>): DailyQuizAssignmentEntity = withContext(Dispatchers.IO) {
        val today = dateFormat.format(Date())
        val existing = dao.getAssignment(userId, today)
        if (existing != null) {
            existing
        } else {
            val assignment = DailyQuizAssignmentEntity(
                userId = userId, 
                date = today, 
                questionIds = questionIds.joinToString(",")
            )
            dao.insertAssignment(assignment)
            assignment
        }
    }

    suspend fun updateAssignment(assignment: DailyQuizAssignmentEntity) = withContext(Dispatchers.IO) {
        dao.updateAssignment(assignment)
    }

    suspend fun updateProgress(progress: DailyQuizChallengeProgressEntity) = withContext(Dispatchers.IO) {
        dao.insertProgress(progress)
    }
}
