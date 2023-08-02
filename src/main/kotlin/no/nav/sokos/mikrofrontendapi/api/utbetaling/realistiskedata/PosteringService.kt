package no.nav.sokos.mikrofrontendapi.api.utbetaling.realistiskedata

import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.PosteringData
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.PosteringSøkeData
import no.nav.sokos.mikrofrontendapi.pdl.PdlService
import no.nav.sokos.mikrofrontendapi.personvern.PersonTilgangException
import no.nav.sokos.mikrofrontendapi.personvern.PersonvernPdlService
import no.nav.sokos.mikrofrontendapi.security.Saksbehandler
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Aktoertype


class PosteringService(
    private val posteringUrService: PosteringUrService,
    private val pdlService: PdlService,
    private val personvernPdlService: PersonvernPdlService
) {

    fun hentPosteringer(posteringSøkeData: PosteringSøkeData, saksbehandler: Saksbehandler): List<PosteringData> {
        val posteringer = posteringerMedNavnFraPdl(posteringSøkeData)
        if (posteringer.isEmpty()) {
            return emptyList()
        }

        val rettighetshavereBrukerHarTilgangTil = posteringer
            .map { it.rettighetshaver.ident }
            .toSet()
            .filter{ personvernPdlService.kanBrukerSePerson(it, saksbehandler)}

        val mottakereBrukerHarTilgangTil = posteringer
        .map { it.utbetalingsmottaker}
            .toSet()
            .filter{  it.aktoertype == Aktoertype.ORGANISASJON || personvernPdlService.kanBrukerSePerson(it.ident, saksbehandler)}
            .map{ it.ident }

        val posteringerBrukerHarTilgangTil = posteringer
            .filter { rettighetshavereBrukerHarTilgangTil.contains(it.rettighetshaver.ident) }
            .filter { mottakereBrukerHarTilgangTil.contains(it.utbetalingsmottaker.ident)}

        if (posteringSøkeData.rettighetshaver != null || posteringSøkeData.utbetalingsmottaker != null) {
            if (posteringerBrukerHarTilgangTil.isEmpty()) {
                throw PersonTilgangException()
            }
        }

        return posteringerBrukerHarTilgangTil
    }

    private fun posteringerMedNavnFraPdl(posteringSøkeData: PosteringSøkeData): List<PosteringData> {
        val posteringer = posteringUrService.hentPosteringer(posteringSøkeData)

        if (posteringer.isEmpty()) {
            return emptyList()
        }

        val navnRettighetshaver: String? = posteringSøkeData.rettighetshaver?.let {
            val navnFraPdl = pdlService.hentPerson(it)?.navn
            navnFraPdl ?: posteringer.first().rettighetshaver.navn
        }

        val navnMottaker: String? = posteringSøkeData.utbetalingsmottaker?.let {
            posteringer.first().utbetalingsmottaker.navn
        }

        val resultat = posteringer.map {
            it.copy(
                rettighetshaver = it.rettighetshaver.copy(navn = navnRettighetshaver),
                utbetalingsmottaker = it.utbetalingsmottaker.copy(
                    navn = if (it.utbetalingsmottaker.aktoertype == Aktoertype.ORGANISASJON) it.utbetalingsmottaker.navn else navnMottaker
                )
            )
        }

        return resultat
    }


}