package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementConfigurer
import org.gradle.api.Project
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import uk.gov.justice.digital.hmpps.gradle.PluginManager

class DependencyManagementPluginManager(override val project: Project) : PluginManager<DependencyManagementPlugin> {

  override fun configure() {
    applyDependencyManagementBom(project)
    // overriding snakeyaml for https://nvd.nist.gov/vuln/detail/CVE-2022-25857 - brought in by org.springframework.boot:spring-boot-dependencies:2.7.2
    // overriding snakeyaml for https://nvd.nist.gov/vuln/detail/CVE-2022-38751 - brought in by org.springframework.boot:spring-boot-dependencies:2.7.2
    project.extensions.extraProperties["snakeyaml.version"] = "1.32"

  }

  private fun applyDependencyManagementBom(project: Project) {
    val depManConfigurer = project.extensions.getByName("dependencyManagement") as DependencyManagementConfigurer
    depManConfigurer.imports {
      it.mavenBom(SpringBootPlugin.BOM_COORDINATES)
    }
  }
}
