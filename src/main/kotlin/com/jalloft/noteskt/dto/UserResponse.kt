package com.jalloft.noteskt.dto

import com.jalloft.noteskt.models.User
import com.jalloft.noteskt.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UserResponse(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val name: String,
    val email: String,
)

fun User.toUserResponse(): UserResponse {
    return UserResponse(id, name, email)
}