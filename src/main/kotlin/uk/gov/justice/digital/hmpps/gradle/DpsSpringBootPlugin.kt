package uk.gov.justice.digital.hmpps.gradle

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
    addDependencies(project)
    setKotlinCompileJvmVersion(project)
  }

  private fun applyPlugins(project: Project) {
    project.plugins.apply(SpringBootPlugin::class.java)
    project.plugins.apply(KotlinPluginWrapper::class.java)
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

  private fun addDependencies(project: Project) {
    project.dependencies.add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    project.dependencies.add("implementation", "org.jetbrains.kotlin:kotlin-reflect")
    project.dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-web:2.2.6.RELEASE")
    project.dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-actuator:2.2.6.RELEASE")
  }
}