package no.nav.sokos.mikrofrontendapi.api.utbetaling.realistiskedata

import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.HentPosteringResponse
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.summer
import org.junit.jupiter.api.Test

internal class CsvLeserTest {

    @Test
    fun lesFil() {
        val posteringer = CsvLeser().lesFil("/mockposteringer.csv").filter {  it.rettighetshaver.ident == "24417337179"}
        println( HentPosteringResponse(
            posteringer,
            posteringer.summer()
        ).tilJson())
    }
}