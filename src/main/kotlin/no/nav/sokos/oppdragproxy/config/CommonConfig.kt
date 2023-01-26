package no.nav.sokos.oppdragproxy.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callIdMdc
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.request.path
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.binder.system.UptimeMetrics
import mu.KotlinLogging
import org.slf4j.event.Level
import java.util.UUID
import no.nav.sokos.oppdragproxy.metrics.prometheusRegistry

private val log = KotlinLogging.logger {}
const val APP_ENDPOINT = "sokos-oppdrag-proxy"

fun Application.installCommonFeatures() {
    install(CallId) {
        header(HttpHeaders.XCorrelationId)
        generate { UUID.randomUUID().toString() }
        verify { it.isNotEmpty() }
    }
    install(CallLogging) {
        logger = log
        level = Level.INFO
        callIdMdc(HttpHeaders.XCorrelationId)
        filter { call -> call.request.path().startsWith("/api/$APP_ENDPOINT") }
        disableDefaultColors()
    }
    install(ContentNegotiation) {
        jackson {
            registerKotlinModule()
            registerModule(JavaTimeModule())
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    install(MicrometerMetrics) {
        registry = prometheusRegistry
        meterBinders = listOf(
            UptimeMetrics(),
            JvmMemoryMetrics(),
            JvmGcMetrics(),
            JvmThreadMetrics(),
            ProcessorMetrics()
        )
    }
    install(CORS) {
        allowCredentials = true
        allowHost("okonomiportalen.dev.intern.nav.no", listOf("https"))
        allowMethod(HttpMethod.Get)
    }
}
