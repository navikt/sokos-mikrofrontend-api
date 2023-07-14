package no.nav.sokos.mikrofrontendapi.api.utbetaling.cucumber

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    features = ["src/test/resources/dokumentasjon/features/utbetaling"],
    glue = ["no.nav.sokos.mikrofrontendapi.api.utbetaling.cucumber.steps"],
    plugin = ["pretty", "html:build/cucumber.html"],
    tags = "not @ignored",
    monochrome = false
)
class RunCucumberTest