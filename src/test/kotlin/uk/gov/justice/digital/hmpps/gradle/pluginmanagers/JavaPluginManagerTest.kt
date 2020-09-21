package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.UnknownPluginException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import uk.gov.justice.digital.hmpps.gradle.UnitTest

class JavaPluginManagerTest : UnitTest() {

  @Test
  fun `Should not apply the Java plugin by default`() {
    assertThrows<UnknownPluginException> {  project.plugins.getPlugin(JavaLibraryPlugin::class.java) }
  }

}