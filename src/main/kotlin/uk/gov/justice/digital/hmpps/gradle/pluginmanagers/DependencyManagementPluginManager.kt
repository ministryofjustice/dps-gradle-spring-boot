package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementConfigurer
import org.gradle.api.Project
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import uk.gov.justice.digital.hmpps.gradle.PluginManager

class DependencyManagementPluginManager(override val project: Project) : PluginManager<DependencyManagementPlugin> {

  override fun configure() {
    applyDependencyManagementBom(project)
    //  Overriding log4j 2.14.1 due to CVE-2021-44228 - possible remove when Spring Boot upgrades to > 2.6.2 (along with tests in functional/DependencyManagementPluginManagerTest)
    project.extensions.extraProperties["log4j2.version"] = "2.16.0"
    //  Overriding netty 4.1.69 due to CVE-2021-43797 - possible remove when Spring Boot upgrades to > 2.6.2 (along with tests in functional/DependencyManagementPluginManagerTest)
    project.extensions.extraProperties["netty.version"] = "4.1.72.Final"
  }

  private fun applyDependencyManagementBom(project: Project) {
    val depManConfigurer = project.extensions.getByName("dependencyManagement") as DependencyManagementConfigurer
    depManConfigurer.imports {
      it.mavenBom(SpringBootPlugin.BOM_COORDINATES)
    }
  }
}
