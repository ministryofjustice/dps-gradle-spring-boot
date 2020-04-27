package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.springframework.boot.gradle.dsl.SpringBootExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.net.InetAddress
import java.time.Instant

class SpringBootPluginManager(override val project: Project) : PluginManager<SpringBootPlugin> {

  override fun configurePlugin() {
    addDependencies()
    setSpringBootInfo()
    setManifestAttributes()
  }

  private fun addDependencies() {
    project.dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-web")
    project.dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-actuator")
    project.dependencies.add("implementation", "com.github.timpeeters:spring-boot-graceful-shutdown:2.2.1")

    val springBootTest = project.dependencies.add("testImplementation", "org.springframework.boot:spring-boot-starter-test") as ExternalModuleDependency
    springBootTest.exclude(mapOf("group" to "org.junit.vintage", "module" to "junit-vintage-engine"))
  }

  private fun setSpringBootInfo() {
    val sbExtension = project.extensions.getByName("springBoot") as SpringBootExtension
    sbExtension.buildInfo { buildInfo ->
      buildInfo.properties { buildInfoProps ->
        buildInfoProps.time = Instant.now()
        buildInfoProps.additional = getAdditionalBuildInfo()
        buildInfoProps.version = project.version.toString()
      }
    }
  }

  private fun getAdditionalBuildInfo(): Map<String, String> {
    return mapOf(
        "by" to System.getProperty("user.name"),
        "operatingSystem" to "${System.getProperty("os.name")} (${System.getProperty("os.version")})",
        "machine" to InetAddress.getLocalHost().hostName)
  }

  private fun setManifestAttributes() {
    val manifest = (project.tasks.getByName("bootJar") as BootJar).manifest
    manifest.attributes["Implementation-Version"] = project.version
    manifest.attributes["Implementation-Title"] = project.name
  }

}