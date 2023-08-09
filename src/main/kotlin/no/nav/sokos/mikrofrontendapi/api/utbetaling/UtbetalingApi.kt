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
import no.nav.sokos.mikrofrontendapi.SECURE_LOGGER_NAME
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.HentPosteringResponse
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.PosteringSøkeData
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.summer
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.tilCsv
import no.nav.sokos.mikrofrontendapi.api.utbetaling.realistiskedata.CsvLeser
import no.nav.sokos.mikrofrontendapi.api.utbetaling.realistiskedata.PosteringURServiceMockImpl
import no.nav.sokos.mikrofrontendapi.appConfig
import no.nav.sokos.mikrofrontendapi.config.AUTHENTICATION_NAME
import no.nav.sokos.mikrofrontendapi.config.authenticate
import no.nav.sokos.mikrofrontendapi.nom.SkjermetClientImpl
import no.nav.sokos.mikrofrontendapi.pdl.PdlServiceImpl
import no.nav.sokos.mikrofrontendapi.personvern.PersonvernPdlService
import no.nav.sokos.mikrofrontendapi.personvern.PersonvernService
import no.nav.sokos.mikrofrontendapi.personvern.SkjermetServiceImpl
import no.nav.sokos.mikrofrontendapi.security.AzureAdClient
import no.nav.sokos.mikrofrontendapi.security.TilgangService
import no.nav.sokos.mikrofrontendapi.util.httpClient

private val logger = KotlinLogging.logger {}
private val secureLogger = KotlinLogging.logger(SECURE_LOGGER_NAME)

object UtbetalingApi {
    private val posteringURService = PosteringURServiceMockImpl(CsvLeser().lesFil("/mockposteringer.csv"))

    private val accessTokenProvider =
        if (appConfig.azureAdProviderConfig.useSecurity) AzureAdClient(
            appConfig.azureAdProviderConfig,
            httpClient
        ) else null

    private val graphQlClient = GraphQLKtorClient(URL(appConfig.pdlUrl))

    private val pdlService = PdlServiceImpl(
        graphQlClient = graphQlClient,
        pdlUrl = appConfig.pdlUrl,
        pdlClientId = appConfig.azureAdProviderConfig.pdlClientId,
        accessTokenProvider = accessTokenProvider
    )

    private val tilgangService = TilgangService(accessTokenProvider)
    private val personvernPdlService = PersonvernPdlService(pdlService)

    private val skjermetClient = SkjermetClientImpl(
        httpClient = httpClient,
        skjermingUrl = appConfig.skjermingUrl,
        skjermingClientId = appConfig.azureAdProviderConfig.skjermingClientId,
        accessTokenProvider = accessTokenProvider
    )

    private val skjermetService = SkjermetServiceImpl(skjermetClient)
    private val personvernService = PersonvernService(personvernPdlService, skjermetService)
    private val posteringService = PosteringService(posteringURService, pdlService, personvernService)


    fun Routing.ruteForUtbetaling(useAuthentication: Boolean) {
        authenticate(useAuthentication, AUTHENTICATION_NAME) {
            route("/api/utbetaling") {

                post("/hentPostering") {
                    val posteringSøkeData: PosteringSøkeData = call.receive()
                    logger.info("Henter postering")
                    secureLogger.info("Henter postering for ${posteringSøkeData.tilJson()}")
                    val saksbehandler = tilgangService.hentSaksbehandler(call)
                    val posteringer =  posteringService.hentPosteringer(posteringSøkeData, saksbehandler)

                    if (posteringer.isEmpty()) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        val posteringSumData = posteringer.summer()
                        val response = HentPosteringResponse(posteringer, posteringSumData)
                        secureLogger.info("Returnerer følgende response: ${response.tilJson()}")
                        call.respond(HttpStatusCode.OK, response)
                    }
                }

                post("/tilCsv") {
                    val posteringSøkeData: PosteringSøkeData = call.receive()
                    secureLogger.info("Henter postering for ${posteringSøkeData.tilJson()}")

                    val saksbehandler = tilgangService.hentSaksbehandler(call)
                    val posteringer =  posteringService.hentPosteringer(posteringSøkeData, saksbehandler)

                    if (posteringer.isEmpty()) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        val csv = posteringer.tilCsv()
                        secureLogger.info("Returnerer følgende CSV: $csv")
                        call.respondText(csv, ContentType.Text.CSV, HttpStatusCode.OK)
                    }
                }

            }
        }
    }
}


