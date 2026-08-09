package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.repository.PictureVocabularyImageMap

@Entity(tableName = "picture_vocab_categories")
data class PictureVocabularyCategoryEntity(
    @PrimaryKey val id: String,
    val displayNameEnglish: String,
    val displayNameArabic: String,
    val displayNameUrdu: String,
    val descriptionEnglish: String,
    val iconName: String,
    val sortOrder: Int,
    val totalQuestionCount: Int = 26
)

@Entity(tableName = "picture_vocab_items")
data class PictureVocabularyItemEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val wordEnglish: String,
    val wordArabic: String,
    val wordUrdu: String,
    val romanUrdu: String?,
    val exampleSentenceEnglish: String,
    val exampleSentenceUrdu: String?,
    val imageAssetKey: String,
    val pronunciationText: String,
    val ipa: String? = null,
    val imageUrl: String? = null,
    val imageSearchQuery: String? = null,
    val alt: String? = null
) {
    fun getEffectiveImageUrl(): String {
        return if (!imageUrl.isNullOrBlank()) imageUrl else PictureVocabularyImageMap.getImageUrlForWord(wordEnglish, imageAssetKey)
    }

    fun getEffectiveAlt(): String {
        return if (!alt.isNullOrBlank()) alt else "Picture of $wordEnglish"
    }

    fun getEffectiveSearchQuery(): String {
        return if (!imageSearchQuery.isNullOrBlank()) imageSearchQuery else PictureVocabularyImageMap.getSearchQueryForWord(wordEnglish)
    }
}

@Entity(tableName = "picture_vocab_questions")
data class PictureVocabularyQuestionEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val targetItemId: String,
    val optionItemIdsString: String, // comma-separated 4 item IDs
    val correctItemId: String,
    val normalizedFingerprint: String
) {
    fun getOptions(): List<String> = optionItemIdsString.split(",")
}

@Entity(tableName = "picture_vocab_progress")
data class PictureVocabularyQuestionProgressEntity(
    @PrimaryKey val questionId: String,
    val categoryId: String,
    val timesShown: Int = 0,
    val timesCorrect: Int = 0,
    val timesIncorrect: Int = 0,
    val lastShownAt: Long = 0L,
    val lastAnsweredAt: Long = 0L,
    val lastAnswerCorrect: Boolean? = null,
    val lastSelectedItemId: String? = null,
    val masteryScore: Float = 0f, // 0.0 to 100.0
    val nextReviewAt: Long = 0L
)

@Entity(tableName = "picture_vocab_sessions")
data class PictureVocabularySessionEntity(
    @PrimaryKey val sessionId: String,
    val categoryId: String?,
    val sessionType: String, // "LEARN", "QUIZ", "REVIEW", "RETRIES", "MIXED"
    val requestedQuestionCount: Int,
    val actualQuestionCount: Int,
    val startedAt: Long,
    val completedAt: Long? = null,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val status: String // "ACTIVE", "COMPLETED"
)

@Entity(tableName = "picture_vocab_session_questions")
data class PictureVocabularySessionQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val sessionId: String,
    val questionId: String,
    val displayOrder: Int,
    val shownAt: Long = System.currentTimeMillis(),
    val selectedItemId: String? = null,
    val answeredCorrectly: Boolean? = null,
    val answeredAt: Long? = null
)

data class PictureVocabularySessionSummary(
    val sessionId: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val accuracy: Float,
    val status: String
)
