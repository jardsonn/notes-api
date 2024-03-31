package com.jalloft.noteskt.dao

import com.jalloft.noteskt.database.DatabaseFactory.dbQuery
import com.jalloft.noteskt.models.Note
import com.jalloft.noteskt.models.Notes
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class NoteDaoImpl : NoteDao {

    override suspend fun allNotes(): List<Note> = dbQuery {
        Notes.selectAll().map(::resultRowToNote)
    }

    override suspend fun note(id: String): Note? = dbQuery {
        val uuid = id.toUUID()?: return@dbQuery null
        Notes.selectAll().where { Notes.id eq uuid }
            .map(::resultRowToNote)
            .singleOrNull()
    }

    override suspend fun save(note: Note) = dbQuery {
        val insertStatement = Notes.insert {
            it[id] = note.id
            it[title] = note.title
            it[content] = note.content
            it[createdIn] = note.createdIn
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToNote)
    }

    override suspend fun edit(id: String, note: Note): Boolean {
        val uuid = id.toUUID()?: return false
        return Notes.update({ Notes.id eq uuid }) {
            it[title] = note.title
            it[content] = note.content
        } > 0
    }

    override suspend fun delete(id: String): Boolean {
        val uuid = id.toUUID()?: return false
        return Notes.deleteWhere { Notes.id eq uuid } > 0
    }

    override suspend fun saveAll(notes: List<Note>) = dbQuery {
        val insertStatements = notes.map { note ->
            Notes.insert {
                it[id] = note.id
                it[title] = note.title
                it[content] = note.content
                it[createdIn] = note.createdIn
            }
        }
        insertStatements.map { it.resultedValues?.singleOrNull()?.let(::resultRowToNote) }
    }


    private fun resultRowToNote(row: ResultRow) =
        Note(id = row[Notes.id], title = row[Notes.title], content = row[Notes.content], createdIn = row[Notes.createdIn])
}

private fun String.toUUID(): UUID? =
    try {
        UUID.fromString(this)
    } catch (_: Throwable) {
        null
    }

