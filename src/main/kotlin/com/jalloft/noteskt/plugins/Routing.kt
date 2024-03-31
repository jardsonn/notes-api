package com.jalloft.noteskt.plugins

import com.jalloft.noteskt.dto.NoteRequest
import com.jalloft.noteskt.dto.OtherResponse
import com.jalloft.noteskt.dto.toNoteResponse
import com.jalloft.noteskt.repository.NoteRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

fun Application.configureRouting() {
    val repository = NoteRepository()

    routing {

        get("/notes") {
            val notes = repository.notes().map { it.toNoteResponse() }
            call.respond(HttpStatusCode.OK, notes)
        }

        post("/note") {
            val noteRequest = call.receive<NoteRequest>()
            val note = repository.save(noteRequest.toNote()) ?: return@post call.respond(HttpStatusCode.BadRequest)
            call.respond(HttpStatusCode.Created, note.toNoteResponse())
        }

        post("/notes") {
            val notesRequest = call.receive<List<NoteRequest>>()
            val savedNotes = repository.saveAll(notesRequest.map { it.toNote() })
            call.respond(HttpStatusCode.Created, savedNotes.map { it?.toNoteResponse() })
        }

        get("/note/{id?}") {
            val noteId = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                OtherResponse(HttpStatusCode.BadRequest.value, "ID ausente")
            )
            val note = repository.note(noteId) ?: return@get call.respond(
                HttpStatusCode.NotFound,
                OtherResponse(HttpStatusCode.NotFound.value, "Nenhuma anotação encontrada com este ID")
            )
            call.respond(HttpStatusCode.OK, note.toNoteResponse())
        }

        delete("/note/{id?}") {
            val noteId = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            newSuspendedTransaction {
                val isDeleted = repository.delete(noteId)
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

        put("/note/{id?}") {
            val noteId = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val noteRequest = call.receive<NoteRequest>()
            val isUpdated = repository.edit(noteId, noteRequest.toNote())
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
}
