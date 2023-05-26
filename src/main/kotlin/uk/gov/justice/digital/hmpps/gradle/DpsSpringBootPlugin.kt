package uk.gov.justice.digital.hmpps.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import uk.gov.justice.digital.hmpps.gradle.configmanagers.AppInsightsConfigManager
import uk.gov.justice.digital.hmpps.gradle.configmanagers.BaseConfigManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.DependencyCheckPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.DependencyManagementPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.GitPropertiesPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.KotlinPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.KtlintPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.SpringBootPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.TestLoggerPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.UseLatestVersionsPluginManager
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
    val configManagers = listOf(
      BaseConfigManager(project),
      AppInsightsConfigManager(project),
    )
    val pluginManagers = listOf(
      SpringBootPluginManager(project),
      KotlinPluginManager(project),
      DependencyManagementPluginManager(project),
      DependencyCheckPluginManager(project),
      VersionsPluginManager(project),
      GitPropertiesPluginManager(project),
      UseLatestVersionsPluginManager(project),
      TestLoggerPluginManager(project),
      KtlintPluginManager(project),
    )
    pluginManagers.forEach { project.plugins.apply(it.pluginProject) }
    return configManagers + pluginManagers
  }
}
