package no.nav.sokos.mikrofrontendapi.security

import com.auth0.jwt.JWT

const val JWT_CLAIM_NAVIDENT = "NAVident"
const val JWT_CLAIM_AZP = "azp"

fun getNAVIdentFromToken(token: String): String {
    val decodedJWT = JWT.decode(token)
    return decodedJWT.claims[JWT_CLAIM_NAVIDENT]?.asString()
        ?: throw RuntimeException("Missing NAVident in private claims")
}

fun getConsumerClientId(token: String): String {
    val decodedJWT = JWT.decode(token)
    return decodedJWT.claims[JWT_CLAIM_AZP]?.asString()
        ?: throw RuntimeException("Missing azp in private claims")
}