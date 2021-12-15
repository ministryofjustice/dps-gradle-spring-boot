package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import uk.gov.justice.digital.hmpps.gradle.functional.GradleBuildTest
import uk.gov.justice.digital.hmpps.gradle.functional.ProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.buildProject
import uk.gov.justice.digital.hmpps.gradle.functional.findJar
import uk.gov.justice.digital.hmpps.gradle.functional.javaProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.kotlinProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.makeProject
import java.util.jar.JarFile

class DependencyManagementPluginManagerNettyTest : GradleBuildTest() {

  companion object {
    @Suppress("unused")
    @JvmStatic
    fun wrongTransitiveNettyVersion() = listOf(
      Arguments.of(javaProjectDetails(projectDir).copy(buildScript = wrongTransitiveNettyVersionBuildFile())),
      Arguments.of(kotlinProjectDetails(projectDir).copy(buildScript = wrongTransitiveNettyVersionBuildFile())),
    )
  }
  private fun jarContainsNettyCommon(jar: JarFile, version: String): Boolean =
    jar.getJarEntry("BOOT-INF/lib/netty-common-$version.jar") != null

  @ParameterizedTest
  @MethodSource("wrongTransitiveNettyVersion")
  fun `Wrong transitive version of spring security should be overridden by the plugin`(projectDetails: ProjectDetails) {
    makeProject(projectDetails.copy())

    val result = buildProject(projectDir, "bootJar")
    assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val file = findJar(projectDir, projectDetails.projectName)
    val jarFile = JarFile(file)
    assertThat(jarContainsNettyCommon(jarFile, "4.1.69.Final")).isFalse
    assertThat(jarContainsNettyCommon(jarFile, "4.1.72.Final")).isTrue
  }
}

private fun wrongTransitiveNettyVersionBuildFile() = """
    plugins {
      id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
    }
    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-security");
        implementation("org.springframework.boot:spring-boot-starter-oauth2-client");
        
    }
""".trimIndent()
