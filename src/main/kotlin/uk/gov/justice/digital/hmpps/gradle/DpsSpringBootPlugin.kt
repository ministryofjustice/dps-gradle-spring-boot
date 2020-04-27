package uk.gov.justice.digital.hmpps.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import uk.gov.justice.digital.hmpps.gradle.configmanagers.AppInsightsConfigManager
import uk.gov.justice.digital.hmpps.gradle.configmanagers.BaseConfigManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.DependencyCheckPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.DependencyManagementPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.GitPropertiesPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.KotlinPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.SpringBootPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.VersionsPluginManager

class DpsSpringBootPlugin : Plugin<Project> {

  override fun apply(project: Project) {

    val configManagers = configManagers(project)

    configManagers.forEach { it.configure() }

    project.afterEvaluate {
      configManagers.forEach { it.afterEvaluate() }
    }
  }

  private fun configManagers(project: Project): List<ConfigManager> {
    return listOf(
        BaseConfigManager(project),
        AppInsightsConfigManager(project),
        PluginManager.from(::SpringBootPluginManager, project),
        PluginManager.from(::KotlinPluginManager, project),
        PluginManager.from(::DependencyManagementPluginManager, project),
        PluginManager.from(::DependencyCheckPluginManager, project),
        PluginManager.from(::VersionsPluginManager, project),
        PluginManager.from(::GitPropertiesPluginManager, project)
    )
  }

}
