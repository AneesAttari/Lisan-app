package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.SavedIdiom
import com.example.data.model.SavedNote
import com.example.data.model.DailyChallengeResultEntity

import com.example.data.model.ChatMessageEntity
import com.example.data.model.LibraryItem
import com.example.data.model.LikedItem

@Database(entities = [SavedNote::class, SavedIdiom::class, DailyChallengeResultEntity::class, LibraryItem::class, LikedItem::class, ChatMessageEntity::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedNoteDao(): SavedNoteDao
    abstract fun savedIdiomDao(): SavedIdiomDao
    abstract fun dailyChallengeDao(): DailyChallengeDao
    abstract fun libraryDao(): LibraryDao
    abstract fun likedDao(): LikedDao
    abstract fun chatMessageDao(): ChatMessageDao

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
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
