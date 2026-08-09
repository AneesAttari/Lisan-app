package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LisanJourneyDao {

    // --- Definitions ---
    @Query("SELECT * FROM achievement_definitions WHERE isActive = 1 ORDER BY sortOrder ASC")
    fun getAllActiveDefinitions(): Flow<List<AchievementDefinitionEntity>>

    @Query("SELECT * FROM achievement_definitions WHERE id = :id")
    suspend fun getDefinitionById(id: String): AchievementDefinitionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefinitions(definitions: List<AchievementDefinitionEntity>)

    // --- Progress ---
    @Query("SELECT * FROM achievement_progress")
    fun getAllProgress(): Flow<List<AchievementProgressEntity>>

    @Query("SELECT * FROM achievement_progress WHERE achievementId = :achievementId")
    suspend fun getProgress(achievementId: String): AchievementProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: AchievementProgressEntity)

    // --- Learning Events ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvent(event: LearningEventEntity): Long

    @Query("SELECT * FROM learning_events WHERE localDate = :localDate")
    suspend fun getEventsForDate(localDate: String): List<LearningEventEntity>

    @Query("SELECT COUNT(*) FROM learning_events WHERE eventType = :eventType")
    suspend fun getEventCountByType(eventType: String): Long

    // --- Daily Summaries ---
    @Query("SELECT * FROM daily_learning_summaries WHERE localDate = :localDate")
    suspend fun getDailySummary(localDate: String): DailyLearningSummaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailySummary(summary: DailyLearningSummaryEntity)

    // --- Tutor Time Schedules ---
    @Query("SELECT * FROM tutor_time_schedules WHERE id = :scheduleId")
    fun getTutorScheduleFlow(scheduleId: String = "default_tutor_schedule"): Flow<TutorTimeScheduleEntity?>

    @Query("SELECT * FROM tutor_time_schedules WHERE id = :scheduleId")
    suspend fun getTutorSchedule(scheduleId: String = "default_tutor_schedule"): TutorTimeScheduleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTutorSchedule(schedule: TutorTimeScheduleEntity)

    // --- Tutor Time Completions ---
    @Query("SELECT * FROM tutor_time_daily_completions WHERE localDate = :localDate")
    suspend fun getTutorCompletionForDate(localDate: String): TutorTimeDailyCompletionEntity?

    @Query("SELECT * FROM tutor_time_daily_completions ORDER BY localDate DESC")
    fun getAllTutorCompletions(): Flow<List<TutorTimeDailyCompletionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTutorCompletion(completion: TutorTimeDailyCompletionEntity)

    // --- Reward Transactions ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRewardTransaction(tx: RewardTransactionEntity): Long

    @Query("SELECT * FROM reward_transactions WHERE status != 'CLAIMED'")
    fun getPendingRewards(): Flow<List<RewardTransactionEntity>>

    @Query("SELECT * FROM reward_transactions WHERE id = :txId")
    suspend fun getRewardTransaction(txId: String): RewardTransactionEntity?

    @Update
    suspend fun updateRewardTransaction(tx: RewardTransactionEntity)

    // --- User Achievement Stats ---
    @Query("SELECT * FROM user_achievement_stats WHERE id = 'default_user_stats'")
    fun getUserStatsFlow(): Flow<UserAchievementStatsEntity?>

    @Query("SELECT * FROM user_achievement_stats WHERE id = 'default_user_stats'")
    suspend fun getUserStats(): UserAchievementStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserStats(stats: UserAchievementStatsEntity)
}
