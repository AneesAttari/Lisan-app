package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RevisionQuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<RevisionQuestionEntity>)

    @Query("SELECT * FROM revision_questions WHERE topicId = :topicId AND isActive = 1")
    suspend fun getQuestionsForTopic(topicId: String): List<RevisionQuestionEntity>

    @Query("SELECT * FROM revision_questions WHERE id = :id LIMIT 1")
    suspend fun getQuestionById(id: String): RevisionQuestionEntity?

    @Query("SELECT * FROM revision_questions WHERE isActive = 1")
    suspend fun getAllQuestions(): List<RevisionQuestionEntity>

    @Query("SELECT COUNT(*) FROM revision_questions WHERE topicId = :topicId AND isActive = 1")
    suspend fun getQuestionCountForTopic(topicId: String): Int
}

@Dao
interface QuestionProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: QuestionProgressEntity)

    @Query("SELECT * FROM question_progress WHERE questionId = :questionId LIMIT 1")
    suspend fun getProgressForQuestion(questionId: String): QuestionProgressEntity?

    @Query("SELECT * FROM question_progress WHERE topicId = :topicId")
    suspend fun getAllProgressForTopic(topicId: String): List<QuestionProgressEntity>

    @Query("SELECT * FROM question_progress")
    suspend fun getAllProgress(): List<QuestionProgressEntity>
}

@Dao
interface RevisionSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: RevisionSessionEntity)

    @Update
    suspend fun updateSession(session: RevisionSessionEntity)

    @Query("SELECT * FROM revision_sessions WHERE sessionId = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: String): RevisionSessionEntity?

    @Query("SELECT * FROM revision_sessions ORDER BY startedAt DESC")
    suspend fun getAllSessions(): List<RevisionSessionEntity>
}

@Dao
interface RevisionSessionQuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessionQuestion(sessionQuestion: RevisionSessionQuestionEntity)

    @Update
    suspend fun updateSessionQuestion(sessionQuestion: RevisionSessionQuestionEntity)

    @Query("SELECT * FROM revision_session_questions WHERE sessionId = :sessionId ORDER BY displayOrder ASC")
    suspend fun getQuestionsForSession(sessionId: String): List<RevisionSessionQuestionEntity>
}

@Dao
interface TopicProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopicProgress(topicProgress: TopicProgressEntity)

    @Query("SELECT * FROM topic_progress WHERE topicId = :topicId LIMIT 1")
    suspend fun getTopicProgress(topicId: String): TopicProgressEntity?

    @Query("SELECT * FROM topic_progress")
    suspend fun getAllTopicProgress(): List<TopicProgressEntity>

    @Query("SELECT * FROM topic_progress")
    fun getAllTopicProgressFlow(): Flow<List<TopicProgressEntity>>
}
