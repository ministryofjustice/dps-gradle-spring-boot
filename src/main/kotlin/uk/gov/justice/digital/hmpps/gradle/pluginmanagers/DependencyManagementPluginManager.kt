package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementConfigurer
import org.gradle.api.Project
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import uk.gov.justice.digital.hmpps.gradle.PluginManager

class DependencyManagementPluginManager(override val project: Project) : PluginManager<DependencyManagementPlugin> {

  override fun configure() {
    applyDependencyManagementBom(project)
    forceTransitiveVersions(project)
  }

  private fun applyDependencyManagementBom(project: Project) {
    val depManConfigurer = project.extensions.getByName("dependencyManagement") as DependencyManagementConfigurer
    depManConfigurer.imports {
      it.mavenBom(SpringBootPlugin.BOM_COORDINATES)
    }
  }

  private fun forceTransitiveVersions(project: Project) {
    val depManConfigurer = project.extensions.getByName("dependencyManagement") as DependencyManagementConfigurer
    depManConfigurer.dependencies {
      // Overriding 0.9.8.RELEASE introduced by Spring Boot 2.3.1 - remove when Spring Boot upgrades to > 0.9.8.RELEASE (along with tests in functional/DependencyManagementPluginManagerTest)
      it.dependency("io.projectreactor.netty:reactor-netty:0.9.9.RELEASE")
      // Overriding 5.4.17 included by Spring Boot 2.3.1 - remove when Spring Boot upgrades to > 5.4.17.Final (along with tests in functional/DependencyManagementPluginManagerTest)
      it.dependency("org.hibernate:hibernate-core:5.4.18.Final")
    }
  }

}
