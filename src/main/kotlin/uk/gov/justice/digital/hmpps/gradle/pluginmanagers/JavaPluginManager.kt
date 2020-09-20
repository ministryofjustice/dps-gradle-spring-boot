package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import uk.gov.justice.digital.hmpps.gradle.PluginManager

class JavaPluginManager(override val project: Project) : PluginManager<JavaLibraryPlugin> {

  override fun configure() {}

}
