package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import uk.gov.justice.digital.hmpps.gradle.PluginManager

class KotlinPluginManager(override val project: Project) : PluginManager<KotlinPluginWrapper> {

  override fun configure() {
    addDependencies()
    setKotlinCompileJvmVersion()
  }

  private fun setKotlinCompileJvmVersion() {
    project.tasks.withType(KotlinCompile::class.java).forEach {
      it.kotlinOptions {
        jvmTarget = "11"
      }
    }
  }

  private fun addDependencies() {
    project.dependencies.add("implementation", "com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    project.dependencies.add("implementation", "org.jetbrains.kotlin:kotlin-reflect")

    project.dependencies.add("testImplementation", "org.mockito.kotlin:mockito-kotlin:4.0.0")
  }
}
