package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.gradle.api.JavaVersion.VERSION_11
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import uk.gov.justice.digital.hmpps.gradle.PluginManager

class JavaPluginManager(override val project: Project) : PluginManager<JavaLibraryPlugin> {

  override fun configure() {
    setJavaVersion()
  }

  private fun setJavaVersion() {
    val java = project.extensions.getByName("java") as JavaPluginExtension
    java.sourceCompatibility = VERSION_11
    java.targetCompatibility = VERSION_11
  }

}
