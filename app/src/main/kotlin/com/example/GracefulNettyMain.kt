package com.example

import arrow.continuations.SuspendApp
import arrow.continuations.ktor.server
import arrow.fx.coroutines.resourceScope
import com.example.config.NettyServiceConfig
import com.example.config.loadConfiguration
import io.ktor.server.config.tryGetString
import io.ktor.server.engine.CommandLineConfig
import io.ktor.server.netty.Netty
import kotlinx.coroutines.awaitCancellation
import kotlin.time.Duration.Companion.milliseconds

object GracefulNettyMain {
    @JvmStatic
    fun main(args: Array<String>): Unit =
        SuspendApp {
            val cmdLineConfig = CommandLineConfig(args)
            val env =
                Environment.valueOf(
                    cmdLineConfig.rootConfig.environment.config.tryGetString("ktor.environment")!!.uppercase(),
                )
            val config = NettyServiceConfig.create(env)

            println("millisecond delay: ${config.deployment.shutdownDelay.milliseconds}")
            resourceScope {
                server(
                    factory = Netty,
                    rootConfig = cmdLineConfig.rootConfig,
                    configure = {
                        takeFrom(cmdLineConfig.engineConfig)
                        loadConfiguration(config.deployment)
                    },
                    preWait = config.deployment.shutdownDelay.milliseconds,
                    grace = config.deployment.shutdownGracePeriod.milliseconds,
                    timeout = config.deployment.shutdownTimeout.milliseconds,
                )

                awaitCancellation()
            }
        }
}