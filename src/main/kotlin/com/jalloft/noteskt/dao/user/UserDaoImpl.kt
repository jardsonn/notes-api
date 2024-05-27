package com.jalloft.noteskt.dao.user

import com.jalloft.noteskt.database.DatabaseFactory
import com.jalloft.noteskt.models.User
import com.jalloft.noteskt.models.Users
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class UserDaoImpl : UserDao {
    override suspend fun findUserByEmail(email: String): User? = DatabaseFactory.dbQuery {
        Users.selectAll()
            .where { Users.email eq email }
            .map { User(it[Users.id], it[Users.name],  it[Users.email], it[Users.password], it[Users.salt]) }
            .singleOrNull()
    }

    override suspend fun saveUser(user: User) = DatabaseFactory.dbQuery {
        val insertStatement = Users.insert {
            it[id] = user.id
            it[name] = user.name
            it[email] = user.email
            it[password] = user.password
            it[salt] = user.salt
        }
        insertStatement.resultedValues.orEmpty().isNotEmpty()
    }

    override suspend fun isEmailAlreadyRegistered(email: String): Boolean = newSuspendedTransaction {
        Users.selectAll().where { Users.email eq email }
            .count() > 0
    }
}