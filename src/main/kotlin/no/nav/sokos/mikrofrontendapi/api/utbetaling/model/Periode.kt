package no.nav.sokos.utbetaldata.api.utbetaling.entitet

import java.time.LocalDate

data class Periode(
    val fomDato: LocalDate,
    val tomDato: LocalDate,
)