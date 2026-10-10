package com.example.data.local

import androidx.room.*
import com.example.data.model.DailyQuizChallengeProgressEntity
import com.example.data.model.DailyQuizAssignmentEntity

@Dao
interface DailyQuizChallengeDao {
    @Query("SELECT * FROM daily_quiz_challenge_progress WHERE userId = :userId")
    suspend fun getProgress(userId: String): DailyQuizChallengeProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: DailyQuizChallengeProgressEntity)

    @Query("SELECT * FROM daily_quiz_assignments WHERE userId = :userId AND date = :date")
    suspend fun getAssignment(userId: String, date: String): DailyQuizAssignmentEntity?

    @Query("SELECT * FROM daily_quiz_assignments WHERE userId = :userId")
    suspend fun getAllAssignments(userId: String): List<DailyQuizAssignmentEntity>

    @Query("SELECT * FROM daily_quiz_assignments WHERE userId = :userId AND date IN (:dates)")
    suspend fun getAssignmentsForDates(userId: String, dates: List<String>): List<DailyQuizAssignmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: DailyQuizAssignmentEntity)

    @Update
    suspend fun updateAssignment(assignment: DailyQuizAssignmentEntity)
}
