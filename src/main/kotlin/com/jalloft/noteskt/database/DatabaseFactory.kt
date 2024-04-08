package com.jalloft.noteskt.database

import com.jalloft.noteskt.models.Notes
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(){
        val database = Database.connect(
            url = System.getenv("DATABASE_URL"),
            driver = "org.postgresql.Driver",
            user = System.getenv("DATABASE_USERNAME"),
            password = System.getenv("DATABASE_PASSWORD")
        )

        transaction(database){
            SchemaUtils.create(Notes)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }


}