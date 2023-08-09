package no.nav.sokos.mikrofrontendapi.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson
import no.nav.sokos.mikrofrontendapi.config.customConfig
import org.apache.http.impl.conn.SystemDefaultRoutePlanner
import java.net.ProxySelector


val jsonMapper: ObjectMapper = jacksonObjectMapper().apply { customConfig() }

val httpClient = HttpClient(Apache) {
    expectSuccess = false
    install(ContentNegotiation) {
        jackson {
            customConfig()
        }
    }
    install(HttpRequestRetry) {
        retryOnServerErrors(maxRetries = 3)
        delayMillis { retry -> retry * 3000L }
    }
    engine {
        customizeClient {
            setRoutePlanner(SystemDefaultRoutePlanner(ProxySelector.getDefault()))
        }
    }
}