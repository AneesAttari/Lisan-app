package com.example.data.local

import androidx.room.*
import com.example.data.model.DailyChallengeResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyChallengeDao {
    @Query("SELECT * FROM daily_challenge_results WHERE date = :date")
    fun getResultsByDate(date: String): Flow<List<DailyChallengeResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: DailyChallengeResultEntity)

    @Query("SELECT * FROM daily_challenge_results ORDER BY date DESC")
    fun getAllResults(): Flow<List<DailyChallengeResultEntity>>

    @Query("SELECT COUNT(*) FROM daily_challenge_results WHERE isCorrect = 1")
    fun getTotalCorrectAnswers(): Flow<Int>
}
