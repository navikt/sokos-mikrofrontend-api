package no.nav.sokos.mikrofrontendapi.api.utbetaling.model

import java.math.BigDecimal
import java.time.LocalDate
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Periode

data class PosteringData(
    val rettighetshaver: Aktoer,
    val posteringsdato: LocalDate,
    val utbetalingsdato: LocalDate,
    val utbetalingNettobeloep: BigDecimal,
    val bilagsnummer: String,
    val regnskapskonto: String,
    val ytelsesperiode: Periode,
    val ansvarssted: String,
    val kostnadssted: String
    )


