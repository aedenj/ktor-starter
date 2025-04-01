package com.example.health

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("health checks")
class HealthChecksTest {
    @Test
    @DisplayName("startup path returns ok")
    fun testStartup() =
        testApplication {
            application { module() }

            val response = client.get("/startup")
            assertThat(HttpStatusCode.OK).isEqualTo(response.status)
        }

    @Test
    @DisplayName("readiness path returns ok")
    fun testReadiness() =
        testApplication {
            application { module() }

            val response = client.get("/readiness")
            assertThat(HttpStatusCode.OK).isEqualTo(response.status)
        }

    @Test
    @DisplayName("liveness path returns ok")
    fun testLiveness() =
        testApplication {
            application { module() }

            val response = client.get("/liveness")
            assertThat(HttpStatusCode.OK).isEqualTo(response.status)
        }
}
