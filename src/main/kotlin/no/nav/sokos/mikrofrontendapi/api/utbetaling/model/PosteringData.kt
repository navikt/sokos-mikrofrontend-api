package no.nav.sokos.mikrofrontendapi.api.utbetaling.model

import java.math.BigDecimal
import java.time.LocalDate
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Periode

data class PosteringData(
    val beregningsId: String,
    val rettighetshaver: Aktoer,
    val posteringsdato: LocalDate,
    val utbetalingsdato: LocalDate?,
    val posteringsbeløp: DebetKreditBeløp,
    val bilagsserie: String,
    val bilagsnummer: String,
    val posteringskonto: Posteringskonto,
    val ytelsesperiode: Periode?,
    val ansvarssted: String,
    val kostnadssted: String,
    val behandlingsstatus: Behandlingsstatus,
    val utbetalingsKontonummer: String,
    val utbetalingsKontotype: String,
    val posteringsstatus: Posteringsstatus,
    val ytelsegrad: Int?,
    val ytelsestype: String,
    val forsystemPosteringsdato: LocalDate?,
    val utbetalingsmottaker: Aktoer,
    val utbetalingsnettobeløp: DebetKreditBeløp?,
) {
    companion object
}

data class DebetKreditBeløp(val beløpUtenFortegn: BigDecimal, val debetKredit: String) {
    val beløp = if (debetKredit == "K") -beløpUtenFortegn else beløpUtenFortegn
}


data class Posteringsstatus(val kode: String, val beskrivelse: String)

data class Behandlingsstatus(val kode: String, val beskrivelse: String)

data class Posteringskonto(val kontonummer: String, val kontonavn: String)