package no.nav.sokos.mikrofrontendapi.api.utbetaling.model

import java.lang.IllegalArgumentException

enum class PosteringStatus(val kode: String, val beskrivelse: String) {
    UTBETALT_AV_BANK("18", "Utbetalt av bank"),
    MOTTATT_KONTOFØRER("10", "Mottatt kontofører"),
    SENDT_KONTOFØRER("12", "Sendt kontofører"),
    MOTTAT_FRA_FORSYSTEM("9", "Mottatt fra forsystem");

    companion object {
        fun parse(kode: String): PosteringStatus {
            return values()
                .find { it.kode == kode }
                ?: throw IllegalArgumentException("Greide ikke parse $kode som en ${this::class.java.name}")

        }
    }

}
