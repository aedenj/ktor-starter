package com.example.smoke

import com.example.Environment
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

object ServiceContainer {
    const val PORT = 8080
    const val IMAGE_NAME = "ktor-starter-app:latest"

    fun create(env: Environment): GenericContainer<*> =
        GenericContainer(DockerImageName.parse(IMAGE_NAME))
            .withExposedPorts(PORT)
            .withEnv("KTOR_ENV", env.toString())
}
