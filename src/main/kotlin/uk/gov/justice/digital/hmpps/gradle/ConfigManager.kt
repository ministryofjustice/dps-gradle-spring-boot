package uk.gov.justice.digital.hmpps.gradle

import org.gradle.api.Project

interface ConfigManager {

  val project: Project

  fun configure() {}
  fun afterEvaluate() {}
}