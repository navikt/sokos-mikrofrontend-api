package no.nav.sokos.mikrofrontendapi.pdl

import no.nav.sokos.mikrofrontendapi.domene.PersonData

interface PdlService {
    fun hentPerson(ident: String): PersonData?
}