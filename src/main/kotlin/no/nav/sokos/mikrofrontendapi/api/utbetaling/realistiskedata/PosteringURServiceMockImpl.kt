package no.nav.sokos.mikrofrontendapi.api.utbetaling.realistiskedata

import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.PosteringData
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.PosteringSøkeData
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Periodetype


class PosteringURServiceMockImpl(var posteringer: List<PosteringData> = emptyList()): PosteringUrService {

    override fun hentPosteringer(posteringSøkeData: PosteringSøkeData): List<PosteringData> {
        val posteringskontoTil = posteringSøkeData.posteringskontoTil ?: posteringSøkeData.posteringskontoFra
        return posteringer
            .filter { posteringSøkeData.rettighetshaver?.equals(it.rettighetshaver.ident) ?: true }
            .filter { posteringSøkeData.utbetalingsmottaker?.equals(it.utbetalingsmottaker.ident) ?: true }
            .filter { posteringSøkeData.ansvarssted?.equals(it.ansvarssted) ?: true }
            .filter { posteringSøkeData.kostnadssted?.equals(it.kostnadssted) ?: true }
            .filter { posteringSøkeData.posteringskontoFra == null || it.posteringskonto.kontonummer >= posteringSøkeData.posteringskontoFra }
            .filter { posteringskontoTil == null || it.posteringskonto.kontonummer <= posteringskontoTil }
            .filter {
                it.ytelsesperiode == null || posteringSøkeData.periodetype != Periodetype.YTELSESPERIODE || !it.ytelsesperiode.fomDato.isBefore(
                    posteringSøkeData.periode.fomDato
                )
            }
            .filter {
                it.ytelsesperiode == null || posteringSøkeData.periodetype != Periodetype.YTELSESPERIODE || !it.ytelsesperiode.tomDato.isAfter(
                    posteringSøkeData.periode.tomDato
                )
            }
            .filter {
                it.utbetalingsdato == null || posteringSøkeData.periodetype != Periodetype.UTBETALINGSPERIODE || !it.utbetalingsdato.isBefore(
                    posteringSøkeData.periode.fomDato
                )
            }
            .filter {
                it.utbetalingsdato == null || posteringSøkeData.periodetype != Periodetype.UTBETALINGSPERIODE || !it.utbetalingsdato.isAfter(
                    posteringSøkeData.periode.tomDato
                )
            }
    }
}