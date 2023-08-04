package no.nav.sokos.mikrofrontendapi.nom

interface SkjermetClient {
    suspend fun erPersonSkjermet(personIdent: String) : Boolean
}