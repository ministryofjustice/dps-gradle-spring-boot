package uk.gov.justice.digital.hmpps.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import uk.gov.justice.digital.hmpps.gradle.configmanagers.AppInsightsConfigManager
import uk.gov.justice.digital.hmpps.gradle.configmanagers.BaseConfigManager
import uk.gov.justice.digital.hmpps.gradle.configmanagers.ConfigManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.DependencyCheckPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.DependencyManagementPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.GitPropertiesPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.KotlinPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.PluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.SpringBootPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.VersionsPluginManager

class DpsSpringBootPlugin : Plugin<Project> {

  override fun apply(project: Project) {

    val configManagers = configManagers(project)
    val pluginManagers = pluginManagers(project)

    configManagers.forEach { it.configure() }
    pluginManagers.forEach { it.configure() }

    project.afterEvaluate {
      pluginManagers.forEach { it.afterEvaluate() }
    }
  }

  private fun configManagers(project: Project): List<ConfigManager> {
    return listOf(
        BaseConfigManager(project),
        AppInsightsConfigManager(project)
    )
  }

  private fun pluginManagers(project: Project): List<PluginManager<out Plugin<Project>>> {
    return listOf(
        PluginManager.from(::SpringBootPluginManager, project),
        PluginManager.from(::KotlinPluginManager, project),
        PluginManager.from(::DependencyManagementPluginManager, project),
        PluginManager.from(::DependencyCheckPluginManager, project),
        PluginManager.from(::VersionsPluginManager, project),
        PluginManager.from(::GitPropertiesPluginManager, project)
    )
  }


}
