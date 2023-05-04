package no.nav.sokos.mikrofrontendapi.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object JsonMapper {
    val objectMapper = jacksonObjectMapper()
}