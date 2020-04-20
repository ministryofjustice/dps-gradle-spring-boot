package uk.gov.justice.digital.hmpps.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.springframework.boot.gradle.plugin.SpringBootPlugin

class DpsSpringBootPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.plugins.apply(SpringBootPlugin::class.java)
    project.repositories.apply {
      mavenLocal()
      mavenCentral()
    }
    project.group = "uk.gov.justice.digital.hmpps"
  }
}