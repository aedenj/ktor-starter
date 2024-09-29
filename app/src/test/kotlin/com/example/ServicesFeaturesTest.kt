package com.example

import io.ktor.server.application.pluginOrNull
import io.ktor.server.routing.IgnoreTrailingSlash
import io.ktor.server.testing.testApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("services features")
class ServicesFeaturesTest {
    @Test
    @DisplayName("ignores trailing slash in path")
    fun testIgnoreTrailingSlash() =
        testApplication {
            application {
                assertThat(pluginOrNull(IgnoreTrailingSlash)).isNotNull()
            }
        }
}
