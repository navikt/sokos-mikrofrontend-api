package no.nav.sokos.mikrofrontendapi.security

private const val GRUPPE_EGNE_ANSATTE = "0000-GA-okonomi-egne_ansatte"
private const val GRUPPE_FORTROLIG = "0000-GA-okonomi-fortrolig"
private const val GRUPPE_STRENGT_FORTROLIG = "0000-GA-okonomi-strengt_fortrolig"

data class Saksbehandler(val ident: String, val roller: List<String>){
    fun harTilgangTilFortrolig(): Boolean {
        return roller.contains(GRUPPE_FORTROLIG)
    }

    fun harTilgangTilStrengtFortrolig(): Boolean {
        return roller.contains(GRUPPE_STRENGT_FORTROLIG)
    }

    fun harTilgangTilEgneAnsatte(): Boolean {
        return roller.contains(GRUPPE_EGNE_ANSATTE)
    }
}
