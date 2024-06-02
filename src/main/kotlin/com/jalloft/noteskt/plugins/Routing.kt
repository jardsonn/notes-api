package com.jalloft.noteskt.plugins

import com.jalloft.noteskt.repository.NoteRepository
import com.jalloft.noteskt.repository.UserRepository
import com.jalloft.noteskt.routes.*
import com.jalloft.noteskt.security.hashing.HashingService
import com.jalloft.noteskt.security.token.TokenConfig
import com.jalloft.noteskt.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    noteRepo: NoteRepository,
    userRepo: UserRepository,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        authenticate {
            getNotes(noteRepo)
            getNote(noteRepo)
            postNote(noteRepo)
            postNotes(noteRepo)
            deleteNote(noteRepo)
            putNote(noteRepo)
        }

        signIn(userRepo, hashingService, tokenService, tokenConfig)
        signUp(hashingService, userRepo,  tokenService, tokenConfig)
        authenticate()
        getSecretInfo()
    }
}
