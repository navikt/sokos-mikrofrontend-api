package no.nav.sokos.mikrofrontendapi.api.utbetaling

import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.PosteringData
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.PosteringSøkeData

interface PosteringUrService {
    fun hentPosteringer(posteringSøkeData: PosteringSøkeData): List<PosteringData>
}