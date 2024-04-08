package com.jalloft.noteskt.dao.note

import com.jalloft.noteskt.models.Note

interface NoteDao {
    suspend fun allNotes(userId: String): List<Note>
    suspend fun note(noteId: String, userId: String): Note?
    suspend fun save(note: Note): Note?
    suspend fun edit(noteId: String, note: Note): Boolean
    suspend fun delete(noteId: String): Boolean
    suspend fun saveAll(notes: List<Note>): List<Note?>

}