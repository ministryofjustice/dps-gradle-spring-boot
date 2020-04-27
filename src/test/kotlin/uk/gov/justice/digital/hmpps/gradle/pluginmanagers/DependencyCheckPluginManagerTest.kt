package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.assertj.core.api.Assertions
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.owasp.dependencycheck.gradle.DependencyCheckPlugin
import org.owasp.dependencycheck.gradle.extension.DependencyCheckExtension
import org.owasp.dependencycheck.reporting.ReportGenerator

class DependencyCheckPluginManagerTest {

  private val project: Project = ProjectBuilder.builder().build()

  @BeforeEach
  fun `Create project`() {
    project.pluginManager.apply("uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot")
  }

  @Test
  fun `Should apply the Owasp Dependency check plugin`() {
    project.plugins.getPlugin(DependencyCheckPlugin::class.java)
  }

  @Test
  fun `Should apply Owasp dependency check configuration`() {
    val extension = project.extensions.getByName("dependencyCheck") as DependencyCheckExtension
    Assertions.assertThat(extension.failBuildOnCVSS).isEqualTo(5f)
    Assertions.assertThat(extension.suppressionFiles).containsExactly(DEPENDENCY_SUPPRESSION_FILENAME)
    Assertions.assertThat(extension.format).isEqualTo(ReportGenerator.Format.ALL)
    Assertions.assertThat(extension.analyzers.assemblyEnabled).isFalse()
  }

}