@file:JvmName("ErrorHandling")

package com.example.errors

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
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
            call.respondError(HttpStatusCode.BadRequest, cause.message)
        }
        exception<NotFoundException> { call, cause ->
            call.respondError(HttpStatusCode.NotFound, cause.message)
        }
        exception<Throwable> { call, _ ->
            call.respondError(HttpStatusCode.InternalServerError, "An unexpected error occurred")
        }
        status(HttpStatusCode.NotFound) { call, status ->
            call.respondError(status, "The requested resource was not found")
        }
        status(HttpStatusCode.MethodNotAllowed) { call, status ->
            call.respondError(status, "Method not allowed")
        }
    }
}

private fun ApplicationCall.wantsBrowser(): Boolean = request.headers[HttpHeaders.Accept]?.contains("text/html") == true

private suspend fun ApplicationCall.respondError(
    status: HttpStatusCode,
    detail: String? = null,
) {
    val path = request.path()
    if (wantsBrowser()) {
        respondText(errorHtml(status, detail, path), ContentType.Text.Html, status)
    } else {
        val problem =
            ProblemDetails(
                title = status.description,
                status = status.value,
                detail = detail,
                instance = path,
            )
        respondText(Json.encodeToString(problem), ProblemJson, status)
    }
}
