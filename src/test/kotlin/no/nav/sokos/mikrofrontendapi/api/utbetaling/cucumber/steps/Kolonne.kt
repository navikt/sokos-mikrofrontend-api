package no.nav.sokos.utbetaldata.cucumber.steps

import io.cucumber.datatable.DataTable
import java.lang.IllegalArgumentException

enum class Kolonne(val kolonnenavn: String) {
    ROLLE("Rolle"),
    RETTIGHETSHAVER("Rettighetshaver"),
    MOTTAKER("Mottaker"),
    POSTERINGSBELØP("Posteringsbeløp"),
    BILAGSNUMMER ("Bilagsnummer"),
    IDENT ("Ident"),
    NAVN("Navn"),
    ADRESSEBESKYTTELSE("Adressebeskyttelse"),
    PERIODETYPE("Periodetype"),
    PERIODE_FOM("Periode FOM"),
    PERIODE_TOM("Periode TOM"),
    KOSTNADSSTED("Kostnadssted"),
    ANSVARSSTED("Ansvarssted"),
    POSTERINGSKONTO_FRA("Posteringskonto fra"),
    POSTERINGSKONTO_TIL("Posteringskonto til"),
    UTBETALINGSDATO("Utbetalingsdato")
    ;

    companion object {
        fun validerAtKolonnenavnErGyldig(tabell: DataTable) {
            if (!tabell.asMaps().isEmpty()) {
                val ugyldige = tabell.asMaps().first().keys
                    .filterNot { key -> key.contains("#") }
                    .filterNot { key -> values().map{ it.kolonnenavn }.contains(key) }
                if (ugyldige.isNotEmpty()) {
                    throw IllegalArgumentException(
                        "Ugyldig kolonne angitt: $ugyldige\nStøttede kolonner er: $ values().map{ it.kolonnenavn }"
                    )
                }
            }
        }
    }
}