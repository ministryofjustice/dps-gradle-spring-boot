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
    copyResourcesFile("sonar-project.properties")
    copyResourcesFile(".trivyignore")
  }

  override fun afterEvaluate() {
    setJunit5()
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
    project.dependencies.add("implementation", "com.google.guava:guava:31.1-jre") // This is only required because the version pulled in as a transitive dependency has CVE vulnerabilities
  }

  private fun setJunit5() {
    project.tasks.withType(Test::class.java) { it.useJUnitPlatform() }
    project.configurations.findByName("testImplementation")?.dependencies?.find { it.group == "junit" }?.version
      ?.takeIf { it.startsWith("4") }
      .run { project.dependencies.add("testImplementation", "org.junit.vintage:junit-vintage-engine:5.9.2") }
  }
}
