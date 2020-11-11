package uk.gov.justice.digital.hmpps.gradle

import org.gradle.api.Project
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

interface ConfigManager {

  val project: Project

  fun configure() {}
  fun afterEvaluate() {}

  fun copyResourcesFile(file: String) {
    val inputStream = this::class.java.classLoader.getResourceAsStream(file)
    val destination = File("${project.projectDir.absolutePath}/$file")
    val line = if (destination.exists()) destination.useLines { it.firstOrNull() } else null
    if (line == null || line.startsWith("# WARNING")) {
      project.logger.info("Copying $file as file doesn't exist or still has warning as first line")
      Files.copy(inputStream!!, destination.toPath(), StandardCopyOption.REPLACE_EXISTING)
    } else {
      project.logger.info("Not copying $file as file doesn't have warning as first line")
    }
  }
}
