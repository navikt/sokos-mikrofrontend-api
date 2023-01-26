package no.nav.sokos.oppdragproxy.api

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.openapitools.client.models.Attestasjon
import org.openapitools.client.models.ModelApiResponse

fun Application.attestasjonApi() {

    routing {
        route("api") {
            get("attestasjon") {
                val attestasjon1 = Attestasjon(1, "SUUFORE", "29073560-34e9-11ed-a261-0242ac120002", 20216, "MND", "01.10.2021 - 30.04.2022")
                val attestasjon2 = Attestasjon(2, "SUUFORE", "29073560-84e9-11ed-b561-0242ac120002", 21181, "MND", "01.07.2022 - 30.09.2022")
                val apiResponse = ModelApiResponse(28098213122, "Testområde", 10002028, listOf(attestasjon1, attestasjon2))
                call.respond(apiResponse)
            }
        }
    }
}