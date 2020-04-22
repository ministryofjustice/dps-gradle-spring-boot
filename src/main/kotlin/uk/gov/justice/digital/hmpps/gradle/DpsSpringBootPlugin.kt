package uk.gov.justice.digital.hmpps.gradle

import com.github.benmanes.gradle.versions.VersionsPlugin
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementConfigurer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.owasp.dependencycheck.gradle.DependencyCheckPlugin
import org.owasp.dependencycheck.gradle.extension.DependencyCheckExtension
import org.owasp.dependencycheck.reporting.ReportGenerator
import org.springframework.boot.gradle.dsl.SpringBootExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.net.InetAddress
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DpsSpringBootPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.group = "uk.gov.justice.digital.hmpps"
    project.version = getVersion()
    applyPlugins(project)
    applyRepositories(project)
    applyDependencyManagementBom(project)
    setSpringBootInfo(project)
    setManifestAttributes(project)
    setDependencyCheckConfig(project)
    addDependencyCheckSuppressionFile(project)
    rejectUnstableDependencyUpdates(project)
    addDependencies(project)
    setKotlinCompileJvmVersion(project)
    project.afterEvaluate {
      checkOverriddenSuppressionsFile(project)
    }
  }

  private fun getVersion(): String {
    return if (System.getenv().contains("BUILD_NUMBER")) System.getenv("BUILD_NUMBER")
    else LocalDate.now().format(DateTimeFormatter.ISO_DATE)
  }

  private fun applyPlugins(project: Project) {
    project.plugins.apply(SpringBootPlugin::class.java)
    project.plugins.apply(KotlinPluginWrapper::class.java)
    project.plugins.apply(DependencyManagementPlugin::class.java)
    project.plugins.apply(DependencyCheckPlugin::class.java)
    project.plugins.apply(VersionsPlugin::class.java)
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

  private fun setSpringBootInfo(project: Project) {
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

  private fun setManifestAttributes(project: Project) {
    val manifest = (project.tasks.getByName("bootJar") as BootJar).manifest
    manifest.attributes["Implementation-Version"] = project.version
    manifest.attributes["Implementation-Title"] = project.name
  }

  private fun setDependencyCheckConfig(project: Project) {
    val extension = project.extensions.getByName("dependencyCheck") as DependencyCheckExtension
    extension.failBuildOnCVSS = 5f
    extension.suppressionFiles.add(DEPENDENCY_SUPPRESSION_FILENAME)
    extension.format = ReportGenerator.Format.ALL
    extension.analyzers.assemblyEnabled = false
  }

  private fun addDependencyCheckSuppressionFile(project: Project) {
    val inputStream = javaClass.classLoader.getResourceAsStream(DEPENDENCY_SUPPRESSION_FILENAME)
    val newFile = Paths.get(project.projectDir.absolutePath + "/$DEPENDENCY_SUPPRESSION_FILENAME")
    Files.copy(inputStream, newFile, StandardCopyOption.REPLACE_EXISTING)
  }

  private fun rejectUnstableDependencyUpdates(project: Project) {
    project.tasks.withType(DependencyUpdatesTask::class.java).forEach { task ->
      task.rejectVersionIf { selection ->
        isUnstable(selection.candidate.version) && isStable(selection.currentVersion)
      }
    }
  }

  private fun isStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    return stableKeyword || regex.matches(version)
  }

  private fun isUnstable(version: String): Boolean {
    return isStable(version).not()
  }

  private fun checkOverriddenSuppressionsFile(project: Project) {
    val extension = project.extensions.getByName("dependencyCheck") as DependencyCheckExtension
    if (extension.suppressionFiles.contains(DEPENDENCY_SUPPRESSION_FILENAME).not()) {
      project.logger.warn("""
        
        WARNING: The default dependency checker suppression file has not been applied. Did you accidentally set suppressionFiles = "<file>" instead of suppressionFiles.add("<file>") in your Gradle build script?
        
      """.trimIndent()
      )
    }
  }
}