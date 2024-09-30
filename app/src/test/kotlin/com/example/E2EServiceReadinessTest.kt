package com.example

import io.ktor.http.HttpStatusCode
import io.ktor.http.hostIsIp
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName


@DisplayName("e2e system readiness test")
@Testcontainers
class E2EServiceReadinessTest {
    companion object {
        const val SERVICE_PORT = 8080

        @Container
        private val service =
            GenericContainer(DockerImageName.parse("ktor-starter-app:latest"))
                .withExposedPorts(SERVICE_PORT)
    }

    @AfterAll
    fun tearDown() {
        service.stop()
        service.close()
    }

    @Test
    @DisplayName("service is ready")
    fun `service is ready`()  {
            Given {
                hostIsIp(service.host)
                port(service.getMappedPort(SERVICE_PORT))
            } When {
                get("/readiness")
            } Then {
                statusCode(HttpStatusCode.OK.value)
            }
        }
}
