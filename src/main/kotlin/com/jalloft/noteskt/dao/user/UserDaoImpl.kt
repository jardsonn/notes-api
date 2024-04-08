package com.jalloft.noteskt.dao.user

import com.jalloft.noteskt.database.DatabaseFactory
import com.jalloft.noteskt.models.User
import com.jalloft.noteskt.models.Users
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class UserDaoImpl : UserDao {
    override suspend fun findUserByEmail(email: String): User? = DatabaseFactory.dbQuery {
        Users.selectAll()
            .where { Users.email eq email }
            .map { User(it[Users.id], it[Users.email], it[Users.password], it[Users.salt]) }
            .singleOrNull()
    }

    override suspend fun saveUser(user: User) = DatabaseFactory.dbQuery {
        val insertStatement = Users.insert {
            it[id] = user.id
            it[email] = user.email
            it[password] = user.password
            it[salt] = user.salt
        }
        insertStatement.resultedValues.orEmpty().isNotEmpty()
    }
}