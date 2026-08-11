package com.example.data.repository

import com.example.data.local.LibraryDao
import com.example.data.local.LikedDao
import com.example.data.local.DislikedDao
import com.example.data.local.FeedbackDao
import com.example.data.model.LibraryItem
import com.example.data.model.LikedItem
import com.example.data.model.DislikedItem
import com.example.data.model.FeedbackEntity
import kotlinx.coroutines.flow.Flow

class LibraryRepository(
    private val libraryDao: LibraryDao,
    private val likedDao: LikedDao,
    private val dislikedDao: DislikedDao,
    private val feedbackDao: FeedbackDao
) {
    val allLibraryItems: Flow<List<LibraryItem>> = libraryDao.getAllItems()
    val allLikedItems: Flow<List<LikedItem>> = likedDao.getAllLikedItems()
    val totalLikesCount: Flow<Int> = likedDao.getTotalLikesCount()
    val totalDislikesCount: Flow<Int> = dislikedDao.getTotalDislikesCount()
    val allFeedback: Flow<List<FeedbackEntity>> = feedbackDao.getAllFeedback()

    fun getLibraryItemsByType(type: String): Flow<List<LibraryItem>> = libraryDao.getItemsByType(type)

    suspend fun saveToLibrary(item: LibraryItem) = libraryDao.insertItem(item)
    suspend fun removeFromLibrary(type: String, contentId: String) = libraryDao.deleteItem(type, contentId)
    fun isSaved(type: String, contentId: String): Flow<Boolean> = libraryDao.isSaved(type, contentId)

    suspend fun likeItem(item: LikedItem) = likedDao.insertLikedItem(item)
    suspend fun unlikeItem(type: String, contentId: String) = likedDao.deleteLikedItem(type, contentId)
    fun isLiked(type: String, contentId: String): Flow<Boolean> = likedDao.isLiked(type, contentId)

    suspend fun dislikeItem(item: DislikedItem) = dislikedDao.insertDislikedItem(item)
    suspend fun undislikeItem(type: String, contentId: String) = dislikedDao.deleteDislikedItem(type, contentId)
    fun isDisliked(type: String, contentId: String): Flow<Boolean> = dislikedDao.isDisliked(type, contentId)

    suspend fun submitFeedback(feedback: FeedbackEntity) = feedbackDao.insertFeedback(feedback)
}

