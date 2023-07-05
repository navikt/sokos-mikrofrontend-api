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
                // Retrieve the token from the request headers
                val authorizationHeader = call.request.headers["Authorization"]
                val token = authorizationHeader?.removePrefix("Bearer ")
                println("TOKEN::::::::::::::: $token")
                val employee1 = Employee(
                    1,
                    "Ola Nordmann",
                    "Prosjektleder"
                )
                val employee2 = Employee(
                    2,
                    "Kari Nordmann",
                    "Tech lead"
                )
                call.respond(listOf(employee1, employee2))
            }
        }
    }
}