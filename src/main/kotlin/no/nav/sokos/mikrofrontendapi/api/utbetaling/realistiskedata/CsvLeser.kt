package no.nav.sokos.mikrofrontendapi.api.utbetaling.realistiskedata

import mu.KotlinLogging
import java.io.BufferedReader
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.Aktoer
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.Behandlingskode
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.DebetKredit
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.PosteringData
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.PosteringStatus
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Aktoertype
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Periode


private val logger = KotlinLogging.logger { }

class CsvLeser(
    private val filstiInn: String,
) {

    fun lesFil(filnavn: String): List<PosteringData> {
        return reader(filnavn)
            .lines()
            .skip(1)
            .map { PosteringData.fraCsv(it) }
            .toList()
    }

    private fun reader(filnavn: String): BufferedReader {
        logger.info { "Skal forsøke å lese fil $filnavn" }
        val reader = this::class.java.getResourceAsStream(filnavn)?.bufferedReader(Charsets.ISO_8859_1) ?: throw RuntimeException("Fant ikke filen $filnavn")

        logger.info { "Har lest fil $filnavn" }
        return reader
    }

}

val datoformat: DateTimeFormatter = DateTimeFormatter.ofPattern(/* pattern = */ "dd/MM/yyyy")

private fun PosteringData.Companion.fraCsv(csvRad: String): PosteringData {
    val kolonner = csvRad.split(";")
    return PosteringData(
        beregningsId = "1000",
        rettighetshaver = Aktoer(Aktoertype.PERSON, kolonner[0], navn = null),
        posteringsdato = LocalDate.parse(kolonner[5], datoformat),
        utbetalingsdato = valgfriDato(lesValgfriKolonne(kolonner[15])),
        posteringsbeloep = parseBigDecimal(kolonner[8]),
        bilagsnummer = (kolonner[6] + kolonner[7]).replace(" ", ""),
        posteringskonto = kolonner[1],
        ytelsesperiode = valgfriDato(lesValgfriKolonne(kolonner[12]))?.let { Periode(it, parseDato(kolonner[13])) },
        ansvarssted = kolonner[4],
        kostnadssted = kolonner[3],
        debetKredit = DebetKredit.parse(kolonner[9]),
        behandlingskode = Behandlingskode.parse(kolonner[2]),
        utbetalingsKontotype = "Bankkonto",
        utbetalingsKontonummer = kolonner[10],
        status = PosteringStatus.parse(kolonner[11]),
        ytelsestype = "Eivind sjekker dette",
        ytelsegrad = lesValgfriKolonne(kolonner[14])?.toInt(),
        forsystemPosteringsdato = valgfriDato(lesValgfriKolonne(kolonner[16])),
    )

}

fun parseBigDecimal(s: String): BigDecimal {
    return BigDecimal(s.replace(" ", "").replace(",", "."))
}

fun parseDato(datoString: String): LocalDate {
    return LocalDate.parse(datoString, datoformat)
}

fun valgfriDato(valgfriKolonne: String?): LocalDate? {
    val dt: LocalDate? = valgfriKolonne?.let { s ->
        val localDate = try {
            LocalDate.parse(s, datoformat)
        } catch (e: DateTimeParseException) {
            throw RuntimeException("Greide ikke å parse $s som en dato", e)
        }
        localDate
    }
    return dt
}

private fun lesValgfriKolonne(s: String) = if (s.isBlank()) null else s.trim()

