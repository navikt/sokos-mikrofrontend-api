package no.nav.sokos.mikrofrontendapi.config

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import mu.KotlinLogging
import java.io.File

private val logger = KotlinLogging.logger {}

object PropertiesConfig {

    private val defaultProperties = ConfigurationMap(
        mapOf(
            "NAIS_APP_NAME" to "sokos-mikrofrontend-api",
            "NAIS_NAMESPACE" to "okonomi"
        )
    )

    private val localDevProperties = ConfigurationMap(
        mapOf(
            "USE_AUTHENTICATION" to "true",
            "APPLICATION_PROFILE" to Profile.LOCAL.toString(),
            "AZURE_APP_CLIENT_ID" to "azure-app-client-id",
            "AZURE_APP_WELL_KNOWN_URL" to "azure-app-well-known-url",
        )
    )

    private val devProperties = ConfigurationMap(mapOf("APPLICATION_PROFILE" to Profile.DEV.toString()))
    private val prodProperties = ConfigurationMap(mapOf("APPLICATION_PROFILE" to Profile.PROD.toString()))

    private val config = when (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")) {
        "dev-gcp" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding devProperties overriding defaultProperties
        "prod-gcp" -> ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding prodProperties overriding defaultProperties
        else ->
            ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding ConfigurationProperties.fromOptionalFile(
                File("defaults.properties")
            ) overriding localDevProperties overriding defaultProperties
    }

    operator fun get(key: String): String = config[Key(key, stringType)]

    data class Configuration(
        val naisAppName: String = get("NAIS_APP_NAME"),
        val profile: Profile = Profile.valueOf(this["APPLICATION_PROFILE"]),
        val useAuthentication: Boolean = get("USE_AUTHENTICATION").toBoolean(),
        val azureAdConfig: AzureAdConfig = AzureAdConfig(),
        val azureAdProviderConfig: AzureAdProviderConfig = AzureAdProviderConfig()
    )

    class AzureAdConfig(
        val clientId: String = this["AZURE_APP_CLIENT_ID"],
        val wellKnownUrl: String = this["AZURE_APP_WELL_KNOWN_URL"]
    )

    data class AzureAdProviderConfig(
        val clientId: String = readProperty("AZURE_APP_CLIENT_ID", ""),
        val authorityEndpoint: String = readProperty("AZURE_APP_WELL_KNOWN_URL", ""),
        val tenant: String = readProperty("AZURE_APP_TENANT_ID", ""),
        val clientSecret: String = readProperty("AZURE_APP_CLIENT_SECRET", ""),
        val pdlClientId: String = readProperty("PDL_CLIENT_ID", ""),
        val useSecurity: Boolean = readProperty("PDL_USE_SECURITY", "true") == "true",
        val tokenUrl: String = readProperty("TOKEN_URL", "https://login.microsoftonline.com/${tenant}/oauth2/v2.0/token"),
    )


    enum class Profile {
        LOCAL, DEV, PROD
    }
}

private fun readProperty(name: String, default: String? = null) =
    System.getenv(name)
        ?: System.getProperty(name)
        ?: default.takeIf { it != null }?.also { logger.info("Bruker default verdi for property $name") }
        ?: throw RuntimeException("Mandatory property '$name' was not found")
