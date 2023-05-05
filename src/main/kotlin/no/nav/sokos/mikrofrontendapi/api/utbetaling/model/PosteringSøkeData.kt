package no.nav.sokos.mikrofrontendapi.api.utbetaling.model

import no.nav.sokos.mikrofrontendapi.util.jsonMapper
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Periode
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Periodetype

data class PosteringSøkeData(
    val rettighetshaver: String?,
    val mottaker: String?,
    val periodetype: Periodetype?,
    val periode: Periode?,
    val ansvarssted: String?,
    val kostnadssted: String?,
    val posteringskontoFra: String?,
    val posteringskontoTil: String?
) {
    fun tilJson(): String {
        return jsonMapper.writeValueAsString(this)
    }

}