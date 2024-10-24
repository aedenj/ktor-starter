package com.example

import arrow.continuations.SuspendApp
import arrow.fx.coroutines.resourceScope
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.loadCommonConfiguration
import io.ktor.server.netty.NettyApplicationEngine
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay

/**
 * Main function for starting the service with graceful shutdown using Netty. This class copies most of it's content
 * from the EngineMain class of Ktor, but adds graceful shutdown capabilities with Arrow's SuspendApp library.
 */
object GracefulNettyMain {
    @JvmStatic
    fun main(args: Array<String>): Unit =
        SuspendApp {
            val env = commandLineEnvironment(args)

            resourceScope {
                install({
                    NettyApplicationEngine(env) { loadConfiguration(env.config) }.apply(ApplicationEngine::start)
                }) { engine, _ ->
                    if (!engine.environment.developmentMode) {
                        engine.environment.log.info(
                            "Shutdown delay of ${shutdownDelay(engine.environment.config)}ms, " +
                                "turn it off using io.ktor.development=true",
                        )
                        delay(shutdownDelay(engine.environment.config))
                    }
                    engine.environment.log.info("Shutting down HTTP server...")
                    engine.stop(gracePeriod(engine.environment.config), timeout(engine.environment.config))
                    engine.environment.log.info("HTTP server shutdown!")
                }

                awaitCancellation()
            }
        }

    private fun NettyApplicationEngine.Configuration.loadConfiguration(config: ApplicationConfig) {
        val deploymentConfig = config.config("ktor.deployment")
        loadCommonConfiguration(deploymentConfig)
        deploymentConfig.propertyOrNull("requestQueueLimit")?.getString()?.toInt()?.let {
            requestQueueLimit = it
        }
        deploymentConfig.propertyOrNull("runningLimit")?.getString()?.toInt()?.let {
            runningLimit = it
        }
        deploymentConfig.propertyOrNull("shareWorkGroup")?.getString()?.toBoolean()?.let {
            shareWorkGroup = it
        }
        deploymentConfig.propertyOrNull("responseWriteTimeoutSeconds")?.getString()?.toInt()?.let {
            responseWriteTimeoutSeconds = it
        }
        deploymentConfig.propertyOrNull("requestReadTimeoutSeconds")?.getString()?.toInt()?.let {
            requestReadTimeoutSeconds = it
        }
        deploymentConfig.propertyOrNull("tcpKeepAlive")?.getString()?.toBoolean()?.let {
            tcpKeepAlive = it
        }
        deploymentConfig.propertyOrNull("maxInitialLineLength")?.getString()?.toInt()?.let {
            maxInitialLineLength = it
        }
        deploymentConfig.propertyOrNull("maxHeaderSize")?.getString()?.toInt()?.let {
            maxHeaderSize = it
        }
        deploymentConfig.propertyOrNull("maxChunkSize")?.getString()?.toInt()?.let {
            maxChunkSize = it
        }
    }

    /**
     * The below functions only needed because the configuration on the NettyApplicationEngine is private and
     * there isn't a public interface for accessing the configuration values.
     */
    private fun shutdownDelay(config: ApplicationConfig): Long {
        return config.propertyOrNull("ktor.deployment.shutdownDelay")?.getString()?.toLong() ?: 0L
    }

    private fun gracePeriod(config: ApplicationConfig): Long {
        return config.propertyOrNull("ktor.deployment.shutdownGracePeriod")?.getString()?.toLong() ?: 0L
    }

    private fun timeout(config: ApplicationConfig): Long {
        return config.propertyOrNull("ktor.deployment.shutdownTimeout")?.getString()?.toLong() ?: 0L
    }
}
