package no.nav.sokos.mikrofrontendapi.personvern

import javax.naming.AuthenticationException

class PersonTilgangException(): AuthenticationException("Du har ikke tilgang til å søke opp denne personen.")