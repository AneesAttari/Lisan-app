package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "achievement_definitions",
    indices = [Index(value = ["groupId"]), Index(value = ["requirementType"])]
)
data class AchievementDefinitionEntity(
    @PrimaryKey val id: String,
    val groupId: String, // getting_started, daily_habit, streak, tutor, grammar_revision, picture_vocabulary, pronunciation, sentence_analyzer, daily_challenge, library_notes
    val titleEnglish: String,
    val titleUrdu: String?,
    val descriptionEnglish: String,
    val descriptionUrdu: String?,
    val iconKey: String,
    val requirementType: String,
    val targetValue: Long,
    val tier: Int = 1,
    val rewardType: String? = "LISAN_POINTS",
    val rewardValue: Long? = 50,
    val isRepeatable: Boolean = false,
    val sortOrder: Int = 0,
    val isActive: Boolean = true,
    val contentVersion: Int = 1
)

@Entity(
    tableName = "achievement_progress",
    indices = [Index(value = ["achievementId"])]
)
data class AchievementProgressEntity(
    @PrimaryKey val achievementId: String,
    val currentValue: Long = 0,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val isRewardClaimed: Boolean = false,
    val rewardClaimedAt: Long? = null,
    val lastUpdatedAt: Long = System.currentTimeMillis(),
    val lastCelebratedAt: Long? = null,
    val version: Int = 1
)

@Entity(
    tableName = "learning_events",
    indices = [
        Index(value = ["eventType"]),
        Index(value = ["localDate"]),
        Index(value = ["sessionId"]),
        Index(value = ["deduplicationKey"], unique = true)
    ]
)
data class LearningEventEntity(
    @PrimaryKey val id: String,
    val eventType: String,
    val featureSource: String, // TUTOR_CHAT, GRAMMAR_LESSON, REVISION, PICTURE_VOCABULARY, SENTENCE_ANALYZER, PRONUNCIATION, DAILY_CHALLENGE, LIBRARY, NOTES
    val occurredAt: Long,
    val localDate: String, // yyyy-MM-dd
    val entityId: String? = null,
    val sessionId: String? = null,
    val metadataJson: String? = null,
    val deduplicationKey: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "daily_learning_summaries"
)
data class DailyLearningSummaryEntity(
    @PrimaryKey val localDate: String, // yyyy-MM-dd
    val completedActivityTypesJson: String = "[]",
    val totalMeaningfulActivities: Int = 0,
    val tutorConversationCompleted: Boolean = false,
    val grammarActivityCompleted: Boolean = false,
    val revisionActivityCompleted: Boolean = false,
    val pictureVocabularyActivityCompleted: Boolean = false,
    val pronunciationActivityCompleted: Boolean = false,
    val sentenceAnalysisCompleted: Boolean = false,
    val dailyChallengeCompleted: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "tutor_time_schedules"
)
data class TutorTimeScheduleEntity(
    @PrimaryKey val id: String = "default_tutor_schedule",
    val isEnabled: Boolean = false,
    val startTime: String = "20:00", // HH:mm
    val endTime: String = "21:00", // HH:mm
    val selectedDaysJson: String = "[1,2,3,4,5,6,7]", // 1 = Monday ... 7 = Sunday
    val remindersEnabled: Boolean = true,
    val reminderLeadMinutes: Int = 10,
    val timezoneId: String = "UTC",
    val effectiveFromLocalDate: String = "",
    val pausedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "tutor_time_daily_completions",
    indices = [Index(value = ["localDate"], unique = true)]
)
data class TutorTimeDailyCompletionEntity(
    @PrimaryKey val localDate: String, // yyyy-MM-dd (logical scheduled day)
    val scheduleId: String,
    val tutorSessionId: String,
    val completedAt: Long,
    val wasInsideWindow: Boolean,
    val rewardGranted: Boolean,
    val streakCountAfterCompletion: Int,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "reward_transactions",
    indices = [Index(value = ["deduplicationKey"], unique = true), Index(value = ["achievementId"])]
)
data class RewardTransactionEntity(
    @PrimaryKey val id: String,
    val achievementId: String? = null,
    val rewardType: String, // LISAN_POINTS, BADGE, BONUS_TOKEN
    val amount: Long? = null,
    val sourceType: String, // ACHIEVEMENT, DAILY_TUTOR_TIME, STREAK_MILESTONE
    val sourceId: String,
    val awardedAt: Long = System.currentTimeMillis(),
    val claimedAt: Long? = null,
    val status: String = "PENDING", // PENDING, CLAIMED, AUTO_AWARDED
    val deduplicationKey: String
)

@Entity(
    tableName = "user_achievement_stats"
)
data class UserAchievementStatsEntity(
    @PrimaryKey val id: String = "default_user_stats",
    val currentLearningStreak: Int = 0,
    val bestLearningStreak: Int = 0,
    val currentOnTimeTutorStreak: Int = 0,
    val bestOnTimeTutorStreak: Int = 0,
    val totalTutorConversations: Long = 0,
    val totalOnTimeTutorCompletions: Long = 0,
    val totalGrammarLessonsCompleted: Long = 0,
    val totalRevisionSessionsCompleted: Long = 0,
    val totalPictureVocabularyWordsLearned: Long = 0,
    val totalPictureVocabularyCategoriesCompleted: Long = 0,
    val totalPronunciationSessionsCompleted: Long = 0,
    val totalSentenceAnalysesCompleted: Long = 0,
    val totalDailyChallengesCompleted: Long = 0,
    val totalAchievementsUnlocked: Long = 0,
    val lisanPointsBalance: Long = 0,
    val updatedAt: Long = System.currentTimeMillis()
)
