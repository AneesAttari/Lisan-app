package com.example.data.repository

import com.example.data.local.LibraryDao
import com.example.data.local.LikedDao
import com.example.data.model.LibraryItem
import com.example.data.model.LikedItem
import kotlinx.coroutines.flow.Flow

class LibraryRepository(
    private val libraryDao: LibraryDao,
    private val likedDao: LikedDao
) {
    val allLibraryItems: Flow<List<LibraryItem>> = libraryDao.getAllItems()
    val allLikedItems: Flow<List<LikedItem>> = likedDao.getAllLikedItems()

    fun getLibraryItemsByType(type: String): Flow<List<LibraryItem>> = libraryDao.getItemsByType(type)

    suspend fun saveToLibrary(item: LibraryItem) = libraryDao.insertItem(item)
    suspend fun removeFromLibrary(type: String, contentId: String) = libraryDao.deleteItem(type, contentId)
    fun isSaved(type: String, contentId: String): Flow<Boolean> = libraryDao.isSaved(type, contentId)

    suspend fun likeItem(item: LikedItem) = likedDao.insertLikedItem(item)
    suspend fun unlikeItem(type: String, contentId: String) = likedDao.deleteLikedItem(type, contentId)
    fun isLiked(type: String, contentId: String): Flow<Boolean> = likedDao.isLiked(type, contentId)
}
