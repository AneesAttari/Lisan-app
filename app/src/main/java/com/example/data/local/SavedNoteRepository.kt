package com.example.data.local

import com.example.data.model.SavedNote
import kotlinx.coroutines.flow.Flow

class SavedNoteRepository(private val savedNoteDao: SavedNoteDao) {
    val allNotes: Flow<List<SavedNote>> = savedNoteDao.getAllNotes()

    suspend fun insert(note: SavedNote) {
        savedNoteDao.insertNote(note)
    }

    suspend fun deleteById(id: Int) {
        savedNoteDao.deleteNoteById(id)
    }
}
