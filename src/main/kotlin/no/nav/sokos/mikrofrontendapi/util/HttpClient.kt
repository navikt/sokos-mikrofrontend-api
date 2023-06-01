package no.nav.sokos.mikrofrontendapi.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*
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

    engine {
        customizeClient {
            setRoutePlanner(SystemDefaultRoutePlanner(ProxySelector.getDefault()))
        }
    }
}