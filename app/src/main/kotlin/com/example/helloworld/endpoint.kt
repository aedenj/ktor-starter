package com.example.helloworld

import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.application.call
import io.ktor.server.response.respondText

fun Route.helloworld() {
    get("/") {
        call.respondText("Hello, world!")
    }
}