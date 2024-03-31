package com.jalloft.noteskt.database

import com.jalloft.noteskt.models.Notes
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    private val dotenv = dotenv{
        this.ignoreIfMissing = true
    }

    fun init(){
        val database = Database.connect(
            url = dotenv["DATABASE_URL"],
            driver = "org.postgresql.Driver",
            user = dotenv["DATABASE_USERNAME"],
            password = dotenv["DATABASE_PASSWORD"]
        )

        transaction(database){
            SchemaUtils.create(Notes)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }


}