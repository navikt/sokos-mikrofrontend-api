package no.nav.sokos.oppdragproxy.config

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing
import no.nav.sokos.oppdragproxy.ApplicationState
import no.nav.sokos.oppdragproxy.api.employeeApi
import no.nav.sokos.oppdragproxy.api.metricsApi
import no.nav.sokos.oppdragproxy.api.naisApi


fun Application.configureRouting(
    applicationState: ApplicationState,
    useAuthentication: Boolean
) {
    routing {
        naisApi({ applicationState.initialized }, { applicationState.running })
        metricsApi()
        employeeApi(useAuthentication)
    }
}

fun Route.authenticate(useAuthentication: Boolean, authenticationProviderId: String? = null, block: Route.() -> Unit) {
    if (useAuthentication) authenticate(authenticationProviderId) { block() } else block()
}