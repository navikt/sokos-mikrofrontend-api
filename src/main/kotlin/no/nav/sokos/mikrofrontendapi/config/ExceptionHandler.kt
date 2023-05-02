package no.nav.sokos.mikrofrontendapi.config

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import mu.KotlinLogging
import javax.naming.AuthenticationException

private val logger = KotlinLogging.logger { }

fun StatusPagesConfig.exceptionHandler() {

    exception<NotFoundException> { call, cause ->
        call.logWarningAndRespond(cause, HttpStatusCode.NotFound) {
            "Fant ingen konto for angitt kontohaver"
        }
    }

    exception<AuthenticationException> { call, cause ->
        call.logWarningAndRespond(cause, HttpStatusCode.Forbidden) {
            cause.message!!
        }
    }

    exception<BadRequestException> { call, cause ->
        call.logWarningAndRespond(cause, HttpStatusCode.BadRequest) {
            "Meldingen oppfyller ikke swagger kontrakt"
        }
    }

    exception<Throwable> { call, cause ->
        call.logErrorAndRespond(cause) {
            "Det har oppstått en feil. Se log for feilmelding."
        }
    }
}


private suspend inline fun ApplicationCall.logWarningAndRespond(
    cause: Throwable,
    status: HttpStatusCode,
    lazyMessage: () -> String
) {
    val message = lazyMessage()
    logger.warn(cause) { message }

    val response = Feilmelding(message)
    this.respond(status, response)
}

private suspend inline fun ApplicationCall.logErrorAndRespond(
    cause: Throwable,
    status: HttpStatusCode = HttpStatusCode.InternalServerError,
    lazyMessage: () -> String
) {
    val message = lazyMessage()
    logger.error(cause) { message }

    val response = Feilmelding(message)
    this.respond(status, response)
}
private suspend inline fun ApplicationCall.logInfoAndRespond(
    cause: Throwable,
    status: HttpStatusCode = HttpStatusCode.InternalServerError,
    lazyMessage: () -> String
) {
    val message = lazyMessage()
    logger.info(cause) { message }

    val response = Feilmelding(message)
    this.respond(status, response)
}

class Feilmelding(val feilmelding: String)
