package no.nav.sokos.mikrofrontendapi.api

import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import no.nav.sokos.mikrofrontendapi.config.AUTHENTICATION_NAME
import no.nav.sokos.mikrofrontendapi.config.authenticate
import no.nav.sokos.mikrofrontendapi.models.Employee


fun Routing.employeeApi(
    useAuthentication: Boolean
) {

    authenticate(useAuthentication, AUTHENTICATION_NAME) {
        route("api") {
            get("employee") {
                val employee1 = Employee(
                    1,
                    "Ola Nordmann",
                    "Systemutvikler"
                )
                val employee2 = Employee(
                    2,
                    "Kari Nordmann",
                    "Prosjektleder"
                )
                call.respond(listOf(employee1, employee2))
            }
        }
    }
}