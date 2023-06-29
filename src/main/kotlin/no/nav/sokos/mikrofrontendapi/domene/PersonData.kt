package no.nav.sokos.mikrofrontendapi.domene

import no.nav.pdl.enums.AdressebeskyttelseGradering
import no.nav.pdl.hentperson.Person
import no.nav.sokos.mikrofrontendapi.domene.AdressebeskyttelseGraderingData.Companion.fra

data class PersonData(
    val ident: String,
    val navn: String,
    private val adressebeskyttelse: AdressebeskyttelseGraderingData
) {

    fun erFortrolig(): Boolean {
        return adressebeskyttelse == AdressebeskyttelseGraderingData.FORTROLIG
    }

    fun erStrengtFortrolig(): Boolean {
        return adressebeskyttelse == AdressebeskyttelseGraderingData.STRENGT_FORTROLIG
    }

    companion object {
        fun fra(ident: String, person: Person): PersonData {
            return PersonData(
                ident = ident,
                navn = person.navn.first().let { "${it.fornavn} ${it.mellomnavn ?: ""} ${it.etternavn}" },
                adressebeskyttelse = fra(person.adressebeskyttelse.firstOrNull()?.gradering)
            )
        }
    }

}

enum class AdressebeskyttelseGraderingData {
    STRENGT_FORTROLIG,
    FORTROLIG,
    UGRADERT;

    companion object {
        fun fra(adressebeskyttelseGradering: AdressebeskyttelseGradering?): AdressebeskyttelseGraderingData {
            return when (adressebeskyttelseGradering) {
                AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND -> STRENGT_FORTROLIG
                AdressebeskyttelseGradering.STRENGT_FORTROLIG -> STRENGT_FORTROLIG
                AdressebeskyttelseGradering.FORTROLIG -> FORTROLIG
                else -> {
                    UGRADERT
                }
            }
        }
    }
}
