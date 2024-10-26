package com.example.config

import com.sksamuel.hoplite.ConfigException
import io.ktor.server.netty.NettyApplicationEngine
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class NettyServiceConfigTest {
    private lateinit var config: NettyServiceConfig

    @Test
    @DisplayName("all fields are mapped with no overrides")
    fun `all fields are mapped with no overrides`() {
        config = NettyServiceConfig.create("no-overrides")

        assertAll(
            { assertThat(config.deployment.connectionGroupSize).isEqualTo(10) },
            { assertThat(config.deployment.workerGroupSize).isEqualTo(10) },
            { assertThat(config.deployment.callGroupSize).isEqualTo(10) },
            { assertThat(config.deployment.shutdownGracePeriod).isEqualTo(345L) },
            { assertThat(config.deployment.shutdownTimeout).isEqualTo(678L) },
            { assertThat(config.deployment.shutdownDelay).isEqualTo(12_345L) },
            { assertThat(config.deployment.requestQueueLimit).isEqualTo(10) },
            { assertThat(config.deployment.runningLimit).isEqualTo(10) },
            { assertThat(config.deployment.shareWorkGroup).isEqualTo(false) },
            { assertThat(config.deployment.responseWriteTimeoutSeconds).isEqualTo(60) },
            { assertThat(config.deployment.requestReadTimeoutSeconds).isEqualTo(60) },
            { assertThat(config.deployment.tcpKeepAlive).isEqualTo(true) },
            { assertThat(config.deployment.maxInitialLineLength).isEqualTo(4096) },
            { assertThat(config.deployment.maxHeaderSize).isEqualTo(8192) },
            { assertThat(config.deployment.maxChunkSize).isEqualTo(8192) },
        )
    }

    @Test
    @DisplayName("all fields are mapped with some overrides")
    fun `all fields are mapped with some overrides`() {
        config = NettyServiceConfig.create("with-overrides")

        assertAll(
            { assertThat(config.deployment.connectionGroupSize).isEqualTo(15) },
            { assertThat(config.deployment.workerGroupSize).isEqualTo(10) },
            { assertThat(config.deployment.callGroupSize).isEqualTo(10) },
            { assertThat(config.deployment.shutdownGracePeriod).isEqualTo(567L) },
            { assertThat(config.deployment.shutdownTimeout).isEqualTo(999L) },
            { assertThat(config.deployment.shutdownDelay).isEqualTo(9876L) },
            { assertThat(config.deployment.requestQueueLimit).isEqualTo(10) },
            { assertThat(config.deployment.runningLimit).isEqualTo(10) },
            { assertThat(config.deployment.shareWorkGroup).isEqualTo(false) },
            { assertThat(config.deployment.responseWriteTimeoutSeconds).isEqualTo(60) },
            { assertThat(config.deployment.requestReadTimeoutSeconds).isEqualTo(60) },
            { assertThat(config.deployment.tcpKeepAlive).isEqualTo(true) },
            { assertThat(config.deployment.maxInitialLineLength).isEqualTo(4096) },
            { assertThat(config.deployment.maxHeaderSize).isEqualTo(8192) },
            { assertThat(config.deployment.maxChunkSize).isEqualTo(16384) },
        )
    }

    @Test
    @DisplayName("using a non-existent env throws")
    fun `using a non-existent env throws`() {
        assertThatThrownBy { NettyServiceConfig.create("non-existent") }
            .isInstanceOf(ConfigException::class.java)
            .hasMessageContaining("Could not find /application.non-existent.conf")
    }

    @Test
    @DisplayName("map to netty engine config")
    fun `map to netty engine config`() {
        config = NettyServiceConfig.create("with-overrides")

        val nettyConfig = NettyApplicationEngine.Configuration().apply { loadConfiguration(config.deployment) }

        assertAll(
            { assertThat(nettyConfig.connectionGroupSize).isEqualTo(15) },
            { assertThat(nettyConfig.workerGroupSize).isEqualTo(10) },
            { assertThat(nettyConfig.callGroupSize).isEqualTo(10) },
            { assertThat(nettyConfig.shutdownGracePeriod).isEqualTo(567L) },
            { assertThat(nettyConfig.shutdownTimeout).isEqualTo(999L) },
            { assertThat(nettyConfig.requestQueueLimit).isEqualTo(10) },
            { assertThat(nettyConfig.runningLimit).isEqualTo(10) },
            { assertThat(nettyConfig.shareWorkGroup).isEqualTo(false) },
            { assertThat(nettyConfig.responseWriteTimeoutSeconds).isEqualTo(60) },
            { assertThat(nettyConfig.requestReadTimeoutSeconds).isEqualTo(60) },
            { assertThat(nettyConfig.tcpKeepAlive).isEqualTo(true) },
            { assertThat(nettyConfig.maxInitialLineLength).isEqualTo(4096) },
            { assertThat(nettyConfig.maxHeaderSize).isEqualTo(8192) },
            { assertThat(nettyConfig.maxChunkSize).isEqualTo(16384) },
        )
    }

    @Test
    @DisplayName("map to netty engine config using all defaults")
    fun `map to netty engine config using all defaults`() {
        config = NettyServiceConfig.create("no-overrides", "/application.empty.conf")

        val nettyConfig =
            NettyApplicationEngine.Configuration().apply {
                loadConfiguration(config.deployment)
            }

        assertAll(
            { assertThat(nettyConfig.connectionGroupSize).isEqualTo(nettyConfig.parallelism / 2 + 1) },
            { assertThat(nettyConfig.workerGroupSize).isEqualTo(nettyConfig.parallelism / 2 + 1) },
            { assertThat(nettyConfig.callGroupSize).isEqualTo(nettyConfig.parallelism) },
            { assertThat(nettyConfig.shutdownGracePeriod).isEqualTo(500L) },
            { assertThat(nettyConfig.shutdownTimeout).isEqualTo(500L) },
            { assertThat(nettyConfig.requestQueueLimit).isEqualTo(16) },
            { assertThat(nettyConfig.runningLimit).isEqualTo(32) },
            { assertThat(nettyConfig.shareWorkGroup).isEqualTo(false) },
            { assertThat(nettyConfig.responseWriteTimeoutSeconds).isEqualTo(10) },
            { assertThat(nettyConfig.requestReadTimeoutSeconds).isEqualTo(0) },
            { assertThat(nettyConfig.tcpKeepAlive).isEqualTo(false) },
            { assertThat(nettyConfig.maxInitialLineLength).isEqualTo(4096) },
            { assertThat(nettyConfig.maxHeaderSize).isEqualTo(8192) },
            { assertThat(nettyConfig.maxChunkSize).isEqualTo(8192) },
        )
    }
}
