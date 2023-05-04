package no.nav.sokos.mikrofrontendapi.api.utbetaling.model

import no.nav.sokos.mikrofrontendapi.util.JsonMapper

data class HentPosteringResponse(val utbetalinger: List<PosteringData>) {

    fun tilJson(): String {
        return JsonMapper.objectMapper.writeValueAsString(this)
    }
}
