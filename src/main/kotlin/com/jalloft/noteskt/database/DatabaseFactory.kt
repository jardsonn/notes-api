package com.jalloft.noteskt.database

import com.jalloft.noteskt.models.Notes
import com.jalloft.noteskt.models.Users
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val dbUrl = System.getenv("DATABASE_URL")
        val dbUser = System.getenv("DATABASE_USERNAME")
        val dbPassword = System.getenv("DATABASE_PASSWORD")
        val database = Database.connect(
            url = dbUrl,
            driver = "org.postgresql.Driver",
            user = dbUser,
            password = dbPassword
        )
//        Flyway
//            .configure()
//            .baselineOnMigrate(true)
//            .dataSource(dbUrl, dbUser, dbPassword)
//            .load()
//            .migrate()

        transaction(database) { SchemaUtils.create(Notes, Users) }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }


}