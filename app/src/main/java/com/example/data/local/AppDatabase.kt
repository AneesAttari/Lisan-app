package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.SavedIdiom
import com.example.data.model.SavedNote
import com.example.data.model.DailyChallengeResultEntity
import com.example.data.model.DailyIdiomAssignment
import com.example.data.model.DailyChallengeAssignment
import com.example.data.model.RevisionItem
import com.example.data.model.AchievementUnlock
import com.example.data.model.DailyCompletionReward

import com.example.data.model.DailyQuizAssignmentEntity
import com.example.data.model.DailyQuizChallengeProgressEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.LibraryItem
import com.example.data.model.LikedItem
import com.example.data.model.DislikedItem
import com.example.data.model.FeedbackEntity
import com.example.data.model.ConversationSessionEntity
import com.example.data.model.CachedLessonEntity
import com.example.data.model.DailyVerbAssignment
import com.example.data.model.PronunciationHistoryItem
import com.example.data.model.RevisionQuestionEntity
import com.example.data.model.QuestionProgressEntity
import com.example.data.model.RevisionSessionEntity
import com.example.data.model.RevisionSessionQuestionEntity
import com.example.data.model.TopicProgressEntity

import com.example.data.model.PictureVocabularyCategoryEntity
import com.example.data.model.PictureVocabularyItemEntity
import com.example.data.model.PictureVocabularyQuestionEntity
import com.example.data.model.PictureVocabularyQuestionProgressEntity
import com.example.data.model.PictureVocabularySessionEntity
import com.example.data.model.PictureVocabularySessionQuestionEntity
import com.example.data.model.AchievementDefinitionEntity
import com.example.data.model.AchievementProgressEntity
import com.example.data.model.LearningEventEntity
import com.example.data.model.DailyLearningSummaryEntity
import com.example.data.model.TutorTimeScheduleEntity
import com.example.data.model.TutorTimeDailyCompletionEntity
import com.example.data.model.RewardTransactionEntity
import com.example.data.model.UserAchievementStatsEntity

@Database(
    entities = [
        SavedNote::class, 
        SavedIdiom::class, 
        DailyChallengeResultEntity::class, 
        LibraryItem::class, 
        LikedItem::class, 
        DislikedItem::class,
        FeedbackEntity::class,
        ChatMessageEntity::class,
        DailyIdiomAssignment::class,
        DailyChallengeAssignment::class,
        RevisionItem::class,
        AchievementUnlock::class,
        DailyCompletionReward::class,
        ConversationSessionEntity::class,
        CachedLessonEntity::class,
        DailyVerbAssignment::class,
        PronunciationHistoryItem::class,
        RevisionQuestionEntity::class,
        QuestionProgressEntity::class,
        RevisionSessionEntity::class,
        RevisionSessionQuestionEntity::class,
        TopicProgressEntity::class,
        PictureVocabularyCategoryEntity::class,
        PictureVocabularyItemEntity::class,
        PictureVocabularyQuestionEntity::class,
        PictureVocabularyQuestionProgressEntity::class,
        PictureVocabularySessionEntity::class,
        PictureVocabularySessionQuestionEntity::class,
        AchievementDefinitionEntity::class,
        AchievementProgressEntity::class,
        LearningEventEntity::class,
        DailyLearningSummaryEntity::class,
        TutorTimeScheduleEntity::class,
        TutorTimeDailyCompletionEntity::class,
        RewardTransactionEntity::class,
        UserAchievementStatsEntity::class,
        DailyQuizChallengeProgressEntity::class,
        DailyQuizAssignmentEntity::class,
        com.example.data.model.ExamSessionEntity::class,
        com.example.data.model.DocumentEntity::class,
        com.example.data.model.DocumentChunkEntity::class
    ], 
    version = 19, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedNoteDao(): SavedNoteDao
    abstract fun savedIdiomDao(): SavedIdiomDao
    abstract fun dailyChallengeDao(): DailyChallengeDao
    abstract fun libraryDao(): LibraryDao
    abstract fun likedDao(): LikedDao
    abstract fun dislikedDao(): DislikedDao
    abstract fun feedbackDao(): FeedbackDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun dailyIdiomAssignmentDao(): DailyIdiomAssignmentDao
    abstract fun dailyChallengeAssignmentDao(): DailyChallengeAssignmentDao
    abstract fun revisionItemDao(): RevisionItemDao
    abstract fun achievementUnlockDao(): AchievementUnlockDao
    abstract fun dailyCompletionRewardDao(): DailyCompletionRewardDao
    abstract fun conversationSessionDao(): ConversationSessionDao
    abstract fun cachedLessonDao(): CachedLessonDao
    abstract fun dailyVerbAssignmentDao(): DailyVerbAssignmentDao
    abstract fun pronunciationHistoryDao(): PronunciationHistoryDao
    abstract fun revisionQuestionDao(): RevisionQuestionDao
    abstract fun questionProgressDao(): QuestionProgressDao
    abstract fun revisionSessionDao(): RevisionSessionDao
    abstract fun revisionSessionQuestionDao(): RevisionSessionQuestionDao
    abstract fun topicProgressDao(): TopicProgressDao
    abstract fun pictureVocabularyDao(): PictureVocabularyDao
    abstract fun lisanJourneyDao(): LisanJourneyDao
    abstract fun dailyQuizChallengeDao(): DailyQuizChallengeDao
    abstract fun examSessionDao(): com.example.data.local.ExamSessionDao
    abstract fun documentDao(): com.example.data.local.DocumentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lisan_grammar_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
