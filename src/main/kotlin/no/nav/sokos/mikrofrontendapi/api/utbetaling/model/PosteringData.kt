package no.nav.sokos.mikrofrontendapi.api.utbetaling.model

import java.math.BigDecimal
import java.time.LocalDate
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Periode

data class PosteringData(
    val beregningsId: String,
    val rettighetshaver: Aktoer,
    val posteringsdato: LocalDate,
    val utbetalingsdato: LocalDate?,
    val posteringsbeløp: DebetKreditBeløp,
    val bilagsserie: String,
    val bilagsnummer: String,
    val posteringskonto: Posteringskonto,
    val ytelsesperiode: Periode?,
    val ansvarssted: String,
    val kostnadssted: String,
    val behandlingsstatus: Behandlingsstatus,
    val utbetalingsKontonummer: String,
    val utbetalingsKontotype: String,
    val posteringsstatus: Posteringsstatus,
    val ytelsegrad: Int?,
    val ytelsestype: String,
    val forsystemPosteringsdato: LocalDate?,
    val utbetalingsmottaker: Aktoer,
    val utbetalingsnettobeløp: DebetKreditBeløp?,
) {
    internal companion object
}

data class DebetKreditBeløp(val beløpUtenFortegn: BigDecimal, val debetKredit: String) {
    val beløp = if (debetKredit == "K") -beløpUtenFortegn else beløpUtenFortegn

    companion object {
        fun fraBigDecimal(beløp: BigDecimal): DebetKreditBeløp {
            val debetKredit = if (beløp.signum() == 0) "D" else "K"
            return DebetKreditBeløp(beløp, debetKredit)
        }
    }
}

data class Posteringsstatus(val kode: String, val beskrivelse: String)

data class Behandlingsstatus(val kode: String, val beskrivelse: String)

data class Posteringskonto(val kontonummer: String, val kontonavn: String)

fun List<PosteringData>.tilCsv(): String {
    val kolonneHeader =
        buildString {
            append("beregningsId;")
            append("rettighetshaver ident;")
            append("rettighetshaver navn;")
            append("posteringsdato;")
            append("utbetalingsdato;")
            append("posteringsbeløp;")
            append("bilagsserie;")
            append("bilagsnummer;")
            append("posteringskonto;")
            append("posteringskonto navn;")
            append("fomDato;")
            append("tomDato;")
            append("ansvarssted;")
            append("kostnadssted;")
            append("behandlingsstatus kode;")
            append("behandlingsstatus navn;")
            append("utbetalingskontonummer;")
            append("utbetalingskontotype;")
            append("posteringsstatus kode;")
            append("posteringsstatus navn;")
            append("ytelsegrad;")
            append("ytelsestype;")
            append("forsystemPosteringsdato;")
            append("Utbetalingsmottaker ident;")
            append("utbetalingsmottaker navn;")
            append("utbetalingsnettobeløp")
        }

    return "$kolonneHeader\n" + joinToString("\n") { it.tilCsv() }
}

private fun PosteringData.tilCsv(): String {
    return buildString {
        append("$beregningsId;")
        append("\t${rettighetshaver.ident};")
        append("\t${rettighetshaver.navn};")
        append("${posteringsdato};")
        append("${utbetalingsdato ?: ""};")
        append("${posteringsbeløp.beløp.formater()};")
        append("\t${bilagsserie};")
        append("\t${bilagsnummer};")
        append("\t${posteringskonto.kontonummer};")
        append("${posteringskonto.kontonavn};")
        append("${ytelsesperiode?.fomDato ?: ""};")
        append("${ytelsesperiode?.tomDato ?: ""};")
        append("\t$ansvarssted;")
        append("\t$kostnadssted;")
        append("${behandlingsstatus.kode};")
        append("${behandlingsstatus.beskrivelse};")
        append("\t$utbetalingsKontonummer;")
        append("$utbetalingsKontotype;")
        append("${posteringsstatus.kode};")
        append("${posteringsstatus.beskrivelse};")
        append("${ytelsegrad ?: ""};")
        append("$ytelsestype;")
        append("${forsystemPosteringsdato ?: ""};")
        append("\t${utbetalingsmottaker.ident};")
        append("\t${utbetalingsmottaker.navn};")
        append(formaterDesimaltall(utbetalingsnettobeløp?.beløp))
    }
}

private fun BigDecimal.formater(): String {
    return this.toString()
        .replace(".", ",")
        .replace(" ", "")
}

private fun formaterDesimaltall(verdi: BigDecimal?): String {
    return verdi?.formater() ?: ""
}