package uk.gov.justice.digital.hmpps.gradle.functional.pluginmanagers

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import uk.gov.justice.digital.hmpps.gradle.functional.GradleBuildTest
import uk.gov.justice.digital.hmpps.gradle.functional.ProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.buildProject
import uk.gov.justice.digital.hmpps.gradle.functional.findJar
import uk.gov.justice.digital.hmpps.gradle.functional.getDependencyVersion
import uk.gov.justice.digital.hmpps.gradle.functional.javaProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.kotlinProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.makeProject
import java.util.jar.JarFile

class DependencyManagementPluginManagerTest : GradleBuildTest() {

  companion object {
    @JvmStatic
    fun wrongTransitiveReactorNettyVersion() = listOf(
        Arguments.of(javaProjectDetails(projectDir).copy(buildScript = javaWrongTransitiveReactorNettyVersion())),
        Arguments.of(kotlinProjectDetails(projectDir).copy(buildScript = kotlinWrongTransitiveReactorNettyVersion()))
    )

    @JvmStatic
    fun wrongExplicitReactorNettyVersion() = listOf(
        Arguments.of(javaProjectDetails(projectDir).copy(buildScript = javaWrongExplicitReactorNettyVersion())),
        Arguments.of(kotlinProjectDetails(projectDir).copy(buildScript = kotlinWrongExplicitReactorNettyVersion()))
    )
  }

  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `Spring dependency versions are defaulted from the dependency management plugin`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val webVersion = getDependencyVersion(projectDir, "spring-boot-starter-web")

    assertThat(webVersion).isNotBlank()

    val actuatorVersion = getDependencyVersion(projectDir, "spring-boot-starter-actuator")
    val validatorVersion = getDependencyVersion(projectDir, "spring-boot-starter-validation")

    assertThat(webVersion).isEqualTo(actuatorVersion).isEqualTo(validatorVersion)
  }

  @ParameterizedTest
  @MethodSource("wrongTransitiveReactorNettyVersion")
  fun `Wrong transitive version of reactor netty should be overridden by the plugin`(projectDetails: ProjectDetails) {
    makeProject(projectDetails.copy())

    val result = buildProject(projectDir, "bootJar")
    assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val file = findJar(projectDir, projectDetails.projectName)
    val jarFile = JarFile(file)
    assertThat(jarContainsNettyVersion(jarFile, "0.9.8")).isFalse()
    assertThat(jarContainsNettyVersion(jarFile, "0.9.9")).isTrue()
  }

  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `Reactor-netty is not imported by the plugin for no reason`(projectDetails: ProjectDetails) {
    makeProject(projectDetails.copy())

    val result = buildProject(projectDir, "bootJar")
    assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val file = findJar(projectDir, projectDetails.projectName)
    val jarFile = JarFile(file)
    assertThat(jarContainsNettyVersion(jarFile, "0.9.9")).isFalse()
  }

  // Demonstrates that you can still override the forced version by explicitly declaring in your build script
  @ParameterizedTest
  @MethodSource("wrongExplicitReactorNettyVersion")
  fun `If explicitly requested the version of rector netty is not forced`(projectDetails: ProjectDetails) {
    makeProject(projectDetails.copy())

    val result = buildProject(projectDir, "bootJar")
    assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val file = findJar(projectDir, projectDetails.projectName)
    val jarFile = JarFile(file)
    assertThat(jarContainsNettyVersion(jarFile, "0.9.8")).isTrue()
    assertThat(jarContainsNettyVersion(jarFile, "0.9.9")).isFalse()
  }

  private fun jarContainsNettyVersion(jar: JarFile, version: String): Boolean =
      jar.getJarEntry("BOOT-INF/lib/reactor-netty-$version.RELEASE.jar") != null

}

private fun javaWrongTransitiveReactorNettyVersion(): String {
  return """
    plugins {
      id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
    }
    dependencies {
      implementation "org.springframework.boot:spring-boot-starter-webflux" // This imports reactor netty 0.9.8.RELEASE
    }
  """.trimIndent()
}

private fun kotlinWrongTransitiveReactorNettyVersion(): String {
  return """
    plugins {
      id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1"
    }
    dependencies {
      implementation("org.springframework.boot:spring-boot-starter-webflux") // This imports reactor netty 0.9.8.RELEASE
    }
  """.trimIndent()
}

private fun javaWrongExplicitReactorNettyVersion(): String {
  return """
    plugins {
      id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
    }
    dependencies {
      implementation "io.projectreactor.netty:reactor-netty:0.9.8.RELEASE"
    }
  """.trimIndent()
}

private fun kotlinWrongExplicitReactorNettyVersion(): String {
  return """
    plugins {
      id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1"
    }
    dependencies {
      implementation("io.projectreactor.netty:reactor-netty:0.9.8.RELEASE")
    }
  """.trimIndent()
}