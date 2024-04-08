package com.jalloft.noteskt.security.hashing

data class SaltedHash(
    val hash: String,
    val salt: String
)