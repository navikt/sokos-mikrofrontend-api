import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


val ktorVersion = "2.3.8"
val junitJupiterVersion = "5.10.0"
val logbackVersion = "1.5.2"
val logstashVersion = "7.4"
val jacksonVersion = "2.16.1"
val prometheusVersion = "1.12.3"
val natpryceVersion = "1.6.10.0"
val kotlinLoggingVersion = "3.0.5"

plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "no.nav.sokos"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-metrics-micrometer:$ktorVersion")
    implementation("io.ktor:ktor-server:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")

    // Security
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktorVersion")

    // Jackson
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    // Monitorering
    implementation("io.micrometer:micrometer-registry-prometheus:$prometheusVersion")

    // Logging
    implementation("ch.qos.logback:logback-core:$logbackVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")

    // Config
    implementation("com.natpryce:konfig:$natpryceVersion")
}


tasks {
    withType<KotlinCompile>().configureEach {
        compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }

    withType<ShadowJar>().configureEach {
        enabled = true
        archiveFileName.set("app.jar")
        manifest {
            attributes["Main-Class"] = "no.nav.sokos.mikrofrontendapi.BootstrapKt"
        }
    }

    ("jar") {
        enabled = false
    }

    withType<Wrapper>().configureEach {
        gradleVersion = "7.6"
    }
}