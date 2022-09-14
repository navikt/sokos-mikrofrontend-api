package no.nav.sokos.oppdragproxy

import no.nav.sokos.oppdragproxy.config.HttpServerConfig
import no.nav.sokos.oppdragproxy.util.ApplicationState

fun main() {
    val applicationState = ApplicationState()
    val httpServer = HttpServerConfig(
        applicationState
    )

    applicationState.ready = true

    Runtime.getRuntime().addShutdownHook(
        Thread {
            applicationState.ready = false
            httpServer.stop()
        }
    )
    httpServer.start()
}
