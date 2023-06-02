package no.nav.sokos.mikrofrontendapi.api.utbetaling

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import mu.KotlinLogging
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.HentPosteringResponse
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.PosteringSøkeData
import no.nav.sokos.mikrofrontendapi.api.utbetaling.realistiskedata.CsvLeser
import no.nav.sokos.mikrofrontendapi.config.AUTHENTICATION_NAME
import no.nav.sokos.mikrofrontendapi.config.authenticate
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Periodetype

private val logger = KotlinLogging.logger {}

object UtbetalingApi {
    val posteringer = CsvLeser().lesFil("/mockposteringer.csv")
}

fun Routing.ruteForUtbetaling(useAuthentication: Boolean) {
    authenticate(useAuthentication, AUTHENTICATION_NAME) {
        route("/api/utbetaling") {

            post("/hentPostering") {
                val posteringSøkeData: PosteringSøkeData = call.receive()
                logger.info("Henter postering for ${posteringSøkeData.tilJson()}")

                val posteringskontoTil = posteringSøkeData.posteringskontoTil ?: posteringSøkeData.posteringskontoFra
                val posteringsresultat =
                    UtbetalingApi
                        .posteringer
                        .filter { posteringSøkeData.rettighetshaver?.equals(it.rettighetshaver.ident) ?: true }
                        .filter { posteringSøkeData.utbetalingsmottaker?.equals(it.rettighetshaver.ident) ?: true }
                        .filter { posteringSøkeData.ansvarssted?.equals(it.ansvarssted) ?: true }
                        .filter { posteringSøkeData.posteringskontoFra == null || posteringSøkeData.posteringskontoFra >= it.posteringskonto }
                        .filter { posteringskontoTil == null || posteringskontoTil <= it.posteringskonto }
                        .filter { it.ytelsesperiode == null || (posteringSøkeData.periodetype == Periodetype.YTELSESPERIODE &&  !it.ytelsesperiode.fomDato.isBefore(posteringSøkeData.periode.fomDato)) }
                        .filter { it.ytelsesperiode == null || (posteringSøkeData.periodetype == Periodetype.YTELSESPERIODE &&  !it.ytelsesperiode.tomDato.isAfter(posteringSøkeData.periode.tomDato)) }
                        .filter { it.utbetalingsdato == null || (posteringSøkeData.periodetype == Periodetype.UTBETALINGSPERIODE && !it.utbetalingsdato.isBefore(posteringSøkeData.periode.fomDato)) }
                        .filter { it.utbetalingsdato == null || (posteringSøkeData.periodetype == Periodetype.UTBETALINGSPERIODE && !it.utbetalingsdato.isAfter(posteringSøkeData.periode.tomDato)) }
                if (posteringsresultat.isEmpty()) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    logger.info("Returnerer følgende data: $posteringsresultat")
                    val response = HentPosteringResponse(posteringsresultat)
                    logger.info("Returnerer følgende response: ${response.tilJson()}")
                    call.respond(HttpStatusCode.OK, HentPosteringResponse(posteringsresultat))
                }

            }
        }
    }
}