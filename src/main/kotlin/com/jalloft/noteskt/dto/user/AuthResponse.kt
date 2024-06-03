package com.jalloft.noteskt.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val userResponse: UserResponse
)
