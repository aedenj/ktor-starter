import com.adarshr.gradle.testlogger.theme.ThemeType

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.jvm)

    // Apply the application plugin to add support for building a CLI application in Java.
    application

    // https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin#jib---containerize-your-gradle-java-project
    alias(libs.plugins.jib)

    // https://github.com/JLLeitschuh/ktlint-gradle
    alias(libs.plugins.ktlint)

    // https://kotlin.github.io/kotlinx-kover/gradle-plugin/
    alias(libs.plugins.kover)

    // https://detekt.dev/
    alias(libs.plugins.detekt)

    // https://plugins.gradle.org/plugin/com.adarshr.test-logger
    alias(libs.plugins.testlogger)

    // https://github.com/dorongold/gradle-task-tree -- very helpful for debugging gradle tasks
    alias(libs.plugins.tasktree)
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    listOf(
        libs.kotlin.stdlib,
        libs.ktor.server.core,
        libs.ktor.server.netty,
        libs.ktor.defaultheaders,
        libs.ktor.compression,
        libs.cohort,
        tegralLibs.openapi.ktor,
        tegralLibs.openapi.ktorui,
        libs.logback,
    ).forEach { implementation(it) }

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(platform(libs.testcontainers.bom))
    listOf(
        libs.restassured,
        libs.restassured.kotlinext,
        libs.junit.jupiter,
        libs.assertj,
        libs.ktor.server.test,
        libs.testcontainers.core,
        libs.testcontainers.junit,
    ).forEach { testImplementation(it) }
}

val jvmOpts =
    listOf(
        "-server",
        "-Djava.awt.headless=true",
        "-XX:+UseG1GC",
        "-Djava.security.egd=file:/dev/./urandom",
    ) + (System.getenv("APP_OPTS")?.split(" ") ?: emptyList())

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
    version = "0.1"
    applicationDefaultJvmArgs = jvmOpts
}

tasks {
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(11)
        }
    }

    jib {
        from {
            image = "eclipse-temurin:11-jre"
            platforms {
                platform {
                    architecture = "arm64"
                    os = "linux"
                }
                platform {
                    architecture = "amd64"
                    os = "linux"
                }
            }
        }

        to {
            image = "${project.parent?.name}-${project.name}:latest"
        }

        container {
            jvmFlags = jvmOpts
        }
    }

    kover {
        reports {
            verify {
                rule {
                    minBound(90)
                }
            }
        }
    }

    test {
        useJUnitPlatform()

        testlogger {
            theme = ThemeType.MOCHA
            slowThreshold = 5000
            showStandardStreams = true
            showFullStackTraces = false
            logLevel = LogLevel.QUIET
        }
    }
}
