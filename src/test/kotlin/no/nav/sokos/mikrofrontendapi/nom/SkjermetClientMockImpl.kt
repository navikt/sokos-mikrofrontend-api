package no.nav.sokos.mikrofrontendapi.nom

class SkjermetClientMockImpl(var skjermedePersoner: List<String>)
    : SkjermetClient {

    override suspend fun erPersonSkjermet(personIdent: String): Boolean {
        return skjermedePersoner.contains(personIdent)
    }

}