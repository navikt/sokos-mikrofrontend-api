package no.nav.sokos.mikrofrontendapi.api.utbetaling.model

import java.math.BigDecimal

data class PosteringSumData(val posteringskonto: Posteringskonto, val posteringskontoNavn: String, val sumPosteringsbeløp: BigDecimal)
