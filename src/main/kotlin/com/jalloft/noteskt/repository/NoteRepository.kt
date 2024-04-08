package com.jalloft.noteskt.repository

import com.jalloft.noteskt.dao.note.NoteDao
import com.jalloft.noteskt.dao.note.NoteDaoImpl
import com.jalloft.noteskt.models.Note

class NoteRepository(
    private val dao: NoteDao = NoteDaoImpl()
) {

    suspend fun notes(userId: String) = dao.allNotes(userId)

    suspend fun note(noteId: String, userId: String) = dao.note(noteId, userId)
    suspend fun save(note: Note) = dao.save(note)

    suspend fun edit(noteId: String, note: Note) = dao.edit(noteId, note)

    suspend fun saveAll(notes: List<Note>) = dao.saveAll(notes)

    suspend fun delete(noteId: String) = dao.delete(noteId)
}
