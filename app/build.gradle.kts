import com.adarshr.gradle.testlogger.theme.ThemeType

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.jvm)

    // Apply the application plugin to add support for building a CLI application in Java.
    application

    // Provides the ability to package and containerize your Ktor application
    alias(libs.plugins.ktor)

    // https://github.com/JLLeitschuh/ktlint-gradle
    alias(libs.plugins.ktlint)

    // https://kotlin.github.io/kotlinx-kover/gradle-plugin/
    alias(libs.plugins.kover)

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
        libs.cohort,
        libs.logback,
    ).forEach { implementation(it) }

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
    applicationDefaultJvmArgs =
        listOf(
            "-Dio.ktor.development=true",
            "-Djava.security.egd=file:/dev/./urandom",
            "-XX:+UseContainerSupport",
            "-XX:MaxRAMPercentage=80",
        )
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
            showStandardStreams = false
            showFullStackTraces = false
            logLevel = LogLevel.QUIET
        }
    }
}
