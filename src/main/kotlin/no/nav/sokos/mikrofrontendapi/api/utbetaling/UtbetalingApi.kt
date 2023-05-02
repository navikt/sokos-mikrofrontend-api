package no.nav.sokos.mikrofrontendapi.api.utbetaling

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.math.BigDecimal
import java.time.LocalDate
import mu.KotlinLogging
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.Aktoer
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.HentPosteringResponse
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.PosteringData
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.PosteringSøkeData
import no.nav.sokos.mikrofrontendapi.config.AUTHENTICATION_NAME
import no.nav.sokos.mikrofrontendapi.config.authenticate
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Aktoertype
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Periode

private val logger = KotlinLogging.logger {}

object UtbetalingApi {
    val posteringer = listOf(
        PosteringData(
            rettighetshaver = Aktoer(ident = "012345678901", navn = "Testperson 1", aktoertype = Aktoertype.PERSON),
            posteringsdato = LocalDate.of(2023, 1, 3),
            utbetalingsdato = LocalDate.of(2023, 1, 4),
            utbetalingNettobeloep = BigDecimal("800.00"),
            bilagsnummer = "726909203",
            regnskapskonto = "1234567",
            ytelsesperiode = Periode(LocalDate.of(2022, 12, 12), LocalDate.of(2022, 12, 19)),
            ansvarssted = "1218",
            kostnadssted = "9710"
        ),
        PosteringData(
            rettighetshaver = Aktoer(ident = "012345678901", navn = "Testperson 1", aktoertype = Aktoertype.PERSON),
            posteringsdato = LocalDate.of(2023, 1, 15),
            utbetalingsdato = LocalDate.of(2023, 1, 20),
            utbetalingNettobeloep = BigDecimal("800.00"),
            bilagsnummer = "726909203",
            regnskapskonto = "1234567",
            ytelsesperiode = Periode(LocalDate.of(2022, 12, 19), LocalDate.of(2022, 12, 25)),
            ansvarssted = "1218",
            kostnadssted = "9710"
        ),
    )

}

fun Routing.ruteForUtbetaling(useAuthentication: Boolean) {
    authenticate(useAuthentication, AUTHENTICATION_NAME) {
        route("/api/utbetaling") {

            post("/hentPostering") {
                val posteringSøkeData: PosteringSøkeData = call.receive()
                logger.info("Henter postering for $posteringSøkeData")

                val posteringsresultat =
                    UtbetalingApi
                        .posteringer
                        .filter { it.rettighetshaver.ident == posteringSøkeData.rettighetshaver }

                if (posteringsresultat.isEmpty()) {
                    call.respond(HttpStatusCode.NoContent)
                }

                logger.info("Returnerer følgende data: $posteringsresultat")
                call.respond(HttpStatusCode.OK, HentPosteringResponse(posteringsresultat))
            }
        }
    }
}