package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.gradle.api.Project
import se.patrikerdes.UseLatestVersionsPlugin
import uk.gov.justice.digital.hmpps.gradle.PluginManager

class UseLatestVersionsPluginManager(override val project: Project) : PluginManager<UseLatestVersionsPlugin>
