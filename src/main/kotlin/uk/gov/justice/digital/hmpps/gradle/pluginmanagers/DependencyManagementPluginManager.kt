package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementConfigurer
import org.gradle.api.Project
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import uk.gov.justice.digital.hmpps.gradle.PluginManager

class DependencyManagementPluginManager(override val project: Project) : PluginManager<DependencyManagementPlugin> {

  override fun configure() {
    applyDependencyManagementBom(project)
    //  Overriding Jackson databind version due to CVE-2020-36518 - possibly remove when Spring Boot upgrades to 2.6.6 (along with tests in functional/DependencyManagementPluginManagerTest)
    project.extensions.extraProperties["jackson.version.databind"] = "2.13.2.2"
  }

  private fun applyDependencyManagementBom(project: Project) {
    val depManConfigurer = project.extensions.getByName("dependencyManagement") as DependencyManagementConfigurer
    depManConfigurer.imports {
      it.mavenBom(SpringBootPlugin.BOM_COORDINATES)
    }
  }
}
