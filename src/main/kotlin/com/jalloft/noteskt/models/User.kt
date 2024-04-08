package com.jalloft.noteskt.models

import org.jetbrains.exposed.sql.Table
import java.util.*

data class User(
    val id: UUID = UUID.randomUUID(),
    val email: String,
    val password: String,
    val salt: String,
)

object Users: Table(){
    val id = uuid("id").autoGenerate()
    val email = varchar("email", 255)
    val password = varchar("password", 255)
    val salt = varchar("salt", 128)

    override val primaryKey = PrimaryKey(id)
}

