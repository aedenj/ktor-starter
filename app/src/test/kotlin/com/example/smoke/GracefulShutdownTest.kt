package com.example.smoke

import io.ktor.http.HttpStatusCode
import io.ktor.http.hostIsIp
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.apache.http.NoHttpResponseException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.testcontainers.containers.output.WaitingConsumer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.concurrent.TimeUnit

@DisplayName("graceful shutdown")
@Testcontainers
@TestMethodOrder(OrderAnnotation::class)
class GracefulShutdownTest {
    companion object {
        private val consumer = WaitingConsumer()

        @Container
        private val service = ServiceContainer.create()
    }

    @BeforeAll
    fun setUp() {
        service.start()
        service.followOutput(consumer)
        service.dockerClient.killContainerCmd(service.containerId).withSignal("SIGTERM").exec()
    }

    @AfterAll
    fun tearDown() {
        service.close()
        service.stop()
    }

    @Test
    @DisplayName("first, logs a pre-shutdown message")
    @Order(1)
    fun `first, logs a pre-shutdown message`() {
        consumer.waitUntil({ frame ->
            frame.utf8String.contains("Shutdown delay of 5000ms, turn it off using io.ktor.development=true")
        }, 1, TimeUnit.SECONDS)
    }

    @Test
    @DisplayName("still responds with http ok response from the liveness probe")
    @Order(2)
    fun `still responds with http ok response from the liveness probe`() {
        Given {
            hostIsIp(service.host)
            port(service.getMappedPort(ServiceContainer.PORT))
        } When {
            get("/liveness")
        } Then {
            statusCode(HttpStatusCode.OK.value)
        }
    }

    @Test
    @DisplayName("logs pre-shutdown message after the pause is over")
    @Order(3)
    fun `logs pre-shutdown message after the pause is over`() {
        consumer.waitUntil({ frame ->
            frame.utf8String.contains("Shutting down HTTP server...")
        }, 5, TimeUnit.SECONDS)
    }

    @Test
    @DisplayName("finally, logs shutdown is complete")
    @Order(4)
    fun `finally, logs shutdown is complete`() {
        consumer.waitUntil({ frame ->
            frame.utf8String.contains("HTTP server shutdown!")
        }, 2, TimeUnit.SECONDS)
    }

    @Test
    @DisplayName("and the service does not respond after shutdown")
    @Order(5)
    fun `and the service does not respond after shutdown`() {
        assertThatThrownBy {
            Given {
                hostIsIp(service.host)
                port(service.getMappedPort(ServiceContainer.PORT))
            } When {
                get("/liveness")
            } Then {
                statusCode(HttpStatusCode.OK.value)
            }
        }.isInstanceOf(NoHttpResponseException::class.java)
    }
}
