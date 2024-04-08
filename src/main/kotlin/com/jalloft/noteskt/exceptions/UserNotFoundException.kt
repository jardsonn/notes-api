package com.jalloft.noteskt.exceptions

data class UserNotFoundException(
    override val message: String? = null,
    override val cause: Throwable? = null
): Throwable(message, cause)