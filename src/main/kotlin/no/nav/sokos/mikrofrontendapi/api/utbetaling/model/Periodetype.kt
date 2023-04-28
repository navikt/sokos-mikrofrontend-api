package no.nav.sokos.utbetaldata.api.utbetaling.entitet

enum class Periodetype(val databaseverdi: String) {
    UTBETALINGSPERIODE("Utbetalingsperiode"),
    YTELSESPERIODE("Ytelsesperiode")
}