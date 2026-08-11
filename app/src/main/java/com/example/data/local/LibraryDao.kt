package com.example.data.local

import androidx.room.*
import com.example.data.model.LibraryItem
import com.example.data.model.LikedItem
import com.example.data.model.DislikedItem
import com.example.data.model.FeedbackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {
    @Query("SELECT * FROM library_items ORDER BY savedTimestamp DESC")
    fun getAllItems(): Flow<List<LibraryItem>>

    @Query("SELECT * FROM library_items WHERE contentType = :type ORDER BY savedTimestamp DESC")
    fun getItemsByType(type: String): Flow<List<LibraryItem>>

    @Query("SELECT * FROM library_items WHERE contentType = :type AND userId = :userId ORDER BY savedTimestamp DESC")
    suspend fun getItemsByTypeSync(type: String, userId: String): List<LibraryItem>

    @Query("SELECT * FROM library_items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: String): LibraryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: LibraryItem)

    @Delete
    suspend fun deleteItem(item: LibraryItem)

    @Query("DELETE FROM library_items WHERE contentType = :type AND contentId = :contentId")
    suspend fun deleteItem(type: String, contentId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM library_items WHERE contentType = :type AND contentId = :contentId)")
    fun isSaved(type: String, contentId: String): Flow<Boolean>
}

@Dao
interface LikedDao {
    @Query("SELECT * FROM liked_items ORDER BY likedTimestamp DESC")
    fun getAllLikedItems(): Flow<List<LikedItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLikedItem(item: LikedItem)

    @Delete
    suspend fun deleteLikedItem(item: LikedItem)

    @Query("DELETE FROM liked_items WHERE contentType = :type AND contentId = :contentId")
    suspend fun deleteLikedItem(type: String, contentId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM liked_items WHERE contentType = :type AND contentId = :contentId)")
    fun isLiked(type: String, contentId: String): Flow<Boolean>

    @Query("SELECT COUNT(*) FROM liked_items")
    fun getTotalLikesCount(): Flow<Int>
}

@Dao
interface DislikedDao {
    @Query("SELECT * FROM disliked_items ORDER BY dislikedTimestamp DESC")
    fun getAllDislikedItems(): Flow<List<DislikedItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDislikedItem(item: DislikedItem)

    @Delete
    suspend fun deleteDislikedItem(item: DislikedItem)

    @Query("DELETE FROM disliked_items WHERE contentType = :type AND contentId = :contentId")
    suspend fun deleteDislikedItem(type: String, contentId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM disliked_items WHERE contentType = :type AND contentId = :contentId)")
    fun isDisliked(type: String, contentId: String): Flow<Boolean>

    @Query("SELECT COUNT(*) FROM disliked_items")
    fun getTotalDislikesCount(): Flow<Int>
}

@Dao
interface FeedbackDao {
    @Query("SELECT * FROM help_feedback_items ORDER BY timestamp DESC")
    fun getAllFeedback(): Flow<List<FeedbackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: FeedbackEntity)

    @Delete
    suspend fun deleteFeedback(feedback: FeedbackEntity)
}

