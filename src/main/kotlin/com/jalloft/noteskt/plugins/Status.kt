package com.jalloft.noteskt.plugins

import com.jalloft.noteskt.exceptions.AuthenticationException
import com.jalloft.noteskt.exceptions.UserNotFoundException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPage(){
    install(StatusPages) {
        exception<AuthenticationException> { call, cause ->
            call.respond(status = HttpStatusCode.Unauthorized, message = cause.message ?: "Authentication failed!")
        }

         exception<UserNotFoundException> { call, cause ->
            call.respond(status = HttpStatusCode.Unauthorized, message = cause.message ?: "Authentication failed!")
        }

        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(message = "404: Page Not Found", status = status)
        }
    }
}