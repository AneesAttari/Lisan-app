package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class RevisionRepository(private val database: AppDatabase) {

    private val questionDao = database.revisionQuestionDao()
    private val progressDao = database.questionProgressDao()
    private val sessionDao = database.revisionSessionDao()
    private val sessionQuestionDao = database.revisionSessionQuestionDao()
    private val topicProgressDao = database.topicProgressDao()

    val topicProgressFlow: Flow<List<TopicProgressEntity>> = topicProgressDao.getAllTopicProgressFlow()

    /**
     * Seeds the local question bank with exactly 100 questions per topic if not already seeded.
     * Runs safely on the IO dispatcher.
     */
    suspend fun seedQuestionBankIfNeeded() = withContext(Dispatchers.IO) {
        val count = questionDao.getAllQuestions().size
        if (count < 400) {
            // Generate all high-quality questions
            val questions = QuestionBankSeeder.generateAllQuestions()
            questionDao.insertQuestions(questions)

            // Seed initial question progress and topic progress for the 4 supported topics
            val topics = listOf("verb_to_be", "articles_a_an_the", "present_simple", "present_continuous")
            
            for (topicId in topics) {
                val topicQuestions = questions.filter { it.topicId == topicId }
                val totalQ = topicQuestions.size
                
                // Initialize topic progress
                val initialTopicProgress = TopicProgressEntity(
                    topicId = topicId,
                    totalQuestions = totalQ,
                    unseenCount = totalQ,
                    dueCount = 0,
                    weakCount = 0,
                    masteredCount = 0,
                    overallMastery = 0,
                    nextReviewAt = null
                )
                topicProgressDao.insertTopicProgress(initialTopicProgress)

                // Initialize progress entries
                for (q in topicQuestions) {
                    val existing = progressDao.getProgressForQuestion(q.id)
                    if (existing == null) {
                        progressDao.insertProgress(
                            QuestionProgressEntity(
                                questionId = q.id,
                                topicId = topicId,
                                timesSeen = 0,
                                timesCorrect = 0,
                                timesIncorrect = 0,
                                lastShownAt = 0L,
                                lastAnsweredAt = 0L,
                                lastAnswerCorrect = null,
                                masteryScore = 0,
                                nextEligibleAt = 0L
                            )
                        )
                    }
                }
            }
        }
    }

    /**
     * Gets the current overall summary statistics for the dashboard.
     */
    suspend fun getDashboardSummary(): DashboardSummary = withContext(Dispatchers.IO) {
        val progressList = progressDao.getAllProgress()
        val totalQuestions = progressList.size
        
        val unseenCount = progressList.count { it.timesSeen == 0 }
        val currentTime = System.currentTimeMillis()
        val dueCount = progressList.count { it.timesSeen > 0 && it.nextEligibleAt <= currentTime }
        val weakCount = progressList.count { it.timesSeen > 0 && it.masteryScore < 50 }
        val masteredCount = progressList.count { it.timesSeen > 0 && it.masteryScore >= 80 }
        
        val overallMastery = if (totalQuestions > 0) {
            val totalScore = progressList.sumOf { it.masteryScore }
            totalScore / totalQuestions
        } else {
            0
        }

        DashboardSummary(
            totalQuestions = totalQuestions,
            unseenCount = unseenCount,
            dueCount = dueCount,
            weakCount = weakCount,
            masteredCount = masteredCount,
            overallMastery = overallMastery
        )
    }

    /**
     * Re-calculates and updates progress statistics for a given topic.
     */
    private suspend fun recalculateTopicProgress(topicId: String) {
        val topicQuestions = questionDao.getQuestionsForTopic(topicId)
        val totalQ = topicQuestions.size
        if (totalQ == 0) return

        val progressList = progressDao.getAllProgressForTopic(topicId)
        val unseenCount = totalQ - progressList.count { it.timesSeen > 0 }
        
        val currentTime = System.currentTimeMillis()
        val dueCount = progressList.count { it.timesSeen > 0 && it.nextEligibleAt <= currentTime }
        val weakCount = progressList.count { it.timesSeen > 0 && it.masteryScore < 50 }
        val masteredCount = progressList.count { it.timesSeen > 0 && it.masteryScore >= 80 }

        val overallMastery = if (totalQ > 0) {
            val totalScore = progressList.sumOf { it.masteryScore }
            totalScore / totalQ
        } else {
            0
        }

        val nextReviewAt = progressList
            .filter { it.timesSeen > 0 && it.nextEligibleAt > currentTime }
            .map { it.nextEligibleAt }
            .minOrNull()

        val updated = TopicProgressEntity(
            topicId = topicId,
            totalQuestions = totalQ,
            unseenCount = unseenCount,
            dueCount = dueCount,
            weakCount = weakCount,
            masteredCount = masteredCount,
            overallMastery = overallMastery,
            nextReviewAt = nextReviewAt,
            updatedAt = currentTime
        )
        topicProgressDao.insertTopicProgress(updated)
    }

    /**
     * Starts a revision session based on selection criteria and anti-repetition rules.
     * Returns a RevisionSessionEntity with nested questions pre-inserted.
     */
    suspend fun startRevisionSession(
        sessionType: String,
        topicId: String?,
        requestedCount: Int
    ): RevisionSessionEntity = withContext(Dispatchers.IO) {
        val currentTime = System.currentTimeMillis()
        
        // 1. Get candidate questions
        val allQuestions = if (topicId != null) {
            questionDao.getQuestionsForTopic(topicId)
        } else {
            questionDao.getAllQuestions()
        }

        if (allQuestions.isEmpty()) {
            throw IllegalStateException("No questions available for revision in this selection.")
        }

        // 2. Load progress for candidates
        val progressMap = progressDao.getAllProgress().associateBy { it.questionId }

        // 3. Score and prioritize questions
        // Priority order:
        // - Due questions (nextEligibleAt <= current time)
        // - Unseen questions (timesSeen == 0)
        // - Weakest questions (lowest masteryScore)
        // We also want to shuffle within groups so sessions feel dynamic, but respect anti-repetition!
        val prioritizedQuestions = allQuestions.sortedWith { q1, q2 ->
            val p1 = progressMap[q1.id]
            val p2 = progressMap[q2.id]
            
            val isDue1 = p1 != null && p1.timesSeen > 0 && p1.nextEligibleAt <= currentTime
            val isDue2 = p2 != null && p2.timesSeen > 0 && p2.nextEligibleAt <= currentTime
            
            val isUnseen1 = p1 == null || p1.timesSeen == 0
            val isUnseen2 = p2 == null || p2.timesSeen == 0

            when {
                // Due questions first
                isDue1 && !isDue2 -> -1
                !isDue1 && isDue2 -> 1
                // Unseen questions second
                isUnseen1 && !isUnseen2 -> -1
                !isUnseen1 && isUnseen2 -> 1
                // Else compare mastery score (lower score is weak, so show first)
                else -> {
                    val m1 = p1?.masteryScore ?: 0
                    val m2 = p2?.masteryScore ?: 0
                    m1.compareTo(m2)
                }
            }
        }

        // Apply a small shuffle among top candidates to avoid exact repeating sequences on different days
        val topSize = (requestedCount * 2).coerceAtMost(prioritizedQuestions.size)
        val selectedCandidates = prioritizedQuestions.take(topSize).shuffled().take(requestedCount)

        val sessionId = UUID.randomUUID().toString()
        val session = RevisionSessionEntity(
            sessionId = sessionId,
            sessionType = sessionType,
            topicId = topicId,
            requestedQuestionCount = requestedCount,
            actualQuestionCount = selectedCandidates.size,
            startedAt = currentTime,
            status = "started"
        )
        sessionDao.insertSession(session)

        // Insert session questions
        selectedCandidates.forEachIndexed { index, q ->
            sessionQuestionDao.insertSessionQuestion(
                RevisionSessionQuestionEntity(
                    sessionId = sessionId,
                    questionId = q.id,
                    displayOrder = index,
                    shownAt = currentTime
                )
            )
        }

        return@withContext session
    }

    /**
     * Loads the questions and their current session records for a given active session.
     */
    suspend fun getSessionQuestions(sessionId: String): List<Pair<RevisionQuestionEntity, RevisionSessionQuestionEntity>> = withContext(Dispatchers.IO) {
        val sqs = sessionQuestionDao.getQuestionsForSession(sessionId)
        sqs.mapNotNull { sq ->
            val q = questionDao.getQuestionById(sq.questionId)
            if (q != null) Pair(q, sq) else null
        }
    }

    /**
     * Submits an answer for a specific question within a session, updating the progress and spaced-repetition data.
     */
    suspend fun submitAnswer(
        sessionId: String,
        questionId: String,
        selectedOptionIndex: Int
    ): AnswerResult = withContext(Dispatchers.IO) {
        val currentTime = System.currentTimeMillis()
        val question = questionDao.getQuestionById(questionId) ?: throw IllegalArgumentException("Question not found")
        val session = sessionDao.getSessionById(sessionId) ?: throw IllegalArgumentException("Session not found")
        
        val isCorrect = (selectedOptionIndex == question.correctOptionIndex)
        
        // 1. Find session question and update it
        val sessionQuestions = sessionQuestionDao.getQuestionsForSession(sessionId)
        val sq = sessionQuestions.firstOrNull { it.questionId == questionId }
        if (sq != null) {
            sessionQuestionDao.updateSessionQuestion(
                sq.copy(
                    selectedOptionIndex = selectedOptionIndex,
                    answeredCorrectly = isCorrect,
                    answeredAt = currentTime
                )
            )
        }

        // 2. Load or create QuestionProgress and update it
        val existingProgress = progressDao.getProgressForQuestion(questionId) ?: QuestionProgressEntity(
            questionId = questionId,
            topicId = question.topicId
        )

        val timesSeen = existingProgress.timesSeen + 1
        val timesCorrect = existingProgress.timesCorrect + (if (isCorrect) 1 else 0)
        val timesIncorrect = existingProgress.timesIncorrect + (if (isCorrect) 0 else 1)
        
        // Spaced Repetition Formula:
        // Capped at 100 mastery.
        // Correct answer increases mastery and sets nexteligible interval.
        // Incorrect answer decreases mastery and schedules for immediate or near-immediate review.
        val masteryScore: Int
        val nextEligibleAt: Long

        if (isCorrect) {
            masteryScore = (existingProgress.masteryScore + 20).coerceAtMost(100)
            
            // Interval increases as mastery increases
            val daysInterval = when {
                masteryScore >= 100 -> 30
                masteryScore >= 80 -> 14
                masteryScore >= 60 -> 7
                masteryScore >= 40 -> 3
                else -> 1
            }
            nextEligibleAt = currentTime + (daysInterval * 24 * 3600 * 1000L)
        } else {
            masteryScore = (existingProgress.masteryScore - 40).coerceAtLeast(0)
            // Make immediately eligible (0 cooldown) so it can be reviewed again
            nextEligibleAt = 0L
        }

        val updatedProgress = existingProgress.copy(
            timesSeen = timesSeen,
            timesCorrect = timesCorrect,
            timesIncorrect = timesIncorrect,
            lastShownAt = currentTime,
            lastAnsweredAt = currentTime,
            lastAnswerCorrect = isCorrect,
            masteryScore = masteryScore,
            nextEligibleAt = nextEligibleAt,
            lastSessionId = sessionId,
            lastAnswerOptionIndex = selectedOptionIndex
        )
        progressDao.insertProgress(updatedProgress)

        // 3. Update session aggregate scores
        val correctDelta = if (isCorrect) 1 else 0
        val incorrectDelta = if (isCorrect) 0 else 1
        
        sessionDao.insertSession(
            session.copy(
                scoreCorrect = session.scoreCorrect + correctDelta,
                scoreIncorrect = session.scoreIncorrect + incorrectDelta
            )
        )

        // 4. Recalculate topic progress
        recalculateTopicProgress(question.topicId)

        return@withContext AnswerResult(
            isCorrect = isCorrect,
            correctOptionIndex = question.correctOptionIndex,
            explanationEnglish = question.explanationEnglish,
            explanationUrdu = question.explanationUrdu,
            explanationArabic = question.explanationArabic
        )
    }

    /**
     * Finalizes and completes a revision session.
     */
    suspend fun completeRevisionSession(sessionId: String): RevisionSessionEntity = withContext(Dispatchers.IO) {
        val session = sessionDao.getSessionById(sessionId) ?: throw IllegalArgumentException("Session not found")
        val completed = session.copy(
            completedAt = System.currentTimeMillis(),
            status = "completed"
        )
        sessionDao.updateSession(completed)
        return@withContext completed
    }
}
