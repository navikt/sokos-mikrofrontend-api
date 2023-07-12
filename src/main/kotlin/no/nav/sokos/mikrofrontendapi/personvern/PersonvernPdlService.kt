package no.nav.sokos.mikrofrontendapi.personvern

import no.nav.sokos.mikrofrontendapi.pdl.PdlService
import no.nav.sokos.mikrofrontendapi.security.Saksbehandler

class PersonvernPdlService(val pdlService: PdlService) {

    fun kanBrukerSePerson(ident: String, saksbehandler: Saksbehandler): Boolean {
        val person = pdlService.hentPerson(ident)

        if (person == null || person.erUgradert()) {
            return true
        } else {
            return (
                    person.erFortrolig() && saksbehandler.harTilgangTilFortrolig()
                    ) || (
                    person.erStrengtFortrolig() && saksbehandler.harTilgangTilStrengtFortrolig()
                    )
        }
    }
}


