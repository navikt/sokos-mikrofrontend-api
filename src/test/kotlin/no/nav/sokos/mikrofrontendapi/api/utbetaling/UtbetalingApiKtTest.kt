package no.nav.sokos.mikrofrontendapi.api.utbetaling

import no.nav.sokos.mikrofrontendapi.api.utbetaling.realistiskedata.CsvLeser
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class UtbetalingApiKtTest {

    @Test
    fun tilCsv() {
        val posteringer = CsvLeser().lesFil("/mockposteringer.csv")

        val csv = posteringer.tilCsv()
        val linjer = csv.split("\n")

        assertTrue(linjer.size > 2)
        val antallSeparatorerIOverskrift = linjer[0].filter{it == ';'}.length
        val antallSeparatorerIRad = linjer[1].filter{it == ';'}.length

        assertEquals(antallSeparatorerIOverskrift, antallSeparatorerIRad)

        //println(csv)
    }
}