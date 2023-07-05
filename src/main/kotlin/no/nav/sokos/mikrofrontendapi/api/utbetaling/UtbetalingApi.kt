package no.nav.sokos.mikrofrontendapi.api.utbetaling

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.net.URL
import mu.KotlinLogging
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.HentPosteringResponse
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.PosteringData
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.PosteringSøkeData
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.summer
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.tilCsv
import no.nav.sokos.mikrofrontendapi.api.utbetaling.realistiskedata.CsvLeser
import no.nav.sokos.mikrofrontendapi.appConfig
import no.nav.sokos.mikrofrontendapi.config.AUTHENTICATION_NAME
import no.nav.sokos.mikrofrontendapi.config.authenticate
import no.nav.sokos.mikrofrontendapi.pdl.PdlService
import no.nav.sokos.mikrofrontendapi.security.AccessTokenProvider
import no.nav.sokos.mikrofrontendapi.util.httpClient
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Aktoertype
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Periodetype

private val logger = KotlinLogging.logger {}

object UtbetalingApi {
    private val posteringer = CsvLeser().lesFil("/mockposteringer.csv")
    private val accessTokenProvider =
        if (appConfig.azureAdProviderConfig.useSecurity) AccessTokenProvider(
            appConfig.azureAdProviderConfig,
            httpClient
        ) else null

    private val graphQlClient = GraphQLKtorClient( URL(appConfig.pdlUrl) )
    private val pdlService = PdlService(graphQlClient, appConfig.pdlUrl, accessTokenProvider)

    fun Routing.ruteForUtbetaling(useAuthentication: Boolean) {
        authenticate(useAuthentication, AUTHENTICATION_NAME) {
            route("/api/utbetaling") {

                post("/hentPostering") {
                    val posteringSøkeData: PosteringSøkeData = call.receive()
                    logger.info("Henter postering for ${posteringSøkeData.tilJson()}")

                    val posteringer = posteringerMedNavnFraPdl(posteringSøkeData)

                    if (posteringer.isEmpty()) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        logger.info("Returnerer følgende data: $posteringer")
                        val posteringSumData = posteringer.summer()
                        val response = HentPosteringResponse(posteringer, posteringSumData)
                        logger.info("Returnerer følgende response: ${response.tilJson()}")
                        call.respond(HttpStatusCode.OK, HentPosteringResponse(posteringer, posteringSumData))
                    }
                }

                post("/tilCsv") {
                    val posteringSøkeData: PosteringSøkeData = call.receive()
                    logger.info("Henter postering for ${posteringSøkeData.tilJson()}")

                    val posteringsresultat = posteringerMedNavnFraPdl(posteringSøkeData)

                    if (posteringsresultat.isEmpty()) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        val csv = posteringsresultat.tilCsv()
                        logger.info("Returnerer følgende CSV: $csv")
                        call.respondText(csv, ContentType.Text.CSV, HttpStatusCode.OK)
                    }
                }

            }
        }
    }

    private fun posteringerMedNavnFraPdl(posteringSøkeData: PosteringSøkeData): List<PosteringData> {
        val posteringer = hentPosteringer(posteringSøkeData)

        if (posteringer.isEmpty()) {
            return emptyList()
        }

        val navnRettighetshaver: String? = posteringSøkeData.rettighetshaver?.let {
            val navnFraPdl = pdlService.hentPerson(it)?.navn
            navnFraPdl ?: posteringer.first().rettighetshaver.navn
        }

        val navnMottaker: String? = posteringSøkeData.utbetalingsmottaker?.let {
            posteringer.first().utbetalingsmottaker.navn
        }

        val resultat = posteringer.map {
            it.copy(
                rettighetshaver = it.rettighetshaver.copy(navn = navnRettighetshaver),
                utbetalingsmottaker = it.utbetalingsmottaker.copy(
                    navn = if (it.utbetalingsmottaker.aktoertype == Aktoertype.ORGANISASJON) it.utbetalingsmottaker.navn else navnMottaker
                )
            )
        }

        return resultat
    }

    private fun hentPosteringer(posteringSøkeData: PosteringSøkeData): List<PosteringData> {
        val posteringskontoTil = posteringSøkeData.posteringskontoTil ?: posteringSøkeData.posteringskontoFra
        return posteringer
            .filter { posteringSøkeData.rettighetshaver?.equals(it.rettighetshaver.ident) ?: true }
            .filter { posteringSøkeData.utbetalingsmottaker?.equals(it.rettighetshaver.ident) ?: true }
            .filter { posteringSøkeData.ansvarssted?.equals(it.ansvarssted) ?: true }
            .filter { posteringSøkeData.kostnadssted?.equals(it.kostnadssted) ?: true }
            .filter { posteringSøkeData.posteringskontoFra == null || it.posteringskonto.kontonummer >= posteringSøkeData.posteringskontoFra }
            .filter { posteringskontoTil == null || it.posteringskonto.kontonummer <= posteringskontoTil }
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


