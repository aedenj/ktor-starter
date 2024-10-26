package com.example

import arrow.continuations.SuspendApp
import arrow.fx.coroutines.resourceScope
import com.example.config.NettyServiceConfig
import com.example.config.loadConfiguration
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay

/**
 * Main function for starting the service with graceful shutdown using Netty. This class copies most of it's content
 * from the EngineMain class of Ktor, but adds graceful shutdown capabilities with Arrow's SuspendApp library.
 *
 */
object GracefulNettyMain {
    @JvmStatic
    fun main(args: Array<String>): Unit =
        SuspendApp {
            val env = commandLineEnvironment(args)
            val config = NettyServiceConfig.create("test")

            /**
             * Unfortunately, the current helper method, named server, in the SuspendApp library that encapsulates
             * this install and release logic isn't flexible enough to allow for the configuration of the server as
             * we have it here. Therefore, we have to manually create the resourceScope and call the install and
             * release methods ourselves. The next release of SuspendApp will have a more flexible server helper.
             */
            resourceScope {
                install({
                    embeddedServer(Netty, env) { loadConfiguration(config.deployment) }.apply(ApplicationEngine::start)
                }) { engine, _ ->
                    engine.release(
                        config.deployment.shutdownDelay,
                        config.deployment.shutdownGracePeriod,
                        config.deployment.shutdownTimeout,
                    )
                }

                awaitCancellation()
            }
        }
}

suspend fun ApplicationEngine.release(
    preWait: Long,
    grace: Long,
    timeout: Long,
) {
    if (!environment.developmentMode) {
        environment.log.info(
            "Shutdown delay of ${preWait}ms, turn it off using io.ktor.development=true",
        )
        delay(preWait)
    }
    environment.log.info("Shutting down HTTP server...")
    stop(grace, timeout)
    environment.log.info("HTTP server shutdown!")
}
