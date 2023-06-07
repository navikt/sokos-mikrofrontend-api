package no.nav.sokos.mikrofrontendapi.api.utbetaling.realistiskedata

import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.HentPosteringResponse
import org.junit.jupiter.api.Test

internal class CsvLeserTest {

    @Test
    fun lesFil() {
        val posteringer = CsvLeser().lesFil("/mockposteringer.csv")
        println( HentPosteringResponse(posteringer.filter{it.rettighetshaver.ident == "00000000001"}).tilJson())
    }
}