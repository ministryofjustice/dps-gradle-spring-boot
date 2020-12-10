package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementConfigurer
import org.gradle.api.Project
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import uk.gov.justice.digital.hmpps.gradle.PluginManager

class DependencyManagementPluginManager(override val project: Project) : PluginManager<DependencyManagementPlugin> {

  override fun configure() {
    applyDependencyManagementBom(project)
    forceVersions(project)
  }

  // TODO Remove these forced versions once they are pulled in automatically
  private fun forceVersions(project: Project) {
    project.extensions.extraProperties["tomcat.version"] = "9.0.40" // Required to avoid version 9.0.39 introduced by spring-boot-starter-web 2.4.0 (CVE-2020-17527)
    project.extensions.extraProperties["hibernate.version"] = "5.4.24.Final" // Required to avoid 5.4.23.Final introduced by spring-boot-starter-data-jpa 2.4.0 (CVE-2020-25638)
  }

  private fun applyDependencyManagementBom(project: Project) {
    val depManConfigurer = project.extensions.getByName("dependencyManagement") as DependencyManagementConfigurer
    depManConfigurer.imports {
      it.mavenBom(SpringBootPlugin.BOM_COORDINATES)
    }
  }
}
