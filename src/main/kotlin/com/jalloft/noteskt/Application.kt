package com.jalloft.noteskt

import com.jalloft.noteskt.database.DatabaseFactory
import com.jalloft.noteskt.plugins.configureRouting
import com.jalloft.noteskt.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()
    configureSerialization()
    configureRouting()
}


