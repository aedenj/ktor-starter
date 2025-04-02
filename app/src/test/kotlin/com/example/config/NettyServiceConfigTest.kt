package com.example.config

import com.example.Environment
import io.ktor.server.netty.NettyApplicationEngine
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

@DisplayName("netty service config creation")
class NettyServiceConfigTest {
    private lateinit var config: NettyServiceConfig

    @Test
    @DisplayName("all fields are mapped when local env has no override")
    fun `all fields are mapped when local env has no overrides`() {
        config = NettyServiceConfig.create(Environment.LOCAL)

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
    @DisplayName("all fields are mapped when test env has some overrides")
    fun `all fields are mapped when test env has some overrides`() {
        config = NettyServiceConfig.create(Environment.TEST)

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
    @DisplayName("map to netty engine config")
    fun `map to netty engine config`() {
        val nettyConfig =
            NettyApplicationEngine.Configuration().apply {
                loadConfiguration(
                    NettyDeploymentConfig(
                        connectionGroupSize = 15,
                        workerGroupSize = 10,
                        callGroupSize = 10,
                        shutdownGracePeriod = 567L,
                        shutdownTimeout = 999L,
                        requestQueueLimit = 10,
                        runningLimit = 10,
                        shareWorkGroup = false,
                        responseWriteTimeoutSeconds = 60,
                        requestReadTimeoutSeconds = 60,
                        tcpKeepAlive = true,
                        maxInitialLineLength = 4096,
                        maxHeaderSize = 8192,
                        maxChunkSize = 16384,
                    ),
                )
            }

        assertAll(
            { assertThat(nettyConfig.connectionGroupSize).isEqualTo(15) },
            { assertThat(nettyConfig.workerGroupSize).isEqualTo(10) },
            { assertThat(nettyConfig.callGroupSize).isEqualTo(10) },
            { assertThat(nettyConfig.shutdownGracePeriod).isEqualTo(567L) },
            { assertThat(nettyConfig.shutdownTimeout).isEqualTo(999L) },
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
        val nettyConfig =
            NettyApplicationEngine.Configuration().apply {
                loadConfiguration(
                    NettyDeploymentConfig(
                        connectionGroupSize = null,
                        workerGroupSize = null,
                        callGroupSize = null,
                        requestQueueLimit = null,
                        runningLimit = null,
                        shareWorkGroup = null,
                        responseWriteTimeoutSeconds = null,
                        requestReadTimeoutSeconds = null,
                        tcpKeepAlive = null,
                        maxInitialLineLength = null,
                        maxHeaderSize = null,
                        maxChunkSize = null,
                    ),
                )
            }

        assertAll(
            { assertThat(nettyConfig.connectionGroupSize).isEqualTo(nettyConfig.parallelism / 2 + 1) },
            { assertThat(nettyConfig.workerGroupSize).isEqualTo(nettyConfig.parallelism / 2 + 1) },
            { assertThat(nettyConfig.callGroupSize).isEqualTo(nettyConfig.parallelism) },
            { assertThat(nettyConfig.shutdownGracePeriod).isEqualTo(500L) },
            { assertThat(nettyConfig.shutdownTimeout).isEqualTo(500L) },
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
