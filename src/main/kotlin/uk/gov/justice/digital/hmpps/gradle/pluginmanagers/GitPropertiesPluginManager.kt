package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import com.gorylenko.GitPropertiesPlugin
import org.gradle.api.Project

class GitPropertiesPluginManager(override val project: Project) : PluginManager<GitPropertiesPlugin>