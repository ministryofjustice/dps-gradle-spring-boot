package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.gradle.api.Project
import org.springframework.boot.gradle.dsl.SpringBootExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.tasks.bundling.BootJar
import uk.gov.justice.digital.hmpps.gradle.PluginManager
import java.net.InetAddress
import java.time.Instant

class SpringBootPluginManager(override val project: Project) : PluginManager<SpringBootPlugin> {

  override fun configure() {
    addDependencies()
    addDependencyConstraints()
    setSpringBootInfo()
    setManifestAttributes()
    disableJarTask()
  }

  override fun afterEvaluate(): Unit = setMacArm64NettyVersionWhenRequired()

  private fun addDependencies(): Unit = with(project.dependencies) {
    add("implementation", "org.springframework.boot:spring-boot-starter-web")
    add("implementation", "org.springframework.boot:spring-boot-starter-actuator")
    add("implementation", "org.springframework.boot:spring-boot-starter-validation")
    add("implementation", "com.github.timpeeters:spring-boot-graceful-shutdown:2.2.2")

    add("testImplementation", "org.springframework.boot:spring-boot-starter-test")
  }

  // json-smart is shaded in nimbus-jose-jwt so therefore need to add dependency constraints on that version
  // rather than on json-smart itself to avoid CVE-2023-1370
  private fun addDependencyConstraints(): Unit = with(project.dependencies.constraints) {
    add("implementation", "com.nimbusds:oauth2-oidc-sdk:9.43.1")
    add("implementation", "com.nimbusds:nimbus-jose-jwt:9.24.4")
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
