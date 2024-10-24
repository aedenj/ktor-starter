package com.example.smoke

import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

object ServiceContainer {
    const val PORT = 8080
    const val IMAGE_NAME = "ktor-starter-app:latest"

    fun create(): GenericContainer<*> {
        return GenericContainer(DockerImageName.parse(IMAGE_NAME))
            .withExposedPorts(PORT)
            .withCommand(
                "-config=application.conf",
                "-config=application.test.conf",
            )
    }
}
