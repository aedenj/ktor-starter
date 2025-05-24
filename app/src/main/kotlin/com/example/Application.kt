@file:JvmName("Application")

package com.example

import com.example.helloworld.helloworld
import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktoropenapi.route
import io.github.smiley4.ktorswaggerui.swaggerUI
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
    install(OpenApi) {
        info {
            title = "Ktor Starter"
            description = "A starter for Ktor microservices"
            version = "1.0.0"
        }
    }

    routing {
        helloworld()

        route("/openapi") {
            openApi()
        }

        route("/swagger") {
            swaggerUI("/openapi")
        }
    }
}
