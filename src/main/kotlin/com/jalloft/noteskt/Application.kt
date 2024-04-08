package com.jalloft.noteskt

import com.jalloft.noteskt.database.DatabaseFactory
import com.jalloft.noteskt.plugins.configureRouting
import com.jalloft.noteskt.plugins.configureSecurity
import com.jalloft.noteskt.plugins.configureSerialization
import com.jalloft.noteskt.plugins.configureStatusPage
import com.jalloft.noteskt.repository.NoteRepository
import com.jalloft.noteskt.repository.UserRepository
import com.jalloft.noteskt.security.hashing.SHA256HashingService
import com.jalloft.noteskt.security.token.JwtTokenService
import com.jalloft.noteskt.security.token.TokenConfig
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )
    val hashingService = SHA256HashingService()
    val noteRepo = NoteRepository()
    val userRepo = UserRepository()

    DatabaseFactory.init()
    configureSerialization()
    configureStatusPage()
    configureSecurity(tokenConfig)
    configureRouting(noteRepo, userRepo, hashingService, tokenService, tokenConfig)
}


