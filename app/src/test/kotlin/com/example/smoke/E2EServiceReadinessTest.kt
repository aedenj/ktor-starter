package com.example.smoke

import com.example.Environment
import com.example.module
import io.ktor.http.HttpStatusCode
import io.ktor.http.hostIsIp
import io.ktor.server.application.pluginOrNull
import io.ktor.server.plugins.compression.Compression
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
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

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
        @Container
        private val service = ServiceContainer.create(Environment.LOCAL)
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
            port(service.getMappedPort(ServiceContainer.PORT))
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
                module()

                assertThat(pluginOrNull(IgnoreTrailingSlash)).isNotNull()
                    .withFailMessage("IgnoreTrailingSlash plugin is not installed")
            }
        }

    @Test
    @DisplayName("responds with default headers")
    fun testDefaultHeaders() =
        testApplication {
            application {
                module()

                assertThat(pluginOrNull(DefaultHeaders)).isNotNull()
                    .withFailMessage("DefaultHeaders plugin is not installed")
            }
        }

    @Test
    @DisplayName("compress outgoing content")
    fun testCompression() =
        testApplication {
            application {
                module()

                assertThat(pluginOrNull(Compression)).isNotNull()
                    .withFailMessage("Compression plugin is not installed")
            }
        }
}
