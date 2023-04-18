package no.nav.sokos.oppdragproxy.api

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import no.nav.sokos.oppdragproxy.models.Employee


fun Application.employeeApi() {

    routing {
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