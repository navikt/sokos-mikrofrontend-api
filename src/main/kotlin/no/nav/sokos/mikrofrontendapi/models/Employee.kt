package no.nav.sokos.mikrofrontendapi.models

import kotlinx.serialization.Serializable

@Serializable
data class Employee(
    val id: Int,
    val navn: String,
    val yrke: String
)