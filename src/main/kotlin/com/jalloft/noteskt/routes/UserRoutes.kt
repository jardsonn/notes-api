package com.jalloft.noteskt.routes

import com.jalloft.noteskt.dto.user.AuthResponse
import com.jalloft.noteskt.dto.user.SignInRequest
import com.jalloft.noteskt.dto.user.SignUpRequest
import com.jalloft.noteskt.dto.error.AuthenticationErrorCodes
import com.jalloft.noteskt.dto.error.ErrorResponse
import com.jalloft.noteskt.dto.user.toUserResponse
import com.jalloft.noteskt.models.User
import com.jalloft.noteskt.repository.UserRepository
import com.jalloft.noteskt.security.hashing.HashingService
import com.jalloft.noteskt.security.hashing.SaltedHash
import com.jalloft.noteskt.security.token.TokenClaim
import com.jalloft.noteskt.security.token.TokenConfig
import com.jalloft.noteskt.security.token.TokenService
import com.jalloft.noteskt.utils.FieldValidator
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.signUp(
    hashingService: HashingService,
    repo: UserRepository,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("signup") {
        val request = call.receive<SignUpRequest>()


        if (request.email.isBlank()) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(AuthenticationErrorCodes.EMAIL_EMPTY, "O campo e-mail não pode ser vazio.")
            )
            return@post
        }

        if (request.password.isBlank()) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(AuthenticationErrorCodes.PASSWORD_EMPTY, "O campo de senha não pode ser vazio.")
            )
            return@post
        }

        if (!FieldValidator.isValidEmail(request.email)) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    AuthenticationErrorCodes.INVALID_EMAIL,
                    "O e-mail fornecido não é válido. Por favor, insira um e-mail válido."
                )
            )
            return@post
        }

        if (!FieldValidator.isValidPassword(request.password)) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    AuthenticationErrorCodes.INVALID_PASSWORD,
                    "A senha é muito curta. Por favor, insira uma senha com pelo menos 8 caracteres."
                )
            )
            return@post
        }

        val isEmailAlreadyRegistered = repo.isEmailAlreadyRegistered(request.email)

        if (isEmailAlreadyRegistered) {
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse(AuthenticationErrorCodes.EMAIL_ALREADY_REGISTERED, "Esse e-mail já está cadastrado. Faça login.")
            )
            return@post
        }


        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            name = request.name,
            email = request.email,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )
        val wasAcknowledged = repo.saveUser(user)
        if (!wasAcknowledged) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    AuthenticationErrorCodes.GENERIC_ERROR,
                    "Ocorreu um erro. Por favor, tente novamente mais tarde."
                )
            )
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )

        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                token = token,
                userResponse = user.toUserResponse()
            )
        )
    }
}

fun Route.signIn(
    repo: UserRepository,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("signin") {
        val request = call.receive<SignInRequest>()

        val user = repo.findUserByEmail(request.email)
        if (user == null) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(AuthenticationErrorCodes.CREDENTIALS_INCORRECT, "Seu e-mail ou senha está incorreto.")
            )
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )
        if (!isValidPassword) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(AuthenticationErrorCodes.CREDENTIALS_INCORRECT, "Seu e-mail ou senha está incorreto.")
            )
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )

        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                token = token,
                userResponse = user.toUserResponse()
            )
        )
    }
}

fun Route.authenticate() {
    authenticate {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "Your userId is $userId")
        }
    }
}