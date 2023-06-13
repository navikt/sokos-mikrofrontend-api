package no.nav.sokos.mikrofrontendapi.api.utbetaling

import no.nav.sokos.mikrofrontendapi.api.utbetaling.realistiskedata.CsvLeser
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class UtbetalingApiKtTest {

    @Test
    fun tilCsv() {
        val posteringer = CsvLeser().lesFil("/mockposteringer.csv")

        val csv = posteringer.tilCsv()

        println(csv)
    }
}