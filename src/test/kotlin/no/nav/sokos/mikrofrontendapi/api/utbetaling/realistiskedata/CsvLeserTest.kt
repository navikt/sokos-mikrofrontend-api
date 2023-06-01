package no.nav.sokos.mikrofrontendapi.api.utbetaling.realistiskedata

import org.junit.jupiter.api.Test

internal class CsvLeserTest {

    @Test
    fun lesFil() {
        CsvLeser().lesFil("/mockposteringer.csv")
    }
}