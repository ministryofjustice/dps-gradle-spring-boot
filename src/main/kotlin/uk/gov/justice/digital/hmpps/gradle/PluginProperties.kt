package uk.gov.justice.digital.hmpps.gradle

import org.gradle.api.Project
import uk.gov.justice.digital.hmpps.gradle.PluginProperty.IS_KOTLIN_PROJECT
import java.io.FileInputStream
import java.util.Properties

internal enum class PluginProperty(val key: String, val defaultValue: Any) {
  IS_KOTLIN_PROJECT("kotlinProject", true)
}

internal const val PROJECT_PROPERTY_FILE = "dps-gradle-spring-boot.properties"

internal fun loadPluginProperties(project: Project) {
  val properties = Properties()
  try { properties.load(FileInputStream(project.file(PROJECT_PROPERTY_FILE))) }
  catch (ex: Exception) { project.logger.info("No $PROJECT_PROPERTY_FILE file found, using defaults")}

  project.kotlinProject(properties.getBooleanProperty(IS_KOTLIN_PROJECT))
}

internal fun Project.kotlinProject() = this.extensions.extraProperties[IS_KOTLIN_PROJECT.key] as Boolean
private fun Project.kotlinProject(kotlinProject: Boolean) {
  this.extensions.extraProperties[IS_KOTLIN_PROJECT.key] = kotlinProject
}

private fun Properties.getBooleanProperty(pluginProperty: PluginProperty) =
    (this.getProperty(pluginProperty.key)
        ?.equals("true")
        ?:pluginProperty.defaultValue) as Boolean
