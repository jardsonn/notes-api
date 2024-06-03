package com.jalloft.noteskt.routes

import com.jalloft.noteskt.dto.error.AuthenticationErrorCodes
import com.jalloft.noteskt.dto.error.ErrorResponse
import com.jalloft.noteskt.dto.error.NoteResponseCodes
import com.jalloft.noteskt.dto.note.NoteRequest
import com.jalloft.noteskt.dto.note.UpdateDeleteNoteResponse
import com.jalloft.noteskt.dto.note.toNoteResponse
import com.jalloft.noteskt.repository.NoteRepository
import com.jalloft.noteskt.utils.toUUID
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

fun Route.getNotes(noteRepo: NoteRepository) {
    get("/notes") {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.payload?.getClaim("userId")?.asString()
        if (userId != null) {
            val notes = noteRepo.notes(userId).map { it.toNoteResponse() }
            call.respond(HttpStatusCode.OK, notes)
        } else {
            call.respond(
                HttpStatusCode.Unauthorized,
                ErrorResponse(
                    AuthenticationErrorCodes.AUTHENTICATION_EXPIRED,
                    "Usuario não encontrado ou token de autenticação expirou"
                )
            )
        }
    }
}

fun Route.getNote(noteRepo: NoteRepository) {
    get("/note/{id?}") {
        val noteId = call.parameters["id"] ?: return@get call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                NoteResponseCodes.MALFORMED_URL,
                "URL mal formatada. id está ausente"
            )
        )
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.payload?.getClaim("userId")?.asString()
        if (userId != null) {
            val note = noteRepo.note(noteId, userId) ?: return@get call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    NoteResponseCodes.NOTE_NOT_FOUND,
                    "Nenhuma anotação encontrada com este ID"
                )
            )
            call.respond(HttpStatusCode.OK, note.toNoteResponse())
        } else {
            call.respond(
                HttpStatusCode.Unauthorized,
                ErrorResponse(
                    AuthenticationErrorCodes.AUTHENTICATION_EXPIRED,
                    "Usuario não encontrado ou token de autenticação expirou"
                )
            )
        }
    }
}

fun Route.deleteNote(noteRepo: NoteRepository) {
    delete("/note/{id?}") {
        val noteId = call.parameters["id"] ?: return@delete call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                NoteResponseCodes.MALFORMED_URL,
                "URL mal formatada. id está ausente"
            )
        )
        newSuspendedTransaction {
            val isDeleted = noteRepo.delete(noteId)
            if (isDeleted) {
                call.respond(
                    HttpStatusCode.Accepted,
                    UpdateDeleteNoteResponse(
                        NoteResponseCodes.NOTE_DELETED_SUCCESSFULLY,
                        "Anotação excluída com sucesso"
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse(
                        NoteResponseCodes.NOTE_NOT_FOUND,
                        "Nenhuma anotação encontrada com este ID"
                    )
                )
            }
        }
    }
}

fun Route.postNote(noteRepo: NoteRepository) {
    post("/note") {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.payload?.getClaim("userId")?.asString()?.toUUID()
        if (userId != null) {
            val noteRequest = call.receive<NoteRequest>()
            val note = noteRepo.save(noteRequest.copy(userId = userId).toNote()) ?: return@post call.respond(
                HttpStatusCode.BadRequest
            )
            call.respond(HttpStatusCode.Created, note.toNoteResponse())
        } else {
            call.respond(
                HttpStatusCode.Unauthorized,
                ErrorResponse(
                    AuthenticationErrorCodes.AUTHENTICATION_EXPIRED,
                    "Usuario não encontrado ou token de autenticação expirou"
                )
            )
        }
    }
}

fun Route.postNotes(noteRepo: NoteRepository) {
    post("/notes") {
        val notesRequest = call.receive<List<NoteRequest>>()
        val savedNotes = noteRepo.saveAll(notesRequest.map { it.toNote() })
        call.respond(HttpStatusCode.Created, savedNotes.map { it?.toNoteResponse() })
    }
}

fun Route.putNote(noteRepo: NoteRepository) {
    put("/note/{id?}") {
        val noteId = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
        val noteRequest = call.receive<NoteRequest>()
        val isUpdated = noteRepo.edit(noteId, noteRequest.toNote())
        if (isUpdated) {
            call.respond(
                HttpStatusCode.Accepted,
                UpdateDeleteNoteResponse(
                    NoteResponseCodes.NOTE_UPDATED_SUCCESSFULLY,
                    "Anotação atualizada com sucesso"
                )
            )
        } else {
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    NoteResponseCodes.NOTE_NOT_FOUND,
                    "Nenhuma anotação encontrada com este ID"
                )
            )
        }
    }
}




