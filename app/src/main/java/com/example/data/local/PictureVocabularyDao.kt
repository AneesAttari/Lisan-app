package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PictureVocabularyCategoryEntity
import com.example.data.model.PictureVocabularyItemEntity
import com.example.data.model.PictureVocabularyQuestionEntity
import com.example.data.model.PictureVocabularyQuestionProgressEntity
import com.example.data.model.PictureVocabularySessionEntity
import com.example.data.model.PictureVocabularySessionQuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PictureVocabularyDao {

    @Query("SELECT * FROM picture_vocab_categories ORDER BY sortOrder ASC")
    fun getAllCategories(): Flow<List<PictureVocabularyCategoryEntity>>

    @Query("SELECT * FROM picture_vocab_categories ORDER BY sortOrder ASC")
    suspend fun getAllCategoriesList(): List<PictureVocabularyCategoryEntity>

    @Query("SELECT * FROM picture_vocab_categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: String): PictureVocabularyCategoryEntity?

    @Query("SELECT * FROM picture_vocab_items WHERE categoryId = :categoryId")
    fun getItemsForCategory(categoryId: String): Flow<List<PictureVocabularyItemEntity>>

    @Query("SELECT * FROM picture_vocab_items WHERE categoryId = :categoryId")
    suspend fun getItemsListForCategory(categoryId: String): List<PictureVocabularyItemEntity>

    @Query("SELECT * FROM picture_vocab_items")
    suspend fun getAllItemsList(): List<PictureVocabularyItemEntity>

    @Query("SELECT * FROM picture_vocab_items WHERE id = :itemId")
    suspend fun getItemById(itemId: String): PictureVocabularyItemEntity?

    @Query("SELECT * FROM picture_vocab_questions WHERE categoryId = :categoryId")
    suspend fun getQuestionsListForCategory(categoryId: String): List<PictureVocabularyQuestionEntity>

    @Query("SELECT * FROM picture_vocab_questions")
    suspend fun getAllQuestionsList(): List<PictureVocabularyQuestionEntity>

    @Query("SELECT * FROM picture_vocab_progress WHERE categoryId = :categoryId")
    fun getProgressForCategory(categoryId: String): Flow<List<PictureVocabularyQuestionProgressEntity>>

    @Query("SELECT * FROM picture_vocab_progress WHERE questionId = :questionId")
    suspend fun getProgressForQuestion(questionId: String): PictureVocabularyQuestionProgressEntity?

    @Query("DELETE FROM picture_vocab_categories")
    suspend fun deleteAllCategories()

    @Query("DELETE FROM picture_vocab_items")
    suspend fun deleteAllItems()

    @Query("DELETE FROM picture_vocab_questions")
    suspend fun deleteAllQuestions()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<PictureVocabularyCategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<PictureVocabularyItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<PictureVocabularyQuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: PictureVocabularyQuestionProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PictureVocabularySessionEntity)

    @Update
    suspend fun updateSession(session: PictureVocabularySessionEntity)

    @Query("SELECT * FROM picture_vocab_sessions WHERE sessionId = :sessionId")
    suspend fun getSessionById(sessionId: String): PictureVocabularySessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessionQuestion(sessionQuestion: PictureVocabularySessionQuestionEntity)

    @Update
    suspend fun updateSessionQuestion(sessionQuestion: PictureVocabularySessionQuestionEntity)

    @Query("SELECT * FROM picture_vocab_session_questions WHERE sessionId = :sessionId ORDER BY displayOrder ASC")
    suspend fun getSessionQuestionsForSession(sessionId: String): List<PictureVocabularySessionQuestionEntity>

    @Query("SELECT * FROM picture_vocab_progress WHERE nextReviewAt > 0 AND nextReviewAt <= :currentTime")
    suspend fun getDueQuestions(currentTime: Long): List<PictureVocabularyQuestionProgressEntity>

    @Query("SELECT * FROM picture_vocab_progress WHERE timesIncorrect > timesCorrect ORDER BY lastShownAt ASC")
    suspend fun getWeakQuestions(): List<PictureVocabularyQuestionProgressEntity>

    @Query("SELECT COUNT(*) FROM picture_vocab_questions")
    suspend fun getTotalQuestionCount(): Int

    @Query("SELECT COUNT(*) FROM picture_vocab_categories")
    suspend fun getTotalCategoryCount(): Int
}
