package no.nav.sokos.mikrofrontendapi.personvern

import no.nav.sokos.mikrofrontendapi.security.Saksbehandler

interface SkjermetService {
    fun kanBrukerSePerson(personIdent: String, saksbehandler: Saksbehandler): Boolean
}