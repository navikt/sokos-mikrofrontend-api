package no.nav.sokos.mikrofrontendapi.personvern

import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.PosteringData
import no.nav.sokos.mikrofrontendapi.security.Saksbehandler
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Aktoertype

class PersonvernService(
    private val personvernPdlService: PersonvernPdlService,
    private val skjermetService: SkjermetService
) {

    fun filtrerPosteringer(posteringer: List<PosteringData>, saksbehandler: Saksbehandler): List<PosteringData> {
        val rettighetshavereBrukerHarTilgangTil = posteringer
            .map { it.rettighetshaver.ident }
            .toSet()
            .filter{ personvernPdlService.kanBrukerSePerson(it, saksbehandler)}
            .filter{ skjermetService.kanBrukerSePerson(it, saksbehandler)}

        val mottakereBrukerHarTilgangTil = posteringer
            .map { it.utbetalingsmottaker}
            .toSet()
            .filter{  it.aktoertype == Aktoertype.ORGANISASJON || personvernPdlService.kanBrukerSePerson(it.ident, saksbehandler)}
            .filter{  it.aktoertype == Aktoertype.ORGANISASJON || skjermetService.kanBrukerSePerson(it.ident, saksbehandler)}
            .map{ it.ident }

        val posteringerBrukerHarTilgangTil = posteringer
            .filter { rettighetshavereBrukerHarTilgangTil.contains(it.rettighetshaver.ident) }
            .filter { mottakereBrukerHarTilgangTil.contains(it.utbetalingsmottaker.ident)}

        return posteringerBrukerHarTilgangTil
    }
}