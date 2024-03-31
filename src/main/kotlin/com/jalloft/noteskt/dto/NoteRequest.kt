package com.jalloft.noteskt.dto

import com.jalloft.noteskt.models.Note
import com.jalloft.noteskt.serialization.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class NoteRequest(
    val title: String,
    val content: String,
    @Serializable(with = LocalDateSerializer::class)
    val createdIn: LocalDateTime = LocalDateTime.now()
) {
    fun toNote() = Note(title = title, content = content, createdIn = createdIn)
}
