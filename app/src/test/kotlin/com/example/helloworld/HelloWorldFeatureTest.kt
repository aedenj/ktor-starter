package com.example.helloworld

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.hostIsIp
import io.ktor.server.testing.testApplication
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName


@DisplayName("hello world feature")
@Testcontainers
class HelloWorldFeatureTest {

    @Test
    @DisplayName("root path returns hello world")
    fun `service is ready`() = testApplication {
        val response = client.get("/")

        assertThat(HttpStatusCode.OK).isEqualTo(response.status)
        assertThat("Hello, world!").isEqualTo(response.bodyAsText())
    }
}
