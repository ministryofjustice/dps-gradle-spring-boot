package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.gradle.UnitTest

class DependencyManagementPluginManagerTest : UnitTest() {

  @Test
  fun `Should apply the Spring Dependency Management plugin`() {
    project.plugins.getPlugin(DependencyManagementPlugin::class.java)
  }


}