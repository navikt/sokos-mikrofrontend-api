package no.nav.sokos.mikrofrontendapi.api.utbetaling.realistiskedata

import java.io.BufferedReader
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import mu.KotlinLogging
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.*
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Aktoertype
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Periode


private val logger = KotlinLogging.logger { }

class CsvLeser() {

    fun lesFil(filnavn: String): List<PosteringData> {
        return reader(filnavn)
            .lines()
            .skip(1)
            .map { PosteringData.fraCsv(it) }
            .toList()
    }

    private fun reader(filnavn: String): BufferedReader {
        logger.info { "Skal forsøke å lese fil $filnavn" }
        val reader = this::class.java.getResourceAsStream(filnavn)
            ?.bufferedReader(Charsets.UTF_8) ?: throw RuntimeException("Fant ikke filen $filnavn")

        return reader
    }
}

val datoformat: DateTimeFormatter = DateTimeFormatter.ofPattern(/* pattern = */ "dd/MM/yyyy")

private fun PosteringData.Companion.fraCsv(csvRad: String): PosteringData {
    val kolonner = csvRad.split(";")
    return PosteringData(
        beregningsId = "1000",
        rettighetshaver = Aktoer(Aktoertype.PERSON, kolonner[0], "Navn Navnesen"),
        posteringsdato = parseDato(kolonner[6]),
        utbetalingsdato = parseValgfriDato(kolonner[16]),
        posteringsbeløp = DebetKreditBeløp(parseBigDecimal(kolonner[9]), DebetKredit.parse(kolonner[10]).kode),
        bilagsserie = (kolonner[7]).replace(" ", ""),
        bilagsnummer = (kolonner[8]).replace(" ", ""),
        posteringskonto = kolonner[1],
        ytelsesperiode = parseValgfriDato(kolonner[13])?.let { Periode(it, parseDato(kolonner[14])) },
        ansvarssted = kolonner[4],
        kostnadssted = kolonner[5],
        behandlingsstatus = (Behandlingsstatus(kode = kolonner[2], beskrivelse = Behandlingskode.parse(kolonner[2]).beskrivelse)),
        utbetalingsKontotype = "Bankkonto",
        utbetalingsKontonummer = kolonner[11],
        posteringsstatus = Posteringsstatus(kode = kolonner[12], beskrivelse = PosteringStatus.parse(kolonner[12]).beskrivelse),
        ytelsestype = kolonner[3],
        ytelsegrad = lesValgfriKolonne(kolonner[15])?.toInt(),
        forsystemPosteringsdato = parseValgfriDato(kolonner[17]),
        utbetalingsmottaker = Aktoer(Aktoertype.PERSON, kolonner[0], "Navn Navnesen"),
        utbetalingsnettobeløp = lesValgfriKolonne(kolonner[20])?.let { DebetKreditBeløp(parseBigDecimal(it), DebetKredit.parse(kolonner[21]).kode)  }
    )

}

private fun parseBigDecimal(s: String): BigDecimal {
    return BigDecimal(s.replace(" ", "").replace(",", ".")).setScale(2)
}

private fun parseDato(datoString: String): LocalDate {
    return LocalDate.parse(datoString, datoformat)
}

private fun parseValgfriDato(valgfriKolonne: String?): LocalDate? {
    val verdi = lesValgfriKolonne(valgfriKolonne)

    if (verdi == null) {
        return null
    } else {
        return parseDato(verdi)
    }
}

private fun lesValgfriKolonne(s: String?) = if (s.isNullOrBlank()) null else s.trim()

