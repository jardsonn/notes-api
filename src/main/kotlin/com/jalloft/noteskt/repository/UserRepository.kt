package com.jalloft.noteskt.repository

import com.jalloft.noteskt.dao.user.UserDao
import com.jalloft.noteskt.dao.user.UserDaoImpl
import com.jalloft.noteskt.models.User

class UserRepository(
    private val dao: UserDao = UserDaoImpl()
) {
    suspend fun findUserByEmail(email: String) = dao.findUserByEmail(email)
    suspend fun saveUser(user: User) = dao.saveUser(user)
    suspend fun isEmailAlreadyRegistered(email: String) = dao.isEmailAlreadyRegistered(email)
}