package no.nav.sokos.mikrofrontendapi.api

import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import no.nav.sokos.mikrofrontendapi.models.Employee

fun Route.employeeApi() {
    route("/api/v1") {
            get("employee") {
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