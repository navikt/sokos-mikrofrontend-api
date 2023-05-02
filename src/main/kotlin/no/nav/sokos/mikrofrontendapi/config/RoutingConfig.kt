package no.nav.sokos.mikrofrontendapi.config

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing
import no.nav.sokos.mikrofrontendapi.ApplicationState
import no.nav.sokos.mikrofrontendapi.api.employeeApi
import no.nav.sokos.mikrofrontendapi.api.metricsApi
import no.nav.sokos.mikrofrontendapi.api.naisApi
import no.nav.sokos.mikrofrontendapi.api.utbetaling.ruteForUtbetaling


fun Application.configureRouting(
    applicationState: ApplicationState,
    useAuthentication: Boolean
) {
    routing {
        naisApi({ applicationState.initialized }, { applicationState.running })
        metricsApi()
        employeeApi(useAuthentication)
        ruteForUtbetaling(useAuthentication)
    }
}

fun Route.authenticate(useAuthentication: Boolean, authenticationProviderId: String? = null, block: Route.() -> Unit) {
    if (useAuthentication) authenticate(authenticationProviderId) { block() } else block()
}