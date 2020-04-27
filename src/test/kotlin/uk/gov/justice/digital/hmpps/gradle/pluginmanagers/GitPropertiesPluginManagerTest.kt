package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import com.gorylenko.GitPropertiesPlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GitPropertiesPluginManagerTest {

  private val project: Project = ProjectBuilder.builder().build()

  @BeforeEach
  fun `Create project`() {
    project.pluginManager.apply("uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot")
  }

  @Test
  fun `Should apply the gradle git properties plugin`() {
    project.plugins.getPlugin(GitPropertiesPlugin::class.java)
  }

}