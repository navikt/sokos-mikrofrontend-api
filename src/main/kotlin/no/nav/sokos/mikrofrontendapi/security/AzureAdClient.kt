package no.nav.sokos.mikrofrontendapi.security

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.accept
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.Parameters
import mu.KotlinLogging
import no.nav.sokos.mikrofrontendapi.config.PropertiesConfig

private val logger = KotlinLogging.logger {}

class AzureAdClient(
    private val azureAd: PropertiesConfig.AzureAdProviderConfig,
    private val httpClient: HttpClient,
    private val azureOpenidConfigTokenEndpoint: String = "https://login.microsoftonline.com/${azureAd.tenant}/oauth2/v2.0/token",
) {

    suspend fun getOnBehalfOfToken(
        scopeClientId: String,
        token: String,
    ): AzureAdToken? {
        val scope = "api://$scopeClientId/.default"
        val azureAdTokenResponse = getAccessToken(
            Parameters.build {
                append("client_id", azureAd.clientId)
                append("client_secret", azureAd.clientSecret)
                append("client_assertion_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                append("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                append("assertion", token)
                append("scope", scope)
                append("requested_token_use", "on_behalf_of")
            }
        )

        return azureAdTokenResponse?.let {
            val azureAdToken = it.toAzureAdToken()
            azureAdToken
        }
    }

    suspend fun getSystemToken(scopeClientId: String): AzureAdToken? {
        val azureAdTokenResponse = getAccessToken(
            Parameters.build {
                append("tenant", azureAd.tenant)
                append("client_id", azureAd.clientId)
                append("client_secret", azureAd.clientSecret)
                append("grant_type", "client_credentials")
                append("scope", "api://$scopeClientId/.default")
            }
        )
        return azureAdTokenResponse?.let { token ->
            val azureAdToken = token.toAzureAdToken()
            azureAdToken
        }
    }

    private suspend fun getAccessToken(
        formParameters: Parameters,
    ): AzureAdTokenResponse? {
        return try {
            val response = httpClient.post(azureOpenidConfigTokenEndpoint) {
                accept(ContentType.Application.Json)
                method = HttpMethod.Post
                setBody(FormDataContent(formParameters))
            }
            response.body<AzureAdTokenResponse>()
        } catch (e: ClientRequestException) {
            handleUnexpectedResponseException(e)
            null
        } catch (e: ServerResponseException) {
            handleUnexpectedResponseException(e)
            null
        }
    }

    private fun handleUnexpectedResponseException(
        responseException: ResponseException,
    ) {
        logger.error(
            "Error while requesting AzureAdAccessToken with statusCode=${responseException.response.status.value}",
            responseException
        )
    }


}