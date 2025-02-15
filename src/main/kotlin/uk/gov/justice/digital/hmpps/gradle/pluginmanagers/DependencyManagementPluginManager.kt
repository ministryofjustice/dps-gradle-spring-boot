package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementConfigurer
import org.gradle.api.Project
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import uk.gov.justice.digital.hmpps.gradle.PluginManager
import uk.gov.justice.digital.hmpps.gradle.configmanagers.OPENTELEMETRY_VERSION

class DependencyManagementPluginManager(override val project: Project) : PluginManager {

  override val pluginProject = DependencyManagementPlugin::class.java

  override fun configure() {
    applyDependencyManagementBom(project)
    project.extensions.extraProperties["opentelemetry.version"] = OPENTELEMETRY_VERSION

    // pinning netty due to CVE-2025-24970
    project.extensions.extraProperties["netty.version"] = "4.1.118.Final"

    // pinning netty due to CVE-2024-57699
    project.extensions.extraProperties["json-smart.version"] = "2.5.2"
  }

  private fun applyDependencyManagementBom(project: Project) {
    val depManConfigurer = project.extensions.getByName("dependencyManagement") as DependencyManagementConfigurer
    depManConfigurer.imports {
      it.mavenBom(SpringBootPlugin.BOM_COORDINATES)
    }
  }
}
