package com.jalloft.noteskt.repository

import com.jalloft.noteskt.dao.NoteDao
import com.jalloft.noteskt.dao.NoteDaoImpl
import com.jalloft.noteskt.models.Note

class NoteRepository(
    private val dao: NoteDao = NoteDaoImpl()
) {

    suspend fun notes() = dao.allNotes()

    suspend fun note(id: String) = dao.note(id)
    suspend fun save(note: Note) = dao.save(note)

    suspend fun edit(id: String, note: Note) = dao.edit(id, note)

    suspend fun saveAll(notes: List<Note>) = dao.saveAll(notes)

    suspend fun delete(id: String) = dao.delete(id)
}
