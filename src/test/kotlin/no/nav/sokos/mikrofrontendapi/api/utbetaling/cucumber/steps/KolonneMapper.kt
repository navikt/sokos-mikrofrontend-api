package no.nav.sokos.utbetaldata.cucumber.steps

import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import no.nav.sokos.mikrofrontendapi.domene.AdressebeskyttelseGraderingData
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Periodetype

class KolonneMapper(private val rad: Map<String, String>) {
    fun parseString(kolonne: Kolonne): String {
        return parseValgfriString(kolonne) ?: throw(RuntimeException("Obligatorisk kolonne mangler: ${kolonne.kolonnenavn}"))
    }

    fun parseValgfriString(kolonne: Kolonne): String? = rad[kolonne.kolonnenavn]

    fun parseDato(kolonne: Kolonne): LocalDate =
        parseValgfriDato(kolonne) ?: throw(RuntimeException("Obligatorisk kolonne mangler: ${kolonne.kolonnenavn}"))

    fun parseValgfriDato(kolonne: Kolonne): LocalDate? {
        val datoStr = rad[kolonne.kolonnenavn]

        return datoStr?.let { param ->
            val datoFormat = "yyyy-MM-dd"
            runCatching {
                LocalDate.parse(param, DateTimeFormatter.ofPattern(datoFormat))
            }
                .onFailure { throw RuntimeException("${kolonne.kolonnenavn} må være på formen $datoFormat, men var $param") }
                .getOrThrow()
        }
    }

    fun parseBigDecimal(kolonne: Kolonne): BigDecimal =
        parseValgfriBigDecimal(kolonne) ?: throw(RuntimeException("Obligatorisk kolonne mangler: ${kolonne.kolonnenavn}"))

    fun parseValgfriBigDecimal(kolonne: Kolonne): BigDecimal? = rad[kolonne.kolonnenavn]?.let { BigDecimal(it.replace(",", ".")) }

    fun parseDouble(kolonne: Kolonne): Double =
        parseValgfriDouble(kolonne) ?: throw(RuntimeException("Obligatorisk kolonne mangler: ${kolonne.kolonnenavn}"))

    fun parseValgfriDouble(kolonne: Kolonne): Double? = rad[kolonne.kolonnenavn]?.toDouble()

    fun parseAdressebeskyttelse(kolonne: Kolonne): AdressebeskyttelseGraderingData =
        AdressebeskyttelseGraderingData.valueOf(parseString(kolonne).uppercase())

    fun parsePeriodetype(kolonne: Kolonne): Periodetype = Periodetype.valueOf(parseString(kolonne).uppercase())
}
