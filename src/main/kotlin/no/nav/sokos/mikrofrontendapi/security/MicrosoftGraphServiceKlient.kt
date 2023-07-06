package no.nav.sokos.mikrofrontendapi.security

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.HttpStatusCode

class MicrosoftGraphServiceKlient(
    private val httpClient: HttpClient,
    private val accesTokenProvider: MicrosoftGraphAccesTokenProvider
) {
    private val msGraphUrl: String = "https://graph.microsoft.com/v1.0/me/memberOf"

    suspend fun hentRoller(ident: String, navCallId: String): List<Rolle> {

        httpClient.get {
            url(msGraphUrl)
            header("Authorization", "Bearer ${accesTokenProvider.token()}")
            header("Nav-Call-Id", navCallId)
        }.let { respons ->
            if(respons.status == HttpStatusCode.OK){
                val body: MedlemAv = respons.body<MedlemAv>()
            }
        }

        return emptyList()
    }
}
@JsonIgnoreProperties(ignoreUnknown = true)
data class MedlemAv(
    @JsonProperty("value")
    val rolles: List<Rolle> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Rolle(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("mailNickname")
    val name: String
)