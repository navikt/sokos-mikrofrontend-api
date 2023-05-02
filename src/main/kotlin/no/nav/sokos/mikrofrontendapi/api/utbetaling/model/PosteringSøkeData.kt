package no.nav.sokos.mikrofrontendapi.api.utbetaling.model

import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Periode
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Periodetype

data class PosteringSøkeData(
    val mottaker: String?,
    val rettighetshaver: String?,
    val periodetype: Periodetype?,
    val periode: Periode?,
    val ansvarssted: String?,
    val kostnadssted: String?,
    val kontonummerFra: String?,
    val kontonummerTil: String?
)