package uk.gov.justice.digital.hmpps.gradle

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementConfigurer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin

class DpsSpringBootPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.group = "uk.gov.justice.digital.hmpps"
    applyPlugins(project)
    applyRepositories(project)
    applyDependencyManagementBom(project)
    addDependencies(project)
    setKotlinCompileJvmVersion(project)
  }

  private fun applyPlugins(project: Project) {
    project.plugins.apply(SpringBootPlugin::class.java)
    project.plugins.apply(KotlinPluginWrapper::class.java)
    project.plugins.apply(DependencyManagementPlugin::class.java)
  }

  private fun applyRepositories(project: Project) {
    project.repositories.apply {
      mavenLocal()
      mavenCentral()
    }
  }

  private fun setKotlinCompileJvmVersion(project: Project) {
    project.tasks.withType(KotlinCompile::class.java).forEach {
      it.kotlinOptions {
        jvmTarget = "11"
      }
    }
  }

  private fun applyDependencyManagementBom(project: Project) {
    val depManConfigurer = project.extensions.getByName("dependencyManagement") as DependencyManagementConfigurer
    depManConfigurer.imports {
      it.mavenBom(SpringBootPlugin.BOM_COORDINATES)
    }
  }

  private fun addDependencies(project: Project) {
    project.dependencies.add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    project.dependencies.add("implementation", "org.jetbrains.kotlin:kotlin-reflect")
    project.dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-web")
    project.dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-actuator")
  }
}