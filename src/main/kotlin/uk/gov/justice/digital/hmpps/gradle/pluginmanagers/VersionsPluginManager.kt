package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import com.github.benmanes.gradle.versions.VersionsPlugin
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.Project

class VersionsPluginManager(override val project: Project) : PluginManager<VersionsPlugin> {

  override fun configurePlugin() {
    rejectUnstableDependencyUpdates()
  }

  private fun rejectUnstableDependencyUpdates() {
    project.tasks.withType(DependencyUpdatesTask::class.java).forEach { task ->
      task.rejectVersionIf { selection ->
        isUnstable(selection.candidate.version) && isStable(selection.currentVersion)
      }
    }
  }

  private fun isStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    return stableKeyword || regex.matches(version)
  }

  private fun isUnstable(version: String): Boolean {
    return isStable(version).not()
  }
}