package uk.gov.justice.digital.hmpps.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

interface PluginManager<T : Plugin<Project>> : ConfigManager {

  companion object {
    inline fun <reified T : Plugin<Project>> from(
      factory: (project: Project) -> PluginManager<T>,
      project: Project
    ): PluginManager<T> {
      val manager: PluginManager<T> = factory(project)
      project.plugins.apply(T::class.java)
      return manager
    }
  }
}
