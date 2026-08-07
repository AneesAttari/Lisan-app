package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SavedNote
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedNoteDao {
    @Query("SELECT * FROM saved_notes ORDER BY isPinned DESC, timestamp DESC")
    fun getAllNotes(): Flow<List<SavedNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: SavedNote)

    @Query("UPDATE saved_notes SET isPinned = :isPinned WHERE id = :id")
    suspend fun updatePin(id: Int, isPinned: Boolean)

    @Query("SELECT * FROM saved_notes WHERE id = :id")
    suspend fun getNoteById(id: Int): SavedNote?

    @Query("DELETE FROM saved_notes WHERE id = :id")
    suspend fun deleteNoteById(id: Int)
}
