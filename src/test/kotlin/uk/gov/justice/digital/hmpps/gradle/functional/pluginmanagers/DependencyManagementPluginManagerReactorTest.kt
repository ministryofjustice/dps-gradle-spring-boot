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

class DependencyManagementPluginManagerReactorTest : GradleBuildTest() {

  companion object {
    @JvmStatic
    fun wrongTransitiveReactorVersion() = listOf(
      Arguments.of(javaProjectDetails(projectDir).copy(buildScript = wrongTransitiveReactorVersionBuildFile())),
      Arguments.of(kotlinProjectDetails(projectDir).copy(buildScript = wrongTransitiveReactorVersionBuildFile())),
    )
  }

  @ParameterizedTest
  @MethodSource("wrongTransitiveReactorVersion")
  fun `Wrong transitive version of netty should be overridden by the plugin`(projectDetails: ProjectDetails) {
    makeProject(projectDetails.copy())

    val result = buildProject(projectDir, "bootJar")
    assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val file = findJar(projectDir, projectDetails.projectName)
    val jarContents = JarFile(file).versionedStream().map { it.name }.toList()
    assertThat(jarContents)
      .doesNotContain("BOOT-INF/lib/reactor-netty-http-1.2.7.jar")
      .contains("BOOT-INF/lib/reactor-netty-http-1.2.8.jar")
  }
}

private fun wrongTransitiveReactorVersionBuildFile() = """
    plugins {
      id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
    }
    dependencies {
      implementation("org.springframework.boot:spring-boot-starter-webflux")
    }
""".trimIndent()
