package no.nav.sokos.mikrofrontendapi.api.utbetaling.model

import java.math.BigDecimal
import java.time.LocalDate
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Periode

data class PosteringData(
    val beregningsId: String,
    val rettighetshaver: Aktoer,
    val posteringsdato: LocalDate,
    val utbetalingsdato: LocalDate?,
    val posteringsbeloep: BigDecimal,
    val bilagsnummer: String,
    val posteringskonto: String,
    val ytelsesperiode: Periode?,
    val ansvarssted: String,
    val kostnadssted: String,
    val behandlingsstatus: Behandlingsstatus,
    val debetKredit: DebetKredit,
    val utbetalingsKontonummer: String,
    val utbetalingsKontotype: String,
    val status: Status,
    val ytelsegrad: Int?,
    val ytelsestype: String,
    val forsystemPosteringsdato: LocalDate?,
    val utbetalingsmottaker: Aktoer
    ) {
    companion object
}

data class Status(val kode: String, val beskrivelse: String)

data class Behandlingsstatus(val kode: String, val beskrivelse: String)
