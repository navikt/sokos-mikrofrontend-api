import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask


val ktorVersion = "2.3.2"
val junitJupiterVersion = "5.9.1"
val logbackVersion = "1.4.5"
val logstashVersion = "7.2"
val jacksonVersion = "2.14.1"
val prometheusVersion = "1.10.3"
val natpryceVersion = "1.6.10.0"
val kotlinLoggingVersion = "3.0.4"
val graphqlClientVersion = "7.0.0-alpha.0"
val cucumberVersion = "7.11.0"
val junit_version= "5.9.0"
val mockk_version= "1.13.4"
val assertj_version = "3.24.2"

plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("com.expediagroup.graphql") version "6.5.3"
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

    // PDL grapql
    implementation("com.expediagroup:graphql-kotlin-ktor-client:$graphqlClientVersion")
    {
        exclude(group = "com.expediagroup", module = "graphql-kotlin-client-serialization")
    }
    implementation("com.expediagroup:graphql-kotlin-client-jackson:$graphqlClientVersion")

    // Test
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
    testImplementation("io.mockk:mockk:$mockk_version")
    testImplementation("org.assertj:assertj-core:$assertj_version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")

    // Avhengigheter for Cucumber-tester
    testImplementation("io.cucumber:cucumber-junit:$cucumberVersion")
    testImplementation("io.cucumber:cucumber-java8:$cucumberVersion")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:$junit_version")
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


    withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging {
            exceptionFormat = FULL
            events("passed", "skipped", "failed")
        }

        // For å øke hastigheten på build kan vi benytte disse metodene
        maxParallelForks = Runtime.getRuntime().availableProcessors() / 2
        reports.forEach { report -> report.required.value(false) }
    }

    withType<Wrapper>().configureEach {
        gradleVersion = "7.6"
    }
}

val graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {
    packageName.set("no.nav.pdl")
    schemaFile.set(file("${project.projectDir}/src/main/resources/graphql/pdl.graphqls"))
    queryFileDirectory.set(file("${project.projectDir}/src/main/resources/graphql"))
}