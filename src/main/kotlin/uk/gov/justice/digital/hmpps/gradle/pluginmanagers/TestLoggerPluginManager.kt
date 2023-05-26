package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import com.adarshr.gradle.testlogger.TestLoggerPlugin
import org.gradle.api.Project
import uk.gov.justice.digital.hmpps.gradle.PluginManager

class TestLoggerPluginManager(override val project: Project) : PluginManager {
  override val pluginProject = TestLoggerPlugin::class.java
}