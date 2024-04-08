package com.jalloft.noteskt.dto

import com.jalloft.noteskt.models.Note
import com.jalloft.noteskt.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class NoteRequest(
    val title: String,
    val content: String,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID? = null,
) {
    fun toNote() = Note(title = title, content = content, userId = userId)
}
