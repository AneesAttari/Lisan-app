package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.TopicProgressEntity
import kotlinx.coroutines.flow.Flow

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

    @Query("SELECT topicId FROM topic_progress WHERE isCompleted = 1")
    fun getCompletedTopicIdsFlow(): Flow<List<String>>

    @Query("SELECT topicId FROM topic_progress WHERE isCompleted = 1")
    suspend fun getCompletedTopicIds(): List<String>
}
