package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.CachedLessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedLessonDao {
    @Query("SELECT * FROM cached_lessons ORDER BY timestamp DESC LIMIT 10")
    fun getLast10Lessons(): Flow<List<CachedLessonEntity>>

    @Query("SELECT * FROM cached_lessons ORDER BY timestamp DESC LIMIT 10")
    suspend fun getLast10LessonsList(): List<CachedLessonEntity>

    @Query("SELECT * FROM cached_lessons WHERE topicId = :topicId")
    suspend fun getLesson(topicId: String): CachedLessonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: CachedLessonEntity)

    @Query("DELETE FROM cached_lessons")
    suspend fun clearAllLessons()

    @Query("DELETE FROM cached_lessons WHERE topicId NOT IN (SELECT topicId FROM cached_lessons ORDER BY timestamp DESC LIMIT 10)")
    suspend fun trimOldLessons()
}
