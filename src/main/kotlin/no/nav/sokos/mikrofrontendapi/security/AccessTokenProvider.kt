package no.nav.sokos.mikrofrontendapi.security

import com.fasterxml.jackson.annotation.JsonAlias
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging
import no.nav.sokos.mikrofrontendapi.config.PropertiesConfig
import java.time.Instant

private val logger = KotlinLogging.logger {}

class AccessTokenProvider(
    private val azureAd: PropertiesConfig.AzureAdProviderConfig,
    private val client: HttpClient,
    private val aadAccessTokenUrl: String = "https://login.microsoftonline.com/${azureAd.tenant}/oauth2/v2.0/token",
) {
    private val mutex = Mutex()

    @Volatile
    private var token: AccessToken = runBlocking { AccessToken(hentAccessTokenFraClient()) }

    suspend fun hentAccessToken(): String {
        val omToMinutter = Instant.now().plusSeconds(120L)
        return mutex.withLock {
            when {
                token.expiresAt.isBefore(omToMinutter) -> {
                    logger.info("henter ny token")
                    token = AccessToken(hentAccessTokenFraClient())
                    token.accessToken
                }
                else -> token.accessToken
            }
        }
    }

    private suspend fun hentAccessTokenFraClient(): AzureAccessToken {
        val response = client.post(aadAccessTokenUrl) {
            accept(ContentType.Application.Json)
            method = HttpMethod.Post
            setBody(FormDataContent(Parameters.build {
                append("tenant", azureAd.tenant)
                append("client_id", azureAd.clientId)
                append("scope", "api://${azureAd.pdlClientId}/.default")
                append("client_secret", azureAd.clientSecret)
                append("grant_type", "client_credentials")
            }))
        }

        if (response.status != HttpStatusCode.OK) {
            val feilmelding = "Acesstoken provider fikk ikke token fra Azure. Fikk følgende statuskode: ${response.status}"
            logger.error { feilmelding }
            throw RuntimeException(feilmelding)
        } else {
            return response.body()
        }
    }
}

private data class AzureAccessToken(
    @JsonAlias("access_token")
    val accessToken: String,
    @JsonAlias("expires_in")
    val expiresIn: Long,
)

private data class AccessToken(
    val accessToken: String,
    val expiresAt: Instant,
) {
    constructor(azureAccessToken: AzureAccessToken) : this(
        accessToken = azureAccessToken.accessToken,
        expiresAt = Instant.now().plusSeconds(azureAccessToken.expiresIn)
    )
}