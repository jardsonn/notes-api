package com.jalloft.noteskt.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.*

data class Note(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID? = null,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

object Notes : Table() {
    val id = uuid("id").autoGenerate()
    val userId = uuid("userId").references(Users.id)
    val title = varchar("title", 128)
    val content = varchar("content", 1024)
    val createdAt = datetime("createdIn").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)
}