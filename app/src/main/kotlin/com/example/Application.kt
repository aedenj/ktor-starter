@file:JvmName("Application")

package com.example

import com.example.helloworld.helloworld
import guru.zoroark.tegral.openapi.ktor.TegralOpenApiKtor
import guru.zoroark.tegral.openapi.ktor.openApiEndpoint
import guru.zoroark.tegral.openapi.ktorui.TegralSwaggerUiKtor
import guru.zoroark.tegral.openapi.ktorui.swaggerUiEndpoint
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.routing.IgnoreTrailingSlash
import io.ktor.server.routing.routing

fun main(args: Array<String>): Unit = GracefulNettyMain.main(args)

fun Application.module() {
    install(IgnoreTrailingSlash)
    install(DefaultHeaders)
    install(Compression)
    install(TegralOpenApiKtor) {
        title = "Ktor Starter App"
        description = "A simple Ktor starter app."
        version = "0.1"
    }
    install(TegralSwaggerUiKtor)

    routing {
        helloworld()

        openApiEndpoint("/openapi")
        swaggerUiEndpoint(path = "/swagger", openApiPath = "/openapi")
    }
}
