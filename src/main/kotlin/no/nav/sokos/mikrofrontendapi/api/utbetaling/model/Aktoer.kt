package no.nav.sokos.mikrofrontendapi.api.utbetaling.model

import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Aktoertype

data class Aktoer(
    val aktoertype: Aktoertype,
    val ident: String,
    val navn: String?,
)

