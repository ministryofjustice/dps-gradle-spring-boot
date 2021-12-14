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

class DependencyManagementPluginManagerTest : GradleBuildTest() {

  companion object {
    @Suppress("unused")
    @JvmStatic
    fun wrongTransitiveSpringSecurityVersion() = listOf(
      Arguments.of(javaProjectDetails(projectDir).copy(buildScript = wrongTransitiveSpringSecurityVersionBuildFile())),
      Arguments.of(kotlinProjectDetails(projectDir).copy(buildScript = wrongTransitiveSpringSecurityVersionBuildFile())),
    )
  }
  private fun jarContainsLog4jToSlf4j(jar: JarFile, version: String): Boolean =
    jar.getJarEntry("BOOT-INF/lib/log4j-to-slf4j-$version.jar") != null

  private fun jarContainsLog4jApi(jar: JarFile, version: String): Boolean =
    jar.getJarEntry("BOOT-INF/lib/log4j-api-$version.jar") != null

  @ParameterizedTest
  @MethodSource("wrongTransitiveSpringSecurityVersion")
  fun `Wrong transitive version of spring security should be overridden by the plugin`(projectDetails: ProjectDetails) {
    makeProject(projectDetails.copy())

    val result = buildProject(projectDir, "bootJar")
    assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val file = findJar(projectDir, projectDetails.projectName)
    val jarFile = JarFile(file)
    assertThat(jarContainsLog4jToSlf4j(jarFile, "2.14.1")).isFalse
    assertThat(jarContainsLog4jToSlf4j(jarFile, "2.15.0")).isTrue
    assertThat(jarContainsLog4jApi(jarFile, "2.14.1")).isFalse
    assertThat(jarContainsLog4jApi(jarFile, "2.15.0")).isTrue
  }
}

private fun wrongTransitiveSpringSecurityVersionBuildFile() = """
    plugins {
      id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
    }
    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-security");
        implementation("org.springframework.boot:spring-boot-starter-oauth2-client");
        
    }
""".trimIndent()
