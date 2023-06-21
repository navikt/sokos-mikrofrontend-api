package no.nav.sokos.mikrofrontendapi.api.utbetaling.model

import no.nav.sokos.mikrofrontendapi.util.jsonMapper

data class HentPosteringResponse(val utbetalinger: List<PosteringData>, val posteringSumData: List<PosteringSumData>) {

    fun tilJson(): String {
        return jsonMapper.writeValueAsString(this)
    }
}
