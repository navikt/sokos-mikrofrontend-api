package no.nav.sokos.mikrofrontendapi.nom

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import mu.KotlinLogging
import no.nav.sokos.mikrofrontendapi.security.AzureAdClient


private val logger = KotlinLogging.logger {}

/**
 * Oppslag mot PIP-tjenesten til skjermingsløsningen for å sjekke om en person er skjermet.
 *
 * Skjermede personer er NAV-ansatte med familiemedlemmer.
 *
 * https://skjermede-personer-pip.intern.nav.no/swagger-ui/index.html#/skjerming-pip/isSkjermetPost
 *
 */
class SkjermetClientImpl(
    private val httpClient: HttpClient,
    private val skjermingUrl: String,
    private val skjermingClientId: String,
    private val accessTokenProvider: AzureAdClient?): SkjermetClient {

    override suspend fun erPersonSkjermet(personIdent: String) : Boolean {
        val token = accessTokenProvider?.getSystemToken(skjermingClientId)?.accessToken

        // TODO: Ta bort logging av token
        logger.info("Token: $token")
        val skjermetUrl = "${skjermingUrl}/skjermet"

        val response = httpClient.post(skjermetUrl) {
            method = HttpMethod.Post
            contentType(ContentType.Application.Json)
            setBody(SkjermetPersonRequest(personIdent))
            header("Authorization", "Bearer $token")
        }

        if (response.status != HttpStatusCode.OK) {
            throw RuntimeException("Kall mot Skjerming-tjeneste feilet: status = $response.status")
        }

        return response.bodyAsText() == "true"
    }
}

data class SkjermetPersonRequest(val personident: String)