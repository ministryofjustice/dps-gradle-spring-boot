package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.gradle.api.Project
import org.owasp.dependencycheck.gradle.DependencyCheckPlugin
import org.owasp.dependencycheck.gradle.extension.DependencyCheckExtension
import org.owasp.dependencycheck.reporting.ReportGenerator
import uk.gov.justice.digital.hmpps.gradle.PluginManager
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

internal const val DEPENDENCY_SUPPRESSION_FILENAME = "dps-gradle-spring-boot-suppressions.xml"

class DependencyCheckPluginManager(override val project: Project) : PluginManager<DependencyCheckPlugin> {

  override fun configure() {
    setDependencyCheckConfig()
    addDependencyCheckSuppressionFile()
  }

  override fun afterEvaluate() {
    checkOverriddenSuppressionFile()
  }

  private fun setDependencyCheckConfig() {
    val extension = project.extensions.getByName("dependencyCheck") as DependencyCheckExtension
    extension.failBuildOnCVSS = 5f
    extension.suppressionFiles.add(DEPENDENCY_SUPPRESSION_FILENAME)
    extension.format = ReportGenerator.Format.ALL
    extension.analyzers.assemblyEnabled = false
  }

  private fun addDependencyCheckSuppressionFile() {
    val inputStream = javaClass.classLoader.getResourceAsStream(DEPENDENCY_SUPPRESSION_FILENAME)
    val newFile = Paths.get(project.projectDir.absolutePath + "/$DEPENDENCY_SUPPRESSION_FILENAME")
    Files.copy(inputStream, newFile, StandardCopyOption.REPLACE_EXISTING)
  }

  private fun checkOverriddenSuppressionFile() {
    val extension = project.extensions.getByName("dependencyCheck") as DependencyCheckExtension
    if (extension.suppressionFiles.isNotEmpty() && extension.suppressionFiles.contains(DEPENDENCY_SUPPRESSION_FILENAME).not()) {
      project.logger.warn(
        """
        
        INFO: The default dependency checker suppression file has not been applied. Did you accidentally set suppressionFiles = listOf("<file>") instead of suppressionFiles.add("<file>") in your Gradle build script?

        """.trimIndent()
      )
    }
  }
}
