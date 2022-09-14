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
                val nav = User(1L, "navusername", "navfirstname", "navlastname", "nav@email.com", "navpassword", "navphone", 1)
                call.respond(nav)
            }
        }
    }
}