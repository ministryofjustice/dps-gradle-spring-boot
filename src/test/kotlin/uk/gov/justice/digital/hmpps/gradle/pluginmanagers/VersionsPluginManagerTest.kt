package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import com.github.benmanes.gradle.versions.VersionsPlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class VersionsPluginManagerTest {

  val project: Project = ProjectBuilder.builder().build()

  @BeforeEach
  fun `Create project`() {
    project.pluginManager.apply("uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot")
  }

  @Test
  fun `Should apply the versions plugin`() {
    project.plugins.getPlugin(VersionsPlugin::class.java)
  }
}