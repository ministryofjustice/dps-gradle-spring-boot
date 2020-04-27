package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import com.gorylenko.GitPropertiesPlugin
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.gradle.UnitTest

class GitPropertiesPluginManagerTest : UnitTest() {

  @Test
  fun `Should apply the gradle git properties plugin`() {
    project.plugins.getPlugin(GitPropertiesPlugin::class.java)
  }

}