package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class DailyVerbExample(
    val english: String,
    val urdu: String
)

@Serializable
data class DailyVerb(
    val id: String,
    val baseForm: String,
    val thirdPerson: String,
    val presentParticiple: String,
    val pastSimple: String,
    val pastParticiple: String,
    val urduMeaning: String,
    val examples: List<DailyVerbExample>,
    val usageNoteEn: String,
    val usageNoteUr: String
)

@Entity(
    tableName = "daily_verb_assignments",
    indices = [
        Index(value = ["userId", "verbId"], unique = true),
        Index(value = ["userId", "assignedDate"], unique = true)
    ]
)
@Serializable
data class DailyVerbAssignment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "current-user-id",
    val verbId: String,
    val assignedDate: String, // "yyyy-MM-dd"
    val completedAt: Long? = null,
    val savedAt: Long? = null,
    val practiseStartedAt: Long? = null
)
