package no.nav.sokos.mikrofrontendapi.security

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.HttpStatusCode
import mu.KotlinLogging
import java.net.URLEncoder

private val logger = KotlinLogging.logger {}

class MicrosoftGraphServiceKlient(
    private val httpClient: HttpClient,
) {
    private val memberOfApiQuery: String = "\$count=true&\$orderby=displayName&\$filter=startswith(displayName, '0000-GA-okonomi')";
    private val encodedQuery: String = URLEncoder.encode(memberOfApiQuery, "UTF-8")
    private val msGraphUrl: String = "https://graph.microsoft.com/v1.0/me/memberOf/?$encodedQuery"


    suspend fun hentRoller(navCallId: String, token: String): List<Rolle> {
        httpClient.get {
            url(msGraphUrl)
            header("Authorization", token)
            header("ConsistencyLevel", "eventual")
            header("Nav-Call-Id", navCallId)
        }.let { respons ->
            logger.info { "Respons fra MSGraph: $respons" }
            if(respons.status == HttpStatusCode.OK){
                val body: MedlemAv = respons.body<MedlemAv>()
                return body.rolles
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