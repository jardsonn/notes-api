package com.jalloft.noteskt.dao.note

import com.jalloft.noteskt.database.DatabaseFactory
import com.jalloft.noteskt.models.Note
import com.jalloft.noteskt.models.Notes
import com.jalloft.noteskt.utils.toUUID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement

class NoteDaoImpl : NoteDao {

    override suspend fun allNotes(userId: String): List<Note> = DatabaseFactory.dbQuery {
        val uuid = userId.toUUID() ?: return@dbQuery emptyList()
        Notes.selectAll().where { Notes.userId eq uuid }.map(::resultRowToNote)
    }

    override suspend fun note(noteId: String, userId: String): Note? = DatabaseFactory.dbQuery {
        val uuidNote = noteId.toUUID() ?: return@dbQuery null
        val uuidUser = userId.toUUID() ?: return@dbQuery null
        Notes.selectAll().where { Notes.userId eq uuidUser }.andWhere { Notes.id eq uuidNote }
            .map(::resultRowToNote)
            .singleOrNull()
    }

    override suspend fun save(note: Note) = DatabaseFactory.dbQuery {
        val insertStatement = Notes.insert { insertNoteRows(it, note) }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToNote)
    }

    override suspend fun edit(noteId: String, note: Note): Boolean {
        val uuid = noteId.toUUID() ?: return false
        return Notes.update({ Notes.id eq uuid }) {
            it[Notes.title] = note.title
            it[Notes.content] = note.content
        } > 0
    }

    override suspend fun delete(noteId: String): Boolean {
        val uuid = noteId.toUUID() ?: return false
        return Notes.deleteWhere { Notes.id eq uuid } > 0
    }

    override suspend fun saveAll(notes: List<Note>) = DatabaseFactory.dbQuery {
        val insertStatements = notes.map { note -> Notes.insert { insertNoteRows(it, note) } }
        insertStatements.map { it.resultedValues?.singleOrNull()?.let(::resultRowToNote) }
    }


    private fun resultRowToNote(row: ResultRow) =
        Note(
            id = row[Notes.id],
            userId = row[Notes.userId],
            title = row[Notes.title],
            content = row[Notes.content],
            createdAt = row[Notes.createdAt]
        )

    private fun insertNoteRows(insertStatement: InsertStatement<Number>, note: Note) {
        insertStatement[Notes.id] = note.id
        note.userId?.let { insertStatement[Notes.userId] = it }
        insertStatement[Notes.title] = note.title
        insertStatement[Notes.content] = note.content
        insertStatement[Notes.createdAt] = note.createdAt
    }
}


