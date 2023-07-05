package no.nav.sokos.mikrofrontendapi.api.utbetaling.model

import java.math.BigDecimal

data class PosteringSumData(val posteringskonto: Posteringskonto, val sumPosteringsbeløp: BigDecimal)

fun List<PosteringData>.summer(): List<PosteringSumData> {
    return groupBy(PosteringData::posteringskonto)
        .mapValues { entry ->
            PosteringSumData(
                entry.key,
                entry.value.map { pd -> pd.posteringsbeløp.beløp }.sumOf { it }
            )
        }.values.toList()
}