package com.example.helloworld

import io.github.smiley4.ktoropenapi.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route

fun Route.helloworld() {
    get("/", {
        description = "Returns a hello world message"
        response {
            code(HttpStatusCode.OK) {
                description = "A success response"
                body<String>()
            }
        }
    }) {
        call.respondText("Hello, world!")
    }
}
