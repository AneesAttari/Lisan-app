package com.example.data.repository

import com.example.data.local.PictureVocabularyDao
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PictureVocabularyRepository(private val dao: PictureVocabularyDao) {

    suspend fun initializeContentIfNeeded() {
        withContext(Dispatchers.IO) {
            val count = dao.getTotalCategoryCount()
            if (count < 40) {
                seedAllContent()
            }
        }
    }

    fun getAllCategories(): Flow<List<PictureVocabularyCategoryEntity>> = dao.getAllCategories()

    fun getItemsForCategory(categoryId: String): Flow<List<PictureVocabularyItemEntity>> = dao.getItemsForCategory(categoryId)

    fun getProgressForCategory(categoryId: String): Flow<List<PictureVocabularyQuestionProgressEntity>> = dao.getProgressForCategory(categoryId)

    suspend fun getCategoryById(categoryId: String): PictureVocabularyCategoryEntity? = dao.getCategoryById(categoryId)

    suspend fun getItemById(itemId: String): PictureVocabularyItemEntity? = dao.getItemById(itemId)

    suspend fun getItemsListForCategory(categoryId: String): List<PictureVocabularyItemEntity> = dao.getItemsListForCategory(categoryId)

    suspend fun startSession(categoryId: String?, sessionType: String): String {
        return withContext(Dispatchers.IO) {
            val sessionId = "sess_${System.currentTimeMillis()}"
            val questions = if (categoryId != null) {
                dao.getQuestionsListForCategory(categoryId)
            } else {
                dao.getAllQuestionsList().shuffled().take(26)
            }

            val session = PictureVocabularySessionEntity(
                sessionId = sessionId,
                categoryId = categoryId,
                sessionType = sessionType,
                requestedQuestionCount = questions.size,
                actualQuestionCount = questions.size,
                startedAt = System.currentTimeMillis(),
                status = "ACTIVE"
            )
            dao.insertSession(session)

            questions.forEachIndexed { index, q ->
                dao.insertSessionQuestion(
                    PictureVocabularySessionQuestionEntity(
                        sessionId = sessionId,
                        questionId = q.id,
                        displayOrder = index + 1
                    )
                )
            }
            sessionId
        }
    }

    suspend fun getSessionWithQuestions(sessionId: String): Pair<PictureVocabularySessionEntity?, List<Pair<PictureVocabularySessionQuestionEntity, PictureVocabularyQuestionEntity>>> {
        return withContext(Dispatchers.IO) {
            val session = dao.getSessionById(sessionId)
            val sessionQuestions = dao.getSessionQuestionsForSession(sessionId)
            val allQuestionsMap = dao.getAllQuestionsList().associateBy { it.id }
            val mapped = sessionQuestions.mapNotNull { sq ->
                allQuestionsMap[sq.questionId]?.let { q -> sq to q }
            }
            session to mapped
        }
    }

    suspend fun submitAnswer(
        sessionId: String,
        questionId: String,
        selectedItemId: String,
        isCorrect: Boolean
    ) {
        withContext(Dispatchers.IO) {
            val sessionQuestions = dao.getSessionQuestionsForSession(sessionId)
            val targetSq = sessionQuestions.find { it.questionId == questionId }
            if (targetSq != null) {
                dao.updateSessionQuestion(
                    targetSq.copy(
                        selectedItemId = selectedItemId,
                        answeredCorrectly = isCorrect,
                        answeredAt = System.currentTimeMillis()
                    )
                )
            }

            // Update session stats
            val session = dao.getSessionById(sessionId)
            if (session != null) {
                val updatedQuestions = dao.getSessionQuestionsForSession(sessionId)
                val answeredCount = updatedQuestions.count { it.answeredCorrectly != null }
                val correctCount = updatedQuestions.count { it.answeredCorrectly == true }
                val incorrectCount = updatedQuestions.count { it.answeredCorrectly == false }

                val isCompleted = answeredCount >= session.actualQuestionCount
                dao.updateSession(
                    session.copy(
                        correctCount = correctCount,
                        incorrectCount = incorrectCount,
                        completedAt = if (isCompleted) System.currentTimeMillis() else null,
                        status = if (isCompleted) "COMPLETED" else "ACTIVE"
                    )
                )
            }

            // Update question progress
            val question = dao.getAllQuestionsList().find { it.id == questionId }
            val categoryId = question?.categoryId ?: "general"
            val existingProgress = dao.getProgressForQuestion(questionId) ?: PictureVocabularyQuestionProgressEntity(
                questionId = questionId,
                categoryId = categoryId
            )

            val newTimesShown = existingProgress.timesShown + 1
            val newTimesCorrect = existingProgress.timesCorrect + if (isCorrect) 1 else 0
            val newTimesIncorrect = existingProgress.timesIncorrect + if (!isCorrect) 1 else 0
            
            val accuracy = if (newTimesShown > 0) (newTimesCorrect.toFloat() / newTimesShown) * 100f else 0f
            val mastery = accuracy.coerceIn(0f, 100f)

            // Spaced review interval calculation
            val reviewIntervalDays = when {
                newTimesCorrect >= 5 -> 30L
                newTimesCorrect == 4 -> 14L
                newTimesCorrect == 3 -> 7L
                newTimesCorrect == 2 -> 3L
                newTimesCorrect == 1 -> 1L
                else -> 0L
            }
            val nextReview = if (isCorrect) {
                System.currentTimeMillis() + (reviewIntervalDays * 24 * 60 * 60 * 1000L)
            } else {
                System.currentTimeMillis() + (4 * 60 * 60 * 1000L) // 4 hours retry cooldown
            }

            dao.upsertProgress(
                existingProgress.copy(
                    timesShown = newTimesShown,
                    timesCorrect = newTimesCorrect,
                    timesIncorrect = newTimesIncorrect,
                    lastShownAt = System.currentTimeMillis(),
                    lastAnsweredAt = System.currentTimeMillis(),
                    lastAnswerCorrect = isCorrect,
                    lastSelectedItemId = selectedItemId,
                    masteryScore = mastery,
                    nextReviewAt = nextReview
                )
            )
        }
    }

    private suspend fun seedAllContent() {
        dao.deleteAllCategories()
        dao.deleteAllItems()
        dao.deleteAllQuestions()

        val categories = PictureVocabularySeedData.generateSeedCategories()
        val (items, questions) = PictureVocabularySeedData.generateSeedItemsAndQuestions()

        dao.insertCategories(categories)
        dao.insertItems(items)
        dao.insertQuestions(questions)
    }
}
