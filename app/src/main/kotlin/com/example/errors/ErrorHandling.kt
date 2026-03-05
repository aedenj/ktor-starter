@file:JvmName("ErrorHandling")

package com.example.errors

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.path
import io.ktor.server.response.respondText
import kotlinx.serialization.json.Json

val ProblemJson = ContentType("application", "problem+json")

fun Application.module() {
    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            call.respondProblem(HttpStatusCode.BadRequest, cause.message)
        }
        exception<NotFoundException> { call, cause ->
            call.respondProblem(HttpStatusCode.NotFound, cause.message)
        }
        exception<Throwable> { call, _ ->
            call.respondProblem(HttpStatusCode.InternalServerError, "An unexpected error occurred")
        }
        status(HttpStatusCode.NotFound) { call, status ->
            call.respondProblem(status, "The requested resource was not found")
        }
        status(HttpStatusCode.MethodNotAllowed) { call, status ->
            call.respondProblem(status, "Method not allowed")
        }
    }
}

private suspend fun ApplicationCall.respondProblem(
    status: HttpStatusCode,
    detail: String? = null,
) {
    val problem =
        ProblemDetails(
            title = status.description,
            status = status.value,
            detail = detail,
            instance = request.path(),
        )
    respondText(Json.encodeToString(problem), ProblemJson, status)
}
