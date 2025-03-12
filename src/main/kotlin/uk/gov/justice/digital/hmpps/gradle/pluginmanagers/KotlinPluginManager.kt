package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import uk.gov.justice.digital.hmpps.gradle.PluginManager

class KotlinPluginManager(override val project: Project) : PluginManager {
  override val pluginProject = KotlinPluginWrapper::class.java

  override fun configure() {
    addDependencies()
    setKotlinCompileJvmVersion()
  }

  private fun setKotlinCompileJvmVersion() {
    project.tasks.withType(KotlinCompile::class.java).forEach {
      it.compilerOptions { JvmTarget.JVM_21 }
    }
  }

  private fun addDependencies() {
    project.dependencies.add("implementation", "com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
    project.dependencies.add("implementation", "org.jetbrains.kotlin:kotlin-reflect")

    // Temporarily bump swagger-ui to 5.20.0 for CVE-2025-26791
    project.dependencies.constraints.add("implementation", "org.webjars:swagger-ui:5.20.0")

    project.dependencies.add("testImplementation", "org.mockito.kotlin:mockito-kotlin:5.4.0")
  }
}
