package uk.gov.justice.digital.hmpps.gradle.configmanagers

import org.gradle.api.Project

interface ConfigManager {

  val project: Project

  fun configure() {}
}