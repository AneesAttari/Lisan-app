package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_notes")
data class SavedNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titleEn: String,
    val titleAr: String,
    val englishText: String,
    val arabicComparisonText: String,
    val category: String = "Lesson Note",
    val isPinned: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
