@file:JvmName("Checks")

package com.example.health

import com.sksamuel.cohort.Cohort
import com.sksamuel.cohort.HealthCheckRegistry
import io.ktor.server.application.Application
import io.ktor.server.application.install
import kotlinx.coroutines.Dispatchers

fun Application.module() {
    install(Cohort) {
        healthcheck("/startup", startupProbes())
        healthcheck("/readiness", readinessProbes())
        healthcheck("/liveness", livenessProbes())
    }
}

fun startupProbes() = HealthCheckRegistry(Dispatchers.Default) { }

fun readinessProbes() = HealthCheckRegistry(Dispatchers.Default) { }

fun livenessProbes() = HealthCheckRegistry(Dispatchers.Default) { }
