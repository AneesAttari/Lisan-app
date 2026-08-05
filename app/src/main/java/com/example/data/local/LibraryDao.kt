package com.example.data.local

import androidx.room.*
import com.example.data.model.LibraryItem
import com.example.data.model.LikedItem
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {
    @Query("SELECT * FROM library_items ORDER BY savedTimestamp DESC")
    fun getAllItems(): Flow<List<LibraryItem>>

    @Query("SELECT * FROM library_items WHERE contentType = :type ORDER BY savedTimestamp DESC")
    fun getItemsByType(type: String): Flow<List<LibraryItem>>

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
}
