package uk.gov.justice.digital.hmpps.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import uk.gov.justice.digital.hmpps.gradle.ProjectProperties.IS_KOTLIN_PROJECT
import uk.gov.justice.digital.hmpps.gradle.configmanagers.AppInsightsConfigManager
import uk.gov.justice.digital.hmpps.gradle.configmanagers.BaseConfigManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.DependencyCheckPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.DependencyManagementPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.GitPropertiesPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.JavaPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.KotlinPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.SpringBootPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.TestLoggerPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.UseLatestVersionsPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.VersionsPluginManager
import java.io.FileInputStream
import java.util.Properties

enum class ProjectProperties(val key: String, val defaultValue: Any) {
  IS_KOTLIN_PROJECT("kotlinProject", true)
}

class DpsSpringBootPlugin : Plugin<Project> {

  override fun apply(project: Project) {

    loadProperties(project)

    val configManagers = configManagers(project)

    configManagers.forEach { it.configure() }

    project.afterEvaluate {
      configManagers.forEach { it.afterEvaluate() }
    }
  }

  private fun loadProperties(project: Project) {
    val properties = Properties()
    try { properties.load(FileInputStream(project.file("gradle.properties"))) }
    catch (ex: Exception) { project.logger.info("No gradle.properties file found, using defaults")}

    project.extensions.extraProperties[IS_KOTLIN_PROJECT.name] = properties.getProperty(IS_KOTLIN_PROJECT.name)?.equals("true") ?: IS_KOTLIN_PROJECT.defaultValue
  }

  private fun configManagers(project: Project): List<ConfigManager> {
    val managers = mutableListOf(
        BaseConfigManager(project),
        AppInsightsConfigManager(project),
        PluginManager.from(::SpringBootPluginManager, project),
        PluginManager.from(::DependencyManagementPluginManager, project),
        PluginManager.from(::DependencyCheckPluginManager, project),
        PluginManager.from(::VersionsPluginManager, project),
        PluginManager.from(::GitPropertiesPluginManager, project),
        PluginManager.from(::UseLatestVersionsPluginManager, project),
        PluginManager.from(::TestLoggerPluginManager, project)
    )
    if (project.extensions.extraProperties[IS_KOTLIN_PROJECT.name] as Boolean) {
      managers.add(PluginManager.from(::KotlinPluginManager, project))
    } else {
      managers.add(PluginManager.from(::JavaPluginManager, project))
    }
    return managers.toList()
  }
}
