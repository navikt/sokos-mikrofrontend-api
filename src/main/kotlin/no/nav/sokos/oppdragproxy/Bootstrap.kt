package no.nav.sokos.oppdragproxy

import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.stop
import io.ktor.server.netty.Netty
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates
import no.nav.sokos.oppdragproxy.api.employeeApi
import no.nav.sokos.oppdragproxy.api.metricsApi
import no.nav.sokos.oppdragproxy.api.naisApi
import no.nav.sokos.oppdragproxy.config.installCommonFeatures
import no.nav.sokos.oppdragproxy.metrics.appStateReadyFalse
import no.nav.sokos.oppdragproxy.metrics.appStateRunningFalse

fun main() {
    val applicationState = ApplicationState()

    HttpServer(applicationState).start()
}

class HttpServer(
    private val applicationState: ApplicationState,
    port: Int = 8080,
) {
    private val embeddedServer = embeddedServer(Netty, port) {
        installCommonFeatures()
        metricsApi()
        naisApi({ applicationState.initialized }, { applicationState.running })
        employeeApi()
    }

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            this.stop()
        })
    }

    fun start() {
        applicationState.running = true
        embeddedServer.start(wait = true)
    }

    private fun stop() {
        applicationState.running = false
        embeddedServer.stop(5, 5, TimeUnit.SECONDS)
    }
}

class ApplicationState(
    alive: Boolean = true,
    ready: Boolean = false
) {
    var initialized: Boolean by Delegates.observable(alive) { _, _, newValue ->
        if (!newValue) appStateReadyFalse.inc()
    }
    var running: Boolean by Delegates.observable(ready) { _, _, newValue ->
        if (!newValue) appStateRunningFalse.inc()
    }
}
