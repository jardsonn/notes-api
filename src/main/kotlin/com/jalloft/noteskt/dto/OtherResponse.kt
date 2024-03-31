package com.jalloft.noteskt.dto

import kotlinx.serialization.Serializable

@Serializable
data class OtherResponse(
    val code: Int,
    val message: String
)
