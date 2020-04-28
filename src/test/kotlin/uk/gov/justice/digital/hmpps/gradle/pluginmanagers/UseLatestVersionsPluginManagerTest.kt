package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.junit.jupiter.api.Test
import se.patrikerdes.UseLatestVersionsPlugin
import uk.gov.justice.digital.hmpps.gradle.UnitTest

class UseLatestVersionsPluginManagerTest : UnitTest() {

  @Test
  fun `Should apply the use latest versions plugin`() {
    project.plugins.getPlugin(UseLatestVersionsPlugin::class.java)
  }

}