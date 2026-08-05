package com.example.data.local

import androidx.room.*
import com.example.data.model.SavedIdiom
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedIdiomDao {
    @Query("SELECT * FROM saved_idioms ORDER BY savedAt DESC")
    fun getAllSavedIdioms(): Flow<List<SavedIdiom>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveIdiom(idiom: SavedIdiom)

    @Delete
    suspend fun deleteIdiom(idiom: SavedIdiom)

    @Query("SELECT EXISTS(SELECT * FROM saved_idioms WHERE idiomId = :id)")
    fun isIdiomSaved(id: String): Flow<Boolean>
}
