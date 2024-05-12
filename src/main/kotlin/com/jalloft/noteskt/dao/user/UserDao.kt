package com.jalloft.noteskt.dao.user

import com.jalloft.noteskt.models.User

interface UserDao {
    suspend fun findUserByEmail(email: String): User?
    suspend fun saveUser(user: User): Boolean
    suspend fun isEmailAlreadyRegistered(email: String): Boolean
}