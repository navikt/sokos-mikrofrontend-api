package no.nav.sokos.mikrofrontendapi.personvern

import kotlinx.coroutines.runBlocking
import no.nav.sokos.mikrofrontendapi.nom.SkjermetClient
import no.nav.sokos.mikrofrontendapi.security.Saksbehandler

class SkjermetServiceImpl(
    private val skjermetClient: SkjermetClient
): SkjermetService {

    override fun kanBrukerSePerson(personIdent: String, saksbehandler: Saksbehandler): Boolean {
        return saksbehandler.harTilgangTilEgneAnsatte() || !erPersonSkjermet(personIdent)
    }

    private fun erPersonSkjermet(personIdent: String): Boolean {
        return runBlocking {
            skjermetClient.erPersonSkjermet(personIdent)
        }
    }
}