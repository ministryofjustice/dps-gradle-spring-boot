package uk.gov.justice.digital.hmpps.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.tasks.Copy
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.DependencyCheckPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.DependencyManagementPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.GitPropertiesPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.KotlinPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.SpringBootPluginManager
import uk.gov.justice.digital.hmpps.gradle.pluginmanagers.VersionsPluginManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DpsSpringBootPlugin : Plugin<Project> {

  override fun apply(project: Project) {

    val pluginManagers = pluginManagers(project)

    project.group = "uk.gov.justice.digital.hmpps"
    project.version = getVersion()

    applyRepositories(project)
    addAgentDepsConfiguration(project)
    createCopyAgentTask(project)

    pluginManagers.forEach { it.configurePlugin() }

    addDependencies(project)

    project.afterEvaluate {
      pluginManagers.forEach { it.afterEvaluate() }
    }
  }

  private fun pluginManagers(project: Project): List<PluginManager<out Plugin<Project>>> {
    return listOf(
        PluginManager.from(::SpringBootPluginManager, project),
        PluginManager.from(::KotlinPluginManager, project),
        PluginManager.from(::DependencyManagementPluginManager, project),
        PluginManager.from(::DependencyCheckPluginManager, project),
        PluginManager.from(::VersionsPluginManager, project),
        PluginManager.from(::GitPropertiesPluginManager, project)
    )
  }

  private fun getVersion(): String {
    return if (System.getenv().contains("BUILD_NUMBER")) System.getenv("BUILD_NUMBER")
    else LocalDate.now().format(DateTimeFormatter.ISO_DATE)
  }

  private fun applyRepositories(project: Project) {
    project.repositories.apply {
      mavenLocal()
      mavenCentral()
    }
  }

  private fun addDependencies(project: Project) {
    project.dependencies.add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    project.dependencies.add("implementation", "org.jetbrains.kotlin:kotlin-reflect")

    project.dependencies.add("implementation", "net.logstash.logback:logstash-logback-encoder:6.3")
    project.dependencies.add("implementation", "com.microsoft.azure:applicationinsights-spring-boot-starter:2.6.0")
    project.dependencies.add("implementation", "com.microsoft.azure:applicationinsights-logging-logback:2.6.0")

    project.dependencies.add("implementation", "com.github.timpeeters:spring-boot-graceful-shutdown:2.2.1")
    project.dependencies.add("implementation", "com.fasterxml.jackson.module:jackson-module-kotlin")
    project.dependencies.add("implementation", "com.google.guava:guava:29.0-jre") // This is only required because the version pulled in a as transitive dependency has CVE vulnerabilities

    val springBootTest = project.dependencies.add("testImplementation", "org.springframework.boot:spring-boot-starter-test") as ExternalModuleDependency
    springBootTest.exclude(mapOf("group" to "org.junit.vintage", "module" to "junit-vintage-engine"))
    project.dependencies.add("testImplementation", "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")

    project.dependencies.add("agentDeps", "com.microsoft.azure:applicationinsights-agent:2.6.0")
  }


  private fun addAgentDepsConfiguration(project: Project) {
    project.configurations.create("agentDeps").isTransitive = false
  }

  private fun createCopyAgentTask(project: Project) {
    val copyAgentTask = project.tasks.register("copyAgent", Copy::class.java) {
      it.from(project.configurations.getByName("agentDeps"))
      it.into("${project.buildDir}/libs")
    }
    project.tasks.getByName("assemble").dependsOn(copyAgentTask)
  }

}
