package uk.gov.justice.digital.hmpps.gradle.configmanagers

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import uk.gov.justice.digital.hmpps.gradle.ConfigManager
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BaseConfigManager(override val project: Project) : ConfigManager {
  override fun configure() {
    setGroupAndVersion()
    applyRepositories()
    addDependencies()
    setJunit5()
    copySonarProperties()
  }

  private fun copySonarProperties() {
    val inputStream = javaClass.classLoader.getResourceAsStream("sonar-project.properties")
    val destination = File("${project.projectDir.absolutePath}/sonar-project.properties")
    val line = if (destination.exists()) destination.useLines { it.firstOrNull() } else null
    if (line == null || line.startsWith("# WARNING")) {
      project.logger.info("Copying sonar project properties as file doesn't exist or still has warning as first line")
      Files.copy(inputStream!!, destination.toPath(), StandardCopyOption.REPLACE_EXISTING)
    } else {
      project.logger.info("Not copying sonar project properties as file doesn't have warning as first line")
    }
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
    project.dependencies.add("implementation", "com.google.guava:guava:29.0-jre") // This is only required because the version pulled in as a transitive dependency has CVE vulnerabilities
  }

  private fun setJunit5() {
    val testTask = project.tasks.getByName("test") as Test
    testTask.useJUnitPlatform()
  }

}
