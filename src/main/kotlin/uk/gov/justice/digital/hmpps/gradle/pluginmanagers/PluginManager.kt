package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.gradle.api.Plugin
import org.gradle.api.Project

interface PluginManager<T : Plugin<Project>> {

  val project: Project

  fun configurePlugin() {}
  fun afterEvaluate() {}

  companion object {
    inline fun <reified T: Plugin<Project>> from(
        factory: (project: Project) -> PluginManager<T>,
        project: Project
    ): PluginManager<T> {
      val manager: PluginManager<T> = factory(project)
      project.plugins.apply(T::class.java)
      return manager
    }
  }

}
