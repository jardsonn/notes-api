package com.jalloft.noteskt.dao

import com.jalloft.noteskt.models.Note

interface NoteDao {
    suspend fun allNotes(): List<Note>
    suspend fun note(id: String): Note?
    suspend fun save(note: Note): Note?
    suspend fun edit(id: String, note: Note): Boolean
    suspend fun delete(id: String): Boolean
    suspend fun saveAll(notes: List<Note>): List<Note?>

}