package no.nav.sokos.oppdragproxy.api

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.openapitools.client.models.Pet
import org.openapitools.client.models.User

fun Application.attestasjonApi() {
    routing {
        route("api/attestasjon") {
            get("hent-attestasjon") {
                val nav1 = User(1L, "navusername", "navfirstname", "navlastname", "nav@email.com", "navpassword", "navphone", 1)
                val nav2 = User(2L, "navusername2", "navfirstname2", "navlastname2", "nav@email.com2", "navpassword2", "navphone2", 2)
                call.respond(listOf(nav1, nav2))
            }
        }
    }
}