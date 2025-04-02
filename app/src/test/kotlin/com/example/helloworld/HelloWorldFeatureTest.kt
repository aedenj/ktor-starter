package com.example.helloworld

import com.example.module
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers

@DisplayName("hello world feature")
@Testcontainers
class HelloWorldFeatureTest {
    @Test
    @DisplayName("root path returns hello world")
    fun `service is ready`() =
        testApplication {
            application {
                module()
            }
            val response = client.get("/")

            assertThat(HttpStatusCode.OK).isEqualTo(response.status)
            assertThat("Hello, world!").isEqualTo(response.bodyAsText())
        }
}
