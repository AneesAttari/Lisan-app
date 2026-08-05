package com.example.data.local

import com.example.data.model.SavedIdiom
import kotlinx.coroutines.flow.Flow

class SavedIdiomRepository(private val savedIdiomDao: SavedIdiomDao) {
    val allSavedIdioms: Flow<List<SavedIdiom>> = savedIdiomDao.getAllSavedIdioms()

    suspend fun saveIdiom(idiomId: String) {
        savedIdiomDao.saveIdiom(SavedIdiom(idiomId))
    }

    suspend fun deleteIdiom(idiomId: String) {
        savedIdiomDao.deleteIdiom(SavedIdiom(idiomId))
    }

    fun isIdiomSaved(idiomId: String): Flow<Boolean> = savedIdiomDao.isIdiomSaved(idiomId)
}
