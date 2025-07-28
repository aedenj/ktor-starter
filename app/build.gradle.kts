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
        libs.smiley4.openapi,
        libs.smiley4.swaggerui,
        libs.arrow.suspendapp,
        libs.arrow.suspendapp.ktor,
        libs.hoplite.core,
        libs.hoplite.hocon,
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

/**
 * Since Ktor v2.3.0 shutdown hook was added to Ktor engines e.g. CIO and Netty, making Ktor server to
 * stop before waiting a preWait duration configured by SuspendApp. This flag disables the shutdown hook
 * in favor of the SuspendApp graceful shutdown mechanism. See https://github.com/arrow-kt/suspendapp/issues/115
 */
val baseJvmOpts =
    listOf(
        "-server",
        "-XX:+UseG1GC",
        "-Djava.awt.headless=true",
        "-Djava.security.egd=file:/dev/./urandom",
        "-Dio.ktor.server.engine.ShutdownHook=false",
    ) + (System.getenv("APP_OPTS")?.split(" ") ?: emptyList())

application {
    mainClass.set("com.example.GracefulNettyMain")
    version = "0.1"
    applicationDefaultJvmArgs = baseJvmOpts
}

tasks {
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    jib {
        from {
            image = "eclipse-temurin:17-jre"
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
            jvmFlags = baseJvmOpts + listOf("-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=80")
            mainClass = application.mainClass.get()
        }
    }

    kover {
        reports {
            filters {
                excludes {
                    classes(
                        "com.example.GracefulNettyMain*",
                        "com.example.Application\$main\$1",
                    )
                }
            }

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
            showStandardStreams = false
            showFullStackTraces = false
            logLevel = LogLevel.QUIET
        }
    }
}
