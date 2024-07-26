package no.nav.sokos.mikrofrontendapi

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.stop
import io.ktor.server.netty.Netty
import java.util.concurrent.TimeUnit
import no.nav.sokos.mikrofrontendapi.config.PropertiesConfig
import no.nav.sokos.mikrofrontendapi.config.commonConfig
import no.nav.sokos.mikrofrontendapi.config.configureRouting
import no.nav.sokos.mikrofrontendapi.config.configureSecurity
import no.nav.sokos.mikrofrontendapi.metrics.appStateReadyFalse
import no.nav.sokos.mikrofrontendapi.metrics.appStateRunningFalse
import kotlin.properties.Delegates

val appConfig = PropertiesConfig.Configuration()
const val SECURE_LOGGER_NAME = "secureLogger"

fun main() {
    val applicationState = ApplicationState()
    HttpServer(applicationState, appConfig).start()
}
class HttpServer(
    private val applicationState: ApplicationState,
    private val applicationConfiguration: PropertiesConfig.Configuration,
    port: Int = 8080,
) {
    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            this.stop()
        })
    }

    private val embeddedServer = embeddedServer(Netty, port, module = {
        applicationModule(applicationConfiguration, applicationState)
    })

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

fun Application.applicationModule(
    applicationConfiguration: PropertiesConfig.Configuration,
    applicationState: ApplicationState
) {
    commonConfig()
    configureSecurity(applicationConfiguration.azureAdConfig, applicationConfiguration.useAuthentication)
    configureRouting(applicationState, applicationConfiguration.useAuthentication)
}
