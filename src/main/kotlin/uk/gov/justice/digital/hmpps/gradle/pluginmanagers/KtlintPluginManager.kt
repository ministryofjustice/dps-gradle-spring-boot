package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.gradle.api.Project
import org.gradle.api.Task
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.KtlintPlugin
import uk.gov.justice.digital.hmpps.gradle.PluginManager

// TODO The ktlint version applied by the ktlint plugin needs pinning to 1.4.1 to workaround this issue: https://github.com/JLLeitschuh/ktlint-gradle/issues/809
// TODO Once the issue is solved we should stop pinning the version and take the default version provided by the ktlint plugin
private const val KTLINT_VERSION = "1.4.1"

class KtlintPluginManager(override val project: Project) : PluginManager {

  override val pluginProject = KtlintPlugin::class.java

  override fun configure() {
    project.getTasksByName("check", false).forEach {
      it.dependsOn("${getProjectPrefix(it)}:ktlintCheck")
    }

    (project.extensions.getByName("ktlint") as KtlintExtension).version.set(KTLINT_VERSION)

    copyResourcesFile(".editorconfig")
  }

  private fun getProjectPrefix(it: Task) = if (it.path.contains(":")) {
    it.path.substringBeforeLast(":")
  } else {
    ""
  }
}
