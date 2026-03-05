package com.example.errors

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
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
    @DisplayName("unknown route returns 404 problem details")
    fun `unknown route returns 404 problem details`() =
        testApplication {
            application {
                module()
            }
            val response = client.get("/nonexistent")

            assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
            assertThat(response.headers["Content-Type"]).contains("application/problem+json")

            val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            assertThat(body["status"]?.jsonPrimitive?.content).isEqualTo("404")
            assertThat(body["title"]?.jsonPrimitive?.content).isEqualTo("Not Found")
            assertThat(body["instance"]?.jsonPrimitive?.content).isEqualTo("/nonexistent")
        }

    @Test
    @DisplayName("BadRequestException returns 400 problem details")
    fun `BadRequestException returns 400 problem details`() =
        testApplication {
            application {
                module()
                routing {
                    get("/bad") { throw BadRequestException("bad input") }
                }
            }
            val response = client.get("/bad")

            assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
            assertThat(response.headers["Content-Type"]).contains("application/problem+json")

            val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            assertThat(body["status"]?.jsonPrimitive?.content).isEqualTo("400")
            assertThat(body["title"]?.jsonPrimitive?.content).isEqualTo("Bad Request")
        }

    @Test
    @DisplayName("uncaught exception returns 500 problem details")
    fun `uncaught exception returns 500 problem details`() =
        testApplication {
            application {
                module()
                routing {
                    get("/boom") { throw RuntimeException("something went wrong") }
                }
            }
            val response = client.get("/boom")

            assertThat(response.status).isEqualTo(HttpStatusCode.InternalServerError)
            assertThat(response.headers["Content-Type"]).contains("application/problem+json")

            val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            assertThat(body["status"]?.jsonPrimitive?.content).isEqualTo("500")
            assertThat(body["title"]?.jsonPrimitive?.content).isEqualTo("Internal Server Error")
            assertThat(body["detail"]?.jsonPrimitive?.content).isEqualTo("An unexpected error occurred")
        }
}
