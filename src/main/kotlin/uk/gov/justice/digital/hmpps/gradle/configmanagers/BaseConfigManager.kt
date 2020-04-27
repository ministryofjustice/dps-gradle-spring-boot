package uk.gov.justice.digital.hmpps.gradle.configmanagers

import org.gradle.api.Project
import uk.gov.justice.digital.hmpps.gradle.ConfigManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BaseConfigManager(override val project: Project) : ConfigManager {

  override fun configure() {
    setGroupAndVersion()
    applyRepositories()
    addDependencies()
  }

  private fun setGroupAndVersion() {
    project.group = "uk.gov.justice.digital.hmpps"
    project.version = getVersion()
  }

  private fun getVersion(): String {
    return if (System.getenv().contains("BUILD_NUMBER")) System.getenv("BUILD_NUMBER")
    else LocalDate.now().format(DateTimeFormatter.ISO_DATE)
  }

  private fun applyRepositories() {
    project.repositories.apply {
      mavenLocal()
      mavenCentral()
    }
  }

  private fun addDependencies() {
    project.dependencies.add("implementation", "com.fasterxml.jackson.module:jackson-module-kotlin")
    project.dependencies.add("implementation", "com.google.guava:guava:29.0-jre") // This is only required because the version pulled in a as transitive dependency has CVE vulnerabilities
  }

}