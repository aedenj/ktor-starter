
import com.adarshr.gradle.testlogger.theme.ThemeType

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.jvm)

    // Apply the application plugin to add support for building a CLI application in Java.
    application

    // Provides the ability to package and containerize your Ktor application
    alias(libs.plugins.ktor)

    // https://plugins.gradle.org/plugin/com.adarshr.test-logger
    alias(libs.plugins.testlogger)
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
        libs.logback,
    ).forEach { implementation(it) }

    // Use JUnit Jupiter for testing.
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    listOf(
        libs.junit.jupiter,
        libs.assertj,
        libs.ktor.server.test,
    ).forEach { testImplementation(it) }

}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
    version = "0.1"
}

tasks {
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    ktor {
        fatJar {
            archiveFileName.set("${project.parent?.name}-${project.name}-${project.version}.jar")
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