package com.jalloft.noteskt.dto.note

import kotlinx.serialization.Serializable

@Serializable
data class UpdateDeleteNoteResponse(
    val code: Int,
    val message: String
)
