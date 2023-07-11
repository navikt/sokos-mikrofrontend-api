package no.nav.sokos.mikrofrontendapi.pdl

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.types.GraphQLClientError
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import io.ktor.client.request.header
import io.ktor.client.request.url
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import no.nav.pdl.HentPerson
import no.nav.pdl.hentperson.Person
import no.nav.sokos.mikrofrontendapi.domene.PersonData
import no.nav.sokos.mikrofrontendapi.security.AzureAdClient

private val logger = KotlinLogging.logger {}

class PdlService(
    private val graphQlClient: GraphQLKtorClient,
    private val pdlUrl: String,
    private val pdlClientId: String,
    private val accessTokenProvider: AzureAdClient?
) {
    fun hentPerson(ident: String): PersonData? {
        val request = HentPerson(HentPerson.Variables(ident = ident))

        val resultat = runBlocking {
            val accessToken = accessTokenProvider?.getSystemToken(pdlClientId)?.accessToken
            graphQlClient.execute(request) {
                url(pdlUrl)
                header("Authorization", "Bearer $accessToken")
            }
        }
        return resultat.errors?.let { errors ->
            if (errors.isEmpty()) {
                hentPerson(resultat)?.let { person: Person -> PersonData.fra(ident, person)  }
            } else {
                handleErrors(errors)
            }
        } ?: hentPerson(resultat)?.let { person: Person -> PersonData.fra(ident, person)  }

    }

    private fun hentPerson(result: GraphQLClientResponse<HentPerson.Result>): Person? {
        return result.data?.hentPerson
    }


    private fun handleErrors(errors: List<GraphQLClientError>): PersonData? =
        if (errors.mapNotNull { it.extensions }.any { it["code"] == "not_found" }) null
        else throw Exception("Feil med henting av person fra PDL").also { logger.error { "Feil i GraphQL-responsen: $errors" } }

}


