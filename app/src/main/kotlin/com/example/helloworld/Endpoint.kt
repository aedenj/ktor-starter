package com.example.helloworld

import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.helloworld() {
    get("/") {
        call.respondText("Hello, world!")
    }
}
