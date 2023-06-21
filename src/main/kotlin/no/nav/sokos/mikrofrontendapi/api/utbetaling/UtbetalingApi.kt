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
import java.math.BigDecimal
import mu.KotlinLogging
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.HentPosteringResponse
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.PosteringData
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.PosteringSumData
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

                    val posteringsresultat = hentPosteringer(posteringSøkeData)

                    if (posteringsresultat.isEmpty()) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        logger.info("Returnerer følgende data: $posteringsresultat")
                        val posteringSumData = posteringsresultat.summer()
                        val response = HentPosteringResponse(posteringsresultat, posteringSumData)
                        logger.info("Returnerer følgende response: ${response.tilJson()}")
                        call.respond(HttpStatusCode.OK, HentPosteringResponse(posteringsresultat, posteringSumData))
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

fun List<PosteringData>.summer(): List<PosteringSumData> {
    return groupBy(PosteringData::posteringskonto)
        .mapValues { entry ->
            PosteringSumData(
                entry.key,
                "todo",
                entry.value.map { pd -> pd.posteringsbeløp.beløp }.sumOf { it }
            )
        }.values.toList()


}


fun List<PosteringData>.tilCsv(): String {
    val kolonneHeader =
        buildString {
            append("beregningsId;")
            append("rettighetshaver ident;")
            append("rettighetshaver navn;")
            append("posteringsdato;")
            append("utbetalingsdato;")
            append("posteringsbeløp;")
            append("bilagsserie;")
            append("bilagsnummer;")
            append("posteringskonto;")
            append("posteringskonto navn;")
            append("fomDato;")
            append("tomDato;")
            append("ansvarssted;")
            append("kostnadssted;")
            append("behandlingsstatus kode;")
            append("behandlingsstatus navn;")
            append("utbetalingskontonummer;")
            append("utbetalingskontotype;")
            append("posteringsstatus kode;")
            append("posteringsstatus navn;")
            append("ytelsegrad;")
            append("ytelsestype;")
            append("forsystemPosteringsdato;")
            append("Utbetalingsmottaker ident;")
            append("utbetalingsmottaker navn;")
            append("utbetalingsnettobeløp")
        }

    return "$kolonneHeader\n" + map { it.tilCsv() }.joinToString("\n")
}

private fun PosteringData.tilCsv(): String {
    return buildString {
        append("$beregningsId;")
        append("\t${rettighetshaver.ident};")
        append("\t${rettighetshaver.navn};")
        append("${posteringsdato};")
        append("${utbetalingsdato ?: ""};")
        append("${posteringsbeløp.beløp.formater()};")
        append("\t${bilagsserie};")
        append("\t${bilagsnummer};")
        append("\t$posteringskonto;")
        append("TODO: Posteringskonto navn;")
        append("${ytelsesperiode?.fomDato ?: ""};")
        append("${ytelsesperiode?.tomDato ?: ""};")
        append("\t$ansvarssted;")
        append("\t$kostnadssted;")
        append("${behandlingsstatus.kode};")
        append("${behandlingsstatus.beskrivelse};")
        append("\t$utbetalingsKontonummer;")
        append("$utbetalingsKontotype;")
        append("${posteringsstatus.kode};")
        append("${posteringsstatus.beskrivelse};")
        append("${ytelsegrad ?: ""};")
        append("$ytelsestype;")
        append("${forsystemPosteringsdato ?: ""};")
        append("\t${utbetalingsmottaker.ident};")
        append("\t${utbetalingsmottaker.navn};")
        append(formaterDesimaltall(utbetalingsnettobeløp?.beløp))
    }
}

private fun BigDecimal.formater(): String {
    return this.toString()
        .replace(".", ",")
        .replace(" ", "")
}

private fun formaterDesimaltall(verdi: BigDecimal?): String {
    return verdi?.formater() ?: ""
}