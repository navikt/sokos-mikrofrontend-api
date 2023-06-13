package no.nav.sokos.mikrofrontendapi.api.utbetaling

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import mu.KotlinLogging
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.HentPosteringResponse
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.PosteringData
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.PosteringSøkeData
import no.nav.sokos.mikrofrontendapi.api.utbetaling.realistiskedata.CsvLeser
import no.nav.sokos.mikrofrontendapi.config.AUTHENTICATION_NAME
import no.nav.sokos.mikrofrontendapi.config.authenticate
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Periodetype

private val logger = KotlinLogging.logger {}

object UtbetalingApi {
    private val posteringer = CsvLeser().lesFil("/mockposteringer.csv")

    fun Routing.ruteForUtbetaling(useAuthentication: Boolean) {
        authenticate(useAuthentication, AUTHENTICATION_NAME) {
            route("/api/utbetaling") {

                post("/hentPostering") {
                    val posteringSøkeData: PosteringSøkeData = call.receive()
                    logger.info("Henter postering for ${posteringSøkeData.tilJson()}")

                    val posteringsresultat = UtbetalingApi.hentPosteringer(posteringSøkeData)

                    if (posteringsresultat.isEmpty()) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        logger.info("Returnerer følgende data: $posteringsresultat")
                        val response = HentPosteringResponse(posteringsresultat)
                        logger.info("Returnerer følgende response: ${response.tilJson()}")
                        call.respond(HttpStatusCode.OK, HentPosteringResponse(posteringsresultat))
                    }
                }

                post("/tilCsv") {
                    val posteringSøkeData: PosteringSøkeData = call.receive()
                    logger.info("Henter postering for ${posteringSøkeData.tilJson()}")

                    val posteringsresultat = hentPosteringer(posteringSøkeData)

                    if (posteringsresultat.isEmpty()) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        val csv = posteringsresultat.tilCsv()

                        logger.info("Returnerer følgende CSV: $csv")
                        call.respondText(posteringsresultat.tilCsv(), ContentType.Text.CSV, HttpStatusCode.OK)
                    }
                }

            }
        }
    }

    private fun hentPosteringer(posteringSøkeData: PosteringSøkeData): List<PosteringData> {
        val posteringskontoTil = posteringSøkeData.posteringskontoTil ?: posteringSøkeData.posteringskontoFra
        return posteringer
            .filter { posteringSøkeData.rettighetshaver?.equals(it.rettighetshaver.ident) ?: true }
            .filter { posteringSøkeData.utbetalingsmottaker?.equals(it.rettighetshaver.ident) ?: true }
            .filter { posteringSøkeData.ansvarssted?.equals(it.ansvarssted) ?: true }
            .filter { posteringSøkeData.kostnadssted?.equals(it.kostnadssted) ?: true }
            .filter { posteringSøkeData.posteringskontoFra == null || it.posteringskonto >= posteringSøkeData.posteringskontoFra }
            .filter { posteringskontoTil == null || it.posteringskonto <= posteringskontoTil }
            .filter {
                it.ytelsesperiode == null || posteringSøkeData.periodetype != Periodetype.YTELSESPERIODE || !it.ytelsesperiode.fomDato.isBefore(
                    posteringSøkeData.periode.fomDato
                )
            }
            .filter {
                it.ytelsesperiode == null || posteringSøkeData.periodetype != Periodetype.YTELSESPERIODE || !it.ytelsesperiode.tomDato.isAfter(
                    posteringSøkeData.periode.tomDato
                )
            }
            .filter {
                it.utbetalingsdato == null || posteringSøkeData.periodetype != Periodetype.UTBETALINGSPERIODE || !it.utbetalingsdato.isBefore(
                    posteringSøkeData.periode.fomDato
                )
            }
            .filter {
                it.utbetalingsdato == null || posteringSøkeData.periodetype != Periodetype.UTBETALINGSPERIODE || !it.utbetalingsdato.isAfter(
                    posteringSøkeData.periode.tomDato
                )
            }
    }

}


fun List<PosteringData>.tilCsv(): String {
    val kolonneHeader =
        buildString {
            append("beregningsId;")
            append("rettighetshaver;")
            append("posteringsdato;")
            append("utbetalingsdato;")
            append("posteringsbeløp;")
            append("bilagsserie;")
            append("bilagsnummer;")
            append("posteringskonto;")
            append("fomDato;")
            append("tomDato;")
            append("ansvarssted;")
            append("kostnadssted;")
            append("behandlingsstatus;");
            append("utbetalingskontonummer;")
            append("utbetalingskontotype;")
            append("posteringsstatus;")
            append("ytelsegrad;")
            append("ytelsestype;")
            append("forsystemPosteringsdato;")
            append("Utbetalingsmottaker;")
            append("utbetalingsnettobeløp")
        }

    return "$kolonneHeader\n" + map { it.tilCsv() }.joinToString("\n")
}

private fun PosteringData.tilCsv(): String {
    val strBuilder = StringBuilder()
    strBuilder.append("$beregningsId;")
    strBuilder.append("${rettighetshaver.ident};")
    strBuilder.append("${posteringsdato};")
    strBuilder.append("${utbetalingsdato};")
    strBuilder.append("${posteringsbeløp.beløp};")
    strBuilder.append("${bilagsserie};")
    strBuilder.append("${bilagsnummer};")
    strBuilder.append("$posteringskonto;")
    strBuilder.append("${ytelsesperiode?.fomDato};")
    strBuilder.append("${ytelsesperiode?.tomDato};")
    strBuilder.append("$ansvarssted;")
    strBuilder.append("$kostnadssted;")
    strBuilder.append("${behandlingsstatus.kode};")
    strBuilder.append("$utbetalingsKontonummer;")
    strBuilder.append("$utbetalingsKontotype;")
    strBuilder.append("${posteringsstatus.kode};")
    strBuilder.append("$ytelsegrad;")
    strBuilder.append("$ytelsestype;")
    strBuilder.append("$forsystemPosteringsdato;")
    strBuilder.append("${utbetalingsmottaker.ident};")
    strBuilder.append("${utbetalingsnettobeløp?.beløp}")

    return strBuilder.toString()
}