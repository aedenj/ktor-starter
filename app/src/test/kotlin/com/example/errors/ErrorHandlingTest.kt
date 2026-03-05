package com.example.errors

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("error handling")
class ErrorHandlingTest {
    @Test
    @DisplayName("unknown route returns 404 problem+json for API clients")
    fun `unknown route returns 404 problem details`() =
        testApplication {
            application { module() }
            val response =
                client.get("/nonexistent") {
                    header(HttpHeaders.Accept, "application/json")
                }

            assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
            assertThat(response.headers["Content-Type"]).contains("application/problem+json")

            val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            assertThat(body["status"]?.jsonPrimitive?.content).isEqualTo("404")
            assertThat(body["title"]?.jsonPrimitive?.content).isEqualTo("Not Found")
            assertThat(body["instance"]?.jsonPrimitive?.content).isEqualTo("/nonexistent")
        }

    @Test
    @DisplayName("BadRequestException returns 400 problem+json for API clients")
    fun `BadRequestException returns 400 problem details`() =
        testApplication {
            application {
                module()
                routing { get("/bad") { throw BadRequestException("bad input") } }
            }
            val response =
                client.get("/bad") {
                    header(HttpHeaders.Accept, "application/json")
                }

            assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
            assertThat(response.headers["Content-Type"]).contains("application/problem+json")

            val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            assertThat(body["status"]?.jsonPrimitive?.content).isEqualTo("400")
            assertThat(body["title"]?.jsonPrimitive?.content).isEqualTo("Bad Request")
        }

    @Test
    @DisplayName("uncaught exception returns 500 problem+json for API clients")
    fun `uncaught exception returns 500 problem details`() =
        testApplication {
            application {
                module()
                routing { get("/boom") { throw RuntimeException("something went wrong") } }
            }
            val response =
                client.get("/boom") {
                    header(HttpHeaders.Accept, "application/json")
                }

            assertThat(response.status).isEqualTo(HttpStatusCode.InternalServerError)
            assertThat(response.headers["Content-Type"]).contains("application/problem+json")

            val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            assertThat(body["status"]?.jsonPrimitive?.content).isEqualTo("500")
            assertThat(body["title"]?.jsonPrimitive?.content).isEqualTo("Internal Server Error")
            assertThat(body["detail"]?.jsonPrimitive?.content).isEqualTo("An unexpected error occurred")
        }

    @Test
    @DisplayName("unknown route returns animated 404 HTML page for browsers")
    fun `unknown route returns html error page for browsers`() =
        testApplication {
            application { module() }
            val response =
                client.get("/nonexistent") {
                    header(HttpHeaders.Accept, "text/html,application/xhtml+xml,*/*")
                }

            assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
            assertThat(response.headers["Content-Type"]).contains("text/html")

            val body = response.bodyAsText()
            assertThat(body).contains("404")
            assertThat(body).contains("<svg")
            assertThat(body).contains("animate")
            assertThat(body).contains("Lost in the void")
        }

    @Test
    @DisplayName("server error returns animated 500 HTML page with fire for browsers")
    fun `server error returns html fire page for browsers`() =
        testApplication {
            application {
                module()
                routing { get("/boom") { throw RuntimeException("kaboom") } }
            }
            val response =
                client.get("/boom") {
                    header(HttpHeaders.Accept, "text/html,application/xhtml+xml,*/*")
                }

            assertThat(response.status).isEqualTo(HttpStatusCode.InternalServerError)
            val body = response.bodyAsText()
            assertThat(body).contains("500")
            assertThat(body).contains("this is fine")
            assertThat(body).contains("<svg")
        }
}
