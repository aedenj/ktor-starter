@file:JvmName("Checks")

package com.example.health

import com.sksamuel.cohort.Cohort
import com.sksamuel.cohort.HealthCheckRegistry
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers

fun Application.module() {
    install(Cohort) {
        healthcheck("/readiness", HealthCheckRegistry(Dispatchers.Default) {})
        healthcheck("/liveness", HealthCheckRegistry(Dispatchers.Default) {})
    }
}
