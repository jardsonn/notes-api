package com.jalloft.noteskt.dto

import com.jalloft.noteskt.models.Note
import com.jalloft.noteskt.serialization.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class NoteResponse(
    val id: String,
    val title: String,
    val content: String,
    @Serializable(with = LocalDateSerializer::class)
    val createdAt: LocalDateTime
)

fun Note.toNoteResponse() = NoteResponse(
    id.toString(),
    title,
    content,
    createdAt
)
