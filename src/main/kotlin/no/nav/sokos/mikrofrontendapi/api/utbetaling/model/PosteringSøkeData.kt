package no.nav.sokos.mikrofrontendapi.api.utbetaling.model

import no.nav.sokos.mikrofrontendapi.util.jsonMapper
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Periode
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Periodetype




data class PosteringSøkeData(
    val rettighetshaver: String?,
    val utbetalingsmottaker: String?,
    val periodetype: Periodetype,
    val periode: Periode,
    val kostnadsted: String?,
    val ansvarssted: String?,
    val posteringskontoFra: String?,
    val posteringskontoTil: String?
) {
    fun tilJson(): String {
        return jsonMapper.writeValueAsString(this)
    }

}