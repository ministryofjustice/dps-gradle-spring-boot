package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.gradle.api.Project
import org.gradle.api.Task
import org.jmailen.gradle.kotlinter.KotlinterPlugin
import uk.gov.justice.digital.hmpps.gradle.PluginManager

class KotlinterPluginManager(override val project: Project) : PluginManager<KotlinterPlugin> {
  override fun configure() {
    project.getTasksByName("check", false).forEach {
      it.dependsOn("${getProjectPrefix(it)}:installKotlinterPrePushHook")
    }

    // TODO delete pre-commit hook of old plugin if present

    copyResourcesFile(".editorconfig")
  }

  private fun getProjectPrefix(it: Task) = if (it.path.contains(":")) {
    it.path.substringBeforeLast(":")
  } else {
    ""
  }
}
