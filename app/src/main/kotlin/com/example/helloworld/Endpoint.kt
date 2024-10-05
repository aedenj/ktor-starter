package com.example.helloworld

import guru.zoroark.tegral.openapi.dsl.schema
import guru.zoroark.tegral.openapi.ktor.describe
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.helloworld() {
    get("/") {
        call.respondText("Hello, world!")
    } describe {
        description = "Returns a simple greeting."
        HttpStatusCode.OK.value response {
            description = "The greeting."
            plainText { schema("Hello, world!") }
        }
    }
}
