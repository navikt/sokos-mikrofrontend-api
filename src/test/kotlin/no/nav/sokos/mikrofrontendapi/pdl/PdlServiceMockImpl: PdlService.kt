package no.nav.sokos.mikrofrontendapi.pdl

import no.nav.sokos.mikrofrontendapi.domene.PersonData

class PdlServiceMockImpl: PdlService {
    private var personListe = emptyList<PersonData>()

    override fun hentPerson(ident: String): PersonData? {
        return personListe.find { it.ident == ident }
    }

    fun setPersoner(personer: List<PersonData>) {
        personListe = personer
    }
}