package com.example

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("endpoints")
class ApplicationTest {
    @Test
    @DisplayName("root path returns 'Hello, world!'")
    fun testRoot() = testApplication {
        val response = client.get("/")

        assertThat(HttpStatusCode.OK).isEqualTo(response.status)
        assertThat("Hello, world!").isEqualTo(response.bodyAsText())
    }

    @Test
    @DisplayName("test ignore trailing slash in path")
    fun testIgnoreTrailingSlash() = testApplication {
        val response = client.get("/readiness/")

        assertThat(HttpStatusCode.OK).isEqualTo(response.status)
    }
}
