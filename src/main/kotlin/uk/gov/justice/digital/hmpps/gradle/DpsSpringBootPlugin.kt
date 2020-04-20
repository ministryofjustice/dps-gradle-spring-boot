package uk.gov.justice.digital.hmpps.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin

class DpsSpringBootPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.plugins.apply(SpringBootPlugin::class.java)
    project.plugins.apply(KotlinPluginWrapper::class.java)
    project.repositories.apply {
      mavenLocal()
      mavenCentral()
    }
    project.group = "uk.gov.justice.digital.hmpps"
    project.dependencies.add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    project.dependencies.add("implementation", "org.jetbrains.kotlin:kotlin-reflect")
    project.dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-web:2.2.6.RELEASE")
    project.dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-actuator:2.2.6.RELEASE")
    project.tasks.withType(KotlinCompile::class.java).forEach {
      it.kotlinOptions{
        jvmTarget = "11"
      }
    }
  }
}