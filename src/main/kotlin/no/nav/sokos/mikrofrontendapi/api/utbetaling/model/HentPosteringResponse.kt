package no.nav.sokos.mikrofrontendapi.api.utbetaling.model

import no.nav.sokos.mikrofrontendapi.util.jsonMapper

data class HentPosteringResponse(val utbetalinger: List<PosteringData>) {

    fun tilJson(): String {
        return jsonMapper.writeValueAsString(this)
    }
}
