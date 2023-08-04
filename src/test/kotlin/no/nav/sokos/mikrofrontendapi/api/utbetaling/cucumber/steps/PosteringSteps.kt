package no.nav.sokos.mikrofrontendapi.api.utbetaling.cucumber.steps

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import java.math.BigDecimal
import java.time.LocalDate
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.Aktoer
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.Behandlingsstatus
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.DebetKreditBeløp
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.PosteringData
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.PosteringSøkeData
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.Posteringskonto
import no.nav.sokos.mikrofrontendapi.api.utbetaling.model.Posteringsstatus
import no.nav.sokos.mikrofrontendapi.api.utbetaling.realistiskedata.PosteringService
import no.nav.sokos.mikrofrontendapi.api.utbetaling.realistiskedata.PosteringURServiceMockImpl
import no.nav.sokos.mikrofrontendapi.domene.PersonData
import no.nav.sokos.mikrofrontendapi.nom.SkjermetClientMockImpl
import no.nav.sokos.mikrofrontendapi.pdl.PdlServiceMockImpl
import no.nav.sokos.mikrofrontendapi.personvern.PersonTilgangException
import no.nav.sokos.mikrofrontendapi.personvern.PersonvernPdlService
import no.nav.sokos.mikrofrontendapi.personvern.PersonvernService
import no.nav.sokos.mikrofrontendapi.personvern.SkjermetServiceImpl
import no.nav.sokos.mikrofrontendapi.security.Saksbehandler
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Aktoertype
import no.nav.sokos.utbetaldata.api.utbetaling.entitet.Periode
import no.nav.sokos.utbetaldata.cucumber.steps.Kolonne
import no.nav.sokos.utbetaldata.cucumber.steps.KolonneMapper
import org.assertj.core.api.Assertions.assertThat

private const val defaultNavn = "Kon Kurs"

private val posteringDataTemplate = PosteringData(
    beregningsId = "123",
    rettighetshaver = Aktoer(Aktoertype.PERSON, "012345", defaultNavn),
    posteringsdato = LocalDate.of(2020, 2, 2),
    utbetalingsdato = LocalDate.of(2020, 2, 2),
    posteringsbeløp = DebetKreditBeløp(BigDecimal("2000.00"), "K"),
    bilagsserie = "10",
    bilagsnummer = "1234234",
    posteringskonto = Posteringskonto("1234", defaultNavn),
    ytelsesperiode = Periode(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 31)),
    ansvarssted = "1234",
    kostnadssted = "2456",
    behandlingsstatus = Behandlingsstatus("", ""),
    utbetalingsKontonummer = "12345",
    utbetalingsKontotype = "",
    posteringsstatus = Posteringsstatus("d", "d"),
    ytelsegrad = 100,
    ytelsestype = "",
    forsystemPosteringsdato = LocalDate.of(2020, 2, 2),
    utbetalingsmottaker = Aktoer(Aktoertype.PERSON, "012345", defaultNavn),
    utbetalingsnettobeløp = null
)

class PosteringSteps : No {
    private var saksbehandler = Saksbehandler("Default", emptyList<String>())
    private val posteringURServiceMockImpl = PosteringURServiceMockImpl()
    private val pdlService = PdlServiceMockImpl()
    private val personvernPdlService = PersonvernPdlService(pdlService)
    private var skjermedePersoner = emptyList<String>()

    private val skjermetClient = SkjermetClientMockImpl(skjermedePersoner)

    private val skjermetService = SkjermetServiceImpl(skjermetClient)
    private var faktiskFeilmelding: String? = null

    private val personvernService = PersonvernService(personvernPdlService, skjermetService)

    private var posteringSøkeResultat = emptyList<PosteringData>()



    private val posteringService = PosteringService(
        posteringURServiceMockImpl,
        pdlService,
        personvernService
    )

    init {
        Gitt(
            "at følgende posteringer finnes i UR:"
        ) { dataTable: DataTable ->
            posteringURServiceMockImpl.posteringer = dataTable.tilPosteringer()
        }

        Gitt(
            "en saksbehandler med følgende roller:"
        ) { dataTable: DataTable ->
            saksbehandler = Saksbehandler("Saksbehandler", dataTable.tilRoller())
        }

        Gitt(
            "at følgende personer finnes i PDL:"
        ) { dataTable: DataTable ->
            pdlService.setPersoner(dataTable.tilPersoner())
        }

        Når(
            "posteringer søkes etter med følgende kriterier:"
        ) { dataTable: DataTable ->
            val posteringSøkeData = dataTable.tilPosteringSøkeData()


            try {
                posteringSøkeResultat = posteringService.hentPosteringer(posteringSøkeData, saksbehandler)
            } catch (personTilgangException: PersonTilgangException) {
                faktiskFeilmelding = personTilgangException.message
            }
        }

        Så(
            "skal følgende posteringer returneres:"
        ) { dataTable: DataTable ->
            assertThat(posteringSøkeResultat).containsExactlyInAnyOrderElementsOf(dataTable.tilPosteringer())
        }

        Så(
            "skal følgende feilmelding gis: {string}"
        ) { feilmelding: String ->
            assertThat(faktiskFeilmelding).isEqualTo(feilmelding)
        }
    }
}

private fun DataTable.tilPosteringSøkeData(): PosteringSøkeData {
    Kolonne.validerAtKolonnenavnErGyldig(this)

    return asMaps().map { rad ->
        val kolonneMapper = KolonneMapper(rad)

        val periodeFom = kolonneMapper.parseDato(Kolonne.PERIODE_FOM)
        val periodeTom = kolonneMapper.parseDato(Kolonne.PERIODE_TOM)

        PosteringSøkeData(
            rettighetshaver = kolonneMapper.parseValgfriString(Kolonne.RETTIGHETSHAVER),
            utbetalingsmottaker = kolonneMapper.parseValgfriString(Kolonne.MOTTAKER),
            periodetype = kolonneMapper.parsePeriodetype(Kolonne.PERIODETYPE),
            periode = Periode(periodeFom, periodeTom),
            kostnadssted = kolonneMapper.parseValgfriString(Kolonne.KOSTNADSSTED),
            ansvarssted = kolonneMapper.parseValgfriString(Kolonne.ANSVARSSTED),
            posteringskontoFra = kolonneMapper.parseValgfriString(Kolonne.POSTERINGSKONTO_FRA),
            posteringskontoTil = kolonneMapper.parseValgfriString(Kolonne.POSTERINGSKONTO_TIL)
        )
    }.first()
}

private fun DataTable.tilPersoner(): List<PersonData> {
    Kolonne.validerAtKolonnenavnErGyldig(this)

    return asMaps().map { rad ->
        val kolonneMapper = KolonneMapper(rad)

        PersonData(
            kolonneMapper.parseString(Kolonne.IDENT),
            kolonneMapper.parseString(Kolonne.NAVN),
            kolonneMapper.parseAdressebeskyttelse(Kolonne.ADRESSEBESKYTTELSE)
        )
    }
}


private fun DataTable.tilRoller(): List<String> {
    Kolonne.validerAtKolonnenavnErGyldig(this)

    return asMaps().map { rad ->
        val kolonneMapper = KolonneMapper(rad)
        kolonneMapper.parseString(Kolonne.ROLLE)
    }
}

private fun DataTable.tilPosteringer(): List<PosteringData> {
    Kolonne.validerAtKolonnenavnErGyldig(this)

    return asMaps().map { rad ->
        val kolonneMapper = KolonneMapper(rad)
        val rettighetshaver = kolonneMapper.parseString(Kolonne.RETTIGHETSHAVER)
        val mottaker = kolonneMapper.parseString(Kolonne.MOTTAKER)
        val mottakertype = kolonneMapper.parseAktørtype(Kolonne.MOTTAKER_TYPE)
        val mottakerNavn = kolonneMapper.parseValgfriString(Kolonne.MOTTAKER_NAVN)
        val rettighetshaverNavn = kolonneMapper.parseValgfriString(Kolonne.RETTIGHETSHAVER_NAVN)
        posteringDataTemplate.copy(
            rettighetshaver = Aktoer(Aktoertype.PERSON, rettighetshaver, rettighetshaverNavn),
            bilagsnummer = kolonneMapper.parseString(Kolonne.BILAGSNUMMER),
            posteringsbeløp = DebetKreditBeløp.fraBigDecimal(kolonneMapper.parseBigDecimal(Kolonne.POSTERINGSBELØP)),
            utbetalingsmottaker = Aktoer(mottakertype, mottaker, mottakerNavn),
            utbetalingsdato = kolonneMapper.parseValgfriDato(Kolonne.UTBETALINGSDATO)
        )
    }

}