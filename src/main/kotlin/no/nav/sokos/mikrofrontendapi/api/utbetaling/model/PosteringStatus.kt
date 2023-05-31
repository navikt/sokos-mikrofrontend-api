package no.nav.sokos.mikrofrontendapi.api.utbetaling.model

import java.lang.IllegalArgumentException

enum class PosteringStatus(val kode: String, val beskrivelse: String) {
    UTBETALT_AV_BANK("18", "Utbetalt av bank"),
    HØRE_MED_GEIR_10("10", "Høre med Geir 10"),
    HØRE_MED_GEIR_12("12", "Høre med Geir 12"),
    MOTTAT_FRA_FORSYSTEM("9", "Mottatt fra forsystem");

    companion object {
        fun parse(kode: String): PosteringStatus {
            return values()
                .find { it.kode == kode }
                ?: throw IllegalArgumentException("Greide ikke parse $kode som en ${this::class.java.name}")

        }
    }

}
