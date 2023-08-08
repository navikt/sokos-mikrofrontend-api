package no.nav.sokos.mikrofrontendapi.security


import io.ktor.server.application.ApplicationCall
import mu.KotlinLogging
import no.nav.sokos.mikrofrontendapi.util.httpClient

private val logger = KotlinLogging.logger {}

class TilgangService(private val accessTokenProvider: AzureAdClient?) {
    private val graphKlient = MicrosoftGraphServiceKlient(httpClient)

    suspend fun hentSaksbehandler(call: ApplicationCall): Saksbehandler {
        val oboToken = call.request.headers["Authorization"]?.removePrefix("Bearer ")
            ?: throw IllegalStateException("Greier ikke hente token fra request header")
        println("oboToken::::::: $oboToken")
        val navIdent = getNAVIdentFromToken(oboToken)

        return Saksbehandler(navIdent, hentBrukerRoller(oboToken).map{it.name})
    }

    private suspend fun hentBrukerRoller(oboToken: String): List<Rolle> {
        val onBehalfOfToken = accessTokenProvider?.getOnBehalfOfTokenForMsGraph(oboToken)

        val roller = graphKlient.hentRoller("finn_riktig_hash_her", onBehalfOfToken?.accessToken ?: oboToken)
        logger.info("Brukerens roller: $roller")

        return roller
    }


}

