package no.nav.sokos.mikrofrontendapi.api.utbetaling.model

import java.lang.IllegalArgumentException

enum class Behandlingskode(val kode: String, val beskrivelse: String) {
    IKKE_PLIKTIG("0", "Ikke skatt/trekk/opplysnings-pliktig"),
    IKKE_SKATTE_PLIKTIG("1", "Ikke skattepliktig"),
    SKATTE_OG_OPPLYSNINGS_PLIKTIG("2", "Skatte-og-opplysnings-pliktig");

    companion object {
        fun parse(kode: String): Behandlingskode {
            return values()
                .find { it.kode == kode }
                ?: throw IllegalArgumentException("Greide ikke parse $kode som en ${this::class.java.name}")
        }

    }
}
