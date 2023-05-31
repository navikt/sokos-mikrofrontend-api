package no.nav.sokos.mikrofrontendapi.api.utbetaling.model

import java.lang.IllegalArgumentException

enum class DebetKredit(val kode: String) {
    DEBET("D"),
    KREDIT("K");

    companion object {
        fun parse(kode: String): DebetKredit {
            return values()
                .find { it.kode == kode }
                ?: throw IllegalArgumentException("Greide ikke parse $kode som en ${this::class.java.name}")
        }
    }
}
