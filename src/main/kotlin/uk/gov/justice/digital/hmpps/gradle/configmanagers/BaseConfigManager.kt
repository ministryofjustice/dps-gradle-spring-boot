package uk.gov.justice.digital.hmpps.gradle.configmanagers

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import uk.gov.justice.digital.hmpps.gradle.ConfigManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BaseConfigManager(override val project: Project) : ConfigManager {
  override fun configure() {
    setGroupAndVersion()
    applyRepositories()
    addDependencies()
    setJunit5()
    copyResourcesFile("sonar-project.properties")
    copyResourcesFile("gradle.properties")
    copyResourcesFile(".trivyignore")
  }

  private fun setGroupAndVersion() {
    project.group = "uk.gov.justice.digital.hmpps"
    project.version = getVersion()
  }

  private fun getVersion(): String {
    return if (System.getenv().contains("BUILD_NUMBER")) {
      System.getenv("BUILD_NUMBER")
    } else {
      LocalDate.now().format(DateTimeFormatter.ISO_DATE)
    }
  }

  private fun applyRepositories() {
    project.repositories.apply {
      mavenLocal()
      mavenCentral()
    }
  }

  private fun addDependencies() {
  }

  private fun setJunit5() {
    project.tasks.withType(Test::class.java) { it.useJUnitPlatform() }
    project.configurations.findByName("testImplementation")?.dependencies?.find { it.group == "junit" }?.version
      ?.takeIf { it.startsWith("4") }
      .run { project.dependencies.add("testImplementation", "org.junit.vintage:junit-vintage-engine:5.11.4") }
  }
}
