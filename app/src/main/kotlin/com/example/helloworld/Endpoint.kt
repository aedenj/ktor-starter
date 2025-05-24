package com.example.helloworld

import io.github.smiley4.ktoropenapi.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route

fun Route.helloworld() {
    get("/", {
        description = "A Hello World endpoint."
        response {
            code(HttpStatusCode.OK) {
                description = "Generates a worldly greeting."
            }
        }
    }) {
        call.respondText("Hello World!")
    }
}
