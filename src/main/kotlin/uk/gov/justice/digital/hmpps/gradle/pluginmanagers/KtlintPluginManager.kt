package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.gradle.api.Project
import org.gradle.api.Task
import org.jlleitschuh.gradle.ktlint.KtlintPlugin
import uk.gov.justice.digital.hmpps.gradle.PluginManager

class KtlintPluginManager(override val project: Project) : PluginManager<KtlintPlugin> {
  override fun configure() {
    project.getTasksByName("check", false).forEach {
      it.dependsOn("${getProjectPrefix(it)}:ktlintCheck")
    }

    copyResourcesFile(".editorconfig")
  }

  private fun getProjectPrefix(it: Task) = if (it.path.contains(":")) {
    it.path.substringBeforeLast(":")
  } else {
    ""
  }
}
