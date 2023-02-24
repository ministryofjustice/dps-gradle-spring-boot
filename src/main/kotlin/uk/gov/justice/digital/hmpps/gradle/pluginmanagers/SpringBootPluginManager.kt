package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.gradle.api.Project
import org.springframework.boot.gradle.dsl.SpringBootExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.tasks.bundling.BootJar
import uk.gov.justice.digital.hmpps.gradle.PluginManager
import java.net.InetAddress

class SpringBootPluginManager(override val project: Project) : PluginManager<SpringBootPlugin> {

  override fun configure() {
    addDependencies()
    setSpringBootInfo()
    setManifestAttributes()
    disableJarTask()
  }

  override fun afterEvaluate() {
    setMacArm64NettyVersionWhenRequired()
  }

  private fun addDependencies() {
    project.dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-web")
    project.dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-actuator")
    project.dependencies.add("implementation", "org.springframework.boot:spring-boot-starter-validation")

    project.dependencies.add("testImplementation", "org.springframework.boot:spring-boot-starter-test")
  }

  private fun setSpringBootInfo() {
    val sbExtension = project.extensions.getByName("springBoot") as SpringBootExtension
    sbExtension.buildInfo { buildInfo ->
      buildInfo.properties { buildInfoProps ->
        buildInfoProps.additional.set(getAdditionalBuildInfo())
        buildInfoProps.version.set(project.version.toString())
      }
    }
  }

  private fun getAdditionalBuildInfo(): Map<String, String> {
    return mapOf(
      "by" to System.getProperty("user.name"),
      "operatingSystem" to "${System.getProperty("os.name")} (${System.getProperty("os.version")})",
      "machine" to InetAddress.getLocalHost().hostName,
    )
  }

  private fun disableJarTask() {
    project.tasks.getByName("jar").enabled = false
  }

  private fun setManifestAttributes() {
    val manifest = (project.tasks.getByName("bootJar") as BootJar).manifest
    manifest.attributes["Implementation-Version"] = project.version
    manifest.attributes["Implementation-Title"] = project.name
  }

  private fun setMacArm64NettyVersionWhenRequired() {
    // reactor-netty only depends on x86_64 macos (since it is currently more common),
    // those running locally on Apple Silicon Mac requires the arm64 version
    // so if WebFlux is used and current platform is aarch_64 (Apple Silicon arm64) add additional runtime dependency
    project.configurations.findByName("implementation")?.dependencies?.find { it.group == "org.springframework.boot" && it.name == "spring-boot-starter-webflux" }
      ?.takeIf { isBuildingOnMacAppleSiliconArchitecture() }
      ?.run { project.dependencies.add("runtimeOnly", "io.netty:netty-resolver-dns-native-macos::osx-aarch_64") }
  }

  private fun isBuildingOnMacAppleSiliconArchitecture() =
    System.getProperty("os.arch").contains("aarch64")
}
