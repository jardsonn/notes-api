package com.jalloft.noteskt.routes

import com.jalloft.noteskt.dto.NoteRequest
import com.jalloft.noteskt.dto.OtherResponse
import com.jalloft.noteskt.dto.toNoteResponse
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

fun Route.getNotes(noteRepo: NoteRepository){
    get("/notes") {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.payload?.getClaim("userId")?.asString()
        if (userId != null){
            val notes = noteRepo.notes(userId).map { it.toNoteResponse() }
            call.respond(HttpStatusCode.OK, notes)
        }else{
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

fun Route.getNote(noteRepo: NoteRepository){
    get("/note/{id?}") {
        val noteId = call.parameters["id"] ?: return@get call.respond(
            HttpStatusCode.BadRequest,
            OtherResponse(HttpStatusCode.BadRequest.value, "ID ausente")
        )
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.payload?.getClaim("userId")?.asString()
        if (userId != null) {
            val note = noteRepo.note(noteId, userId) ?: return@get call.respond(
                HttpStatusCode.NotFound,
                OtherResponse(HttpStatusCode.NotFound.value, "Nenhuma anotação encontrada com este ID")
            )
            call.respond(HttpStatusCode.OK, note.toNoteResponse())
        } else {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

fun Route.deleteNote(noteRepo: NoteRepository){
    delete("/note/{id?}") {
        val noteId = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
        newSuspendedTransaction {
            val isDeleted = noteRepo.delete(noteId)
            if (isDeleted) {
                call.respond(
                    HttpStatusCode.Accepted,
                    OtherResponse(HttpStatusCode.Accepted.value, "Anotação excluída com sucesso")
                )

            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    OtherResponse(HttpStatusCode.NotFound.value, "Nenhuma anotação encontrada com este ID")
                )
            }
        }
    }
}

fun Route.postNote(noteRepo: NoteRepository){
    post("/note") {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.payload?.getClaim("userId")?.asString()?.toUUID()
        if (userId != null) {
            val noteRequest = call.receive<NoteRequest>()
            val note = noteRepo.save(noteRequest.copy(userId = userId).toNote()) ?: return@post call.respond(HttpStatusCode.BadRequest)
            call.respond(HttpStatusCode.Created, note.toNoteResponse())
        }else{
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}

fun Route.postNotes(noteRepo: NoteRepository){
    post("/notes") {
        val notesRequest = call.receive<List<NoteRequest>>()
        val savedNotes = noteRepo.saveAll(notesRequest.map { it.toNote() })
        call.respond(HttpStatusCode.Created, savedNotes.map { it?.toNoteResponse() })
    }
}

fun Route.putNote(noteRepo: NoteRepository){
    put("/note/{id?}") {
        val noteId = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
        val noteRequest = call.receive<NoteRequest>()
        val isUpdated = noteRepo.edit(noteId, noteRequest.toNote())
        if (isUpdated) {
            call.respond(
                HttpStatusCode.Accepted,
                OtherResponse(HttpStatusCode.Accepted.value, "Anotação atualizada com sucesso")
            )
        } else {
            call.respond(
                HttpStatusCode.NotFound,
                OtherResponse(HttpStatusCode.NotFound.value, "Nenhuma anotação encontrada com este ID")
            )
        }
    }
}




