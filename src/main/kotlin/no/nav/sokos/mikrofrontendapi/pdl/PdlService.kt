package no.nav.sokos.mikrofrontendapi.pdl

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.types.GraphQLClientError
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import io.ktor.client.request.header
import io.ktor.client.request.url
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import no.nav.pdl.HentPerson
import no.nav.pdl.hentperson.Navn
import no.nav.sokos.mikrofrontendapi.security.AccessTokenProvider

private val logger = KotlinLogging.logger {}

class PdlService (
    private val graphQlClient: GraphQLKtorClient,
    private val pdlUrl: String,
    private val accessTokenProvider: AccessTokenProvider
) {

    fun hentPerson(ident: String): List<Navn> {
        val request = HentPerson(HentPerson.Variables(ident = ident))

        val result = runBlocking {
            val accessToken = accessTokenProvider.hentAccessToken()
            graphQlClient.execute(request) {
                url(pdlUrl)
                header("Authorization", "Bearer $accessToken")
            }
        }
        return result.errors?.let { errors ->
            if (errors.isEmpty()) {
                hentNavn(result)
            } else {
                handleErrors(errors)
            }
        } ?: hentNavn(result)

    }

    private fun hentNavn(result: GraphQLClientResponse<HentPerson.Result>) =
        result.data?.hentPerson?.navn ?: emptyList()

    private fun handleErrors(errors: List<GraphQLClientError>): List<Navn> =
        if (errors.mapNotNull { it.extensions }.any { it["code"] == "not_found" }) emptyList()
        else throw Exception("feil med henting av identer").also { logger.error { "Feil i GraphQL-responsen: $errors" } }

}