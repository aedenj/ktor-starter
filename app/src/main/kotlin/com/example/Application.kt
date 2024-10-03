@file:JvmName("Application")

package com.example

import com.example.helloworld.helloworld
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.routing.IgnoreTrailingSlash
import io.ktor.server.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(IgnoreTrailingSlash)
    install(DefaultHeaders)
    install(Compression)

    routing {
        helloworld()
    }
}
