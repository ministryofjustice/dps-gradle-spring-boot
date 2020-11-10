package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import com.github.benmanes.gradle.versions.VersionsPlugin
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.gradle.UnitTest

class VersionsPluginManagerTest : UnitTest() {

  @Test
  fun `Should apply the versions plugin`() {
    project.plugins.getPlugin(VersionsPlugin::class.java)
  }
}
