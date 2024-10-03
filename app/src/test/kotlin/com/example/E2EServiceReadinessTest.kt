package com.example

import io.ktor.http.HttpStatusCode
import io.ktor.http.hostIsIp
import io.ktor.server.application.pluginOrNull
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.routing.IgnoreTrailingSlash
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

/**
 * The general goal of this system readiness test is to ensure that the system is ready to accept traffic. Revealing
 * simple failures severe enough to reject a prospective change in the context of all requisite components
 * and dependencies. The types of changes a readiness test can catch include:
 *
 * - Missing configuration
 * - Incorrect configuration of dependencies
 * - Missing service features. (e.g. Compression, DefaultHeaders, IgnoreTrailingSlash, etc.)
 */
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
    fun `service is ready`() {
        Given {
            hostIsIp(service.host)
            port(service.getMappedPort(SERVICE_PORT))
        } When {
            get("/readiness")
        } Then {
            statusCode(HttpStatusCode.OK.value)
        }
    }

    @Test
    @DisplayName("ignores trailing slash in all paths")
    fun testIgnoreTrailingSlash() =
        testApplication {
            application {
                assertThat(pluginOrNull(IgnoreTrailingSlash)).isNotNull()
                    .withFailMessage("IgnoreTrailingSlash plugin is not installed")
            }
        }

    @Test
    @DisplayName("responds with default headers")
    fun testDefaultHeaders() =
        testApplication {
            application {
                assertThat(pluginOrNull(DefaultHeaders)).isNotNull()
                    .withFailMessage("DefaultHeaders plugin is not installed")
            }
        }
}
