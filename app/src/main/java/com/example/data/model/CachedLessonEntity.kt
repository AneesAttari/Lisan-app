package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_lessons")
data class CachedLessonEntity(
    @PrimaryKey val topicId: String,
    val topicTitleEn: String,
    val topicTitleUr: String,
    val lessonJson: String,
    val timestamp: Long = System.currentTimeMillis()
)
