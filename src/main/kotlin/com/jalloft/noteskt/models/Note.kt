package com.jalloft.noteskt.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.*

data class Note(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val content: String,
    val createdIn: LocalDateTime
)

object Notes : Table() {
    val id = uuid("id").autoGenerate()
    val title = varchar("title", 128)
    val content = varchar("content", 1024)
    val createdIn = datetime("createdIn")

    override val primaryKey = PrimaryKey(id)
}