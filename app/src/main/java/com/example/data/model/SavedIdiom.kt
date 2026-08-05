package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "saved_idioms")
data class SavedIdiom(
    @PrimaryKey
    val idiomId: String,
    val savedAt: Long = System.currentTimeMillis()
)
