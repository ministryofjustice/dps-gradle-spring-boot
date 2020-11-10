package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import com.adarshr.gradle.testlogger.TestLoggerPlugin
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.gradle.UnitTest

class TestLoggerPluginManagerTest : UnitTest() {

  @Test
  fun `Should apply the gradle git properties plugin`() {
    project.plugins.getPlugin(TestLoggerPlugin::class.java)
  }
}
