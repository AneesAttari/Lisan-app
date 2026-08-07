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

import com.example.data.model.ChatMessageEntity
import com.example.data.model.LibraryItem
import com.example.data.model.LikedItem
import com.example.data.model.ConversationSessionEntity
import com.example.data.model.CachedLessonEntity
import com.example.data.model.DailyVerbAssignment

@Database(
    entities = [
        SavedNote::class, 
        SavedIdiom::class, 
        DailyChallengeResultEntity::class, 
        LibraryItem::class, 
        LikedItem::class, 
        ChatMessageEntity::class,
        DailyIdiomAssignment::class,
        DailyChallengeAssignment::class,
        RevisionItem::class,
        AchievementUnlock::class,
        DailyCompletionReward::class,
        ConversationSessionEntity::class,
        CachedLessonEntity::class,
        DailyVerbAssignment::class
    ], 
    version = 9, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedNoteDao(): SavedNoteDao
    abstract fun savedIdiomDao(): SavedIdiomDao
    abstract fun dailyChallengeDao(): DailyChallengeDao
    abstract fun libraryDao(): LibraryDao
    abstract fun likedDao(): LikedDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun dailyIdiomAssignmentDao(): DailyIdiomAssignmentDao
    abstract fun dailyChallengeAssignmentDao(): DailyChallengeAssignmentDao
    abstract fun revisionItemDao(): RevisionItemDao
    abstract fun achievementUnlockDao(): AchievementUnlockDao
    abstract fun dailyCompletionRewardDao(): DailyCompletionRewardDao
    abstract fun conversationSessionDao(): ConversationSessionDao
    abstract fun cachedLessonDao(): CachedLessonDao
    abstract fun dailyVerbAssignmentDao(): DailyVerbAssignmentDao

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
