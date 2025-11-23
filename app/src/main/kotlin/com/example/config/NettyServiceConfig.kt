package com.example.config

import com.example.Environment
import com.sksamuel.hoplite.ConfigAlias
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import io.ktor.server.netty.NettyApplicationEngine

data class NettyServiceConfig(
    @param:ConfigAlias("deployment") val deployment: NettyDeploymentConfig,
) {
    companion object {
        fun create(env: Environment): NettyServiceConfig =
            ConfigLoaderBuilder.Companion
                .default()
                .addResourceSource("/application.$env.conf")
                .build()
                .loadConfigOrThrow<NettyServiceConfig>(prefix = "ktor")
    }
}

data class NettyDeploymentConfig(
    val connectionGroupSize: Int?,
    val workerGroupSize: Int?,
    val callGroupSize: Int?,
    val shutdownGracePeriod: Long = 500L,
    val shutdownTimeout: Long = 500L,
    val shutdownDelay: Long = 30_000L,
    val requestQueueLimit: Int?,
    val runningLimit: Int?,
    val shareWorkGroup: Boolean?,
    val responseWriteTimeoutSeconds: Int?,
    val requestReadTimeoutSeconds: Int?,
    val tcpKeepAlive: Boolean?,
    val maxInitialLineLength: Int?,
    val maxHeaderSize: Int?,
    val maxChunkSize: Int?,
)

fun NettyApplicationEngine.Configuration.loadConfiguration(config: NettyDeploymentConfig) {
    loadBase(config)

    config.runningLimit?.let { runningLimit = it }
    config.shareWorkGroup?.let { shareWorkGroup = it }
    config.responseWriteTimeoutSeconds?.let { responseWriteTimeoutSeconds = it }
    config.requestReadTimeoutSeconds?.let { requestReadTimeoutSeconds = it }
    config.tcpKeepAlive?.let { tcpKeepAlive = it }
    config.maxInitialLineLength?.let { maxInitialLineLength = it }
    config.maxHeaderSize?.let { maxHeaderSize = it }
    config.maxChunkSize?.let { maxChunkSize = it }
}

fun NettyApplicationEngine.Configuration.loadBase(config: NettyDeploymentConfig) {
    config.connectionGroupSize?.let { connectionGroupSize = it }
    config.workerGroupSize?.let { workerGroupSize = it }
    config.callGroupSize?.let { callGroupSize = it }
    config.shutdownGracePeriod.let { shutdownGracePeriod = it }
    config.shutdownTimeout.let { shutdownTimeout = it }
}
