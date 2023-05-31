package no.nav.sokos.mikrofrontendapi.api.utbetaling.model

enum class Behandlingskode(val kode: Int, val beskrivelse: String) {
    IKKE_PLIKTIG(0, "Ikke skatt/trekk/opplysnings-pliktig"),
    IKKE_SKATTE_PLIKTIG(1, "Ikke skattepliktig"),
    SKATTE_OG_OPPLYSNINGS_PLIKTIG(2, "Skatte-og-opplysnings-pliktig")
}
