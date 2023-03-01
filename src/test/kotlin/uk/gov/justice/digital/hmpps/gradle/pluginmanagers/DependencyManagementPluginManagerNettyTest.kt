package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

private fun wrongTransitiveNettyVersionBuildFile() = """
    plugins {
      id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
    }
    dependencies {
      implementation("org.springframework.boot:spring-boot-starter-webflux")
    }
""".trimIndent()
