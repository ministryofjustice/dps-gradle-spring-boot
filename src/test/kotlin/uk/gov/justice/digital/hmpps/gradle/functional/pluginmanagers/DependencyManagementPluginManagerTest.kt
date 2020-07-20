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
    @Suppress("unused")
    @JvmStatic
    fun wrongTransitiveReactorNettyVersion() = listOf(
        Arguments.of(javaProjectDetails(projectDir).copy(buildScript = wrongTransitiveReactorNettyVersionBuildFile())),
        Arguments.of(kotlinProjectDetails(projectDir).copy(buildScript = wrongTransitiveReactorNettyVersionBuildFile()))
    )

    @Suppress("unused")
    @JvmStatic
    fun wrongExplicitReactorNettyVersion() = listOf(
        Arguments.of(javaProjectDetails(projectDir).copy(buildScript = wrongExplicitReactorNettyVersionBuildFile())),
        Arguments.of(kotlinProjectDetails(projectDir).copy(buildScript = wrongExplicitReactorNettyVersionBuildFile()))
    )

    @Suppress("unused")
    @JvmStatic
    fun wrongTransitiveHibernateCoreVersion() = listOf(
        Arguments.of(javaProjectDetails(projectDir).copy(buildScript = wrongTransitiveHibernateCoreVersionBuildFile())),
        Arguments.of(kotlinProjectDetails(projectDir).copy(buildScript = wrongTransitiveHibernateCoreVersionBuildFile()))
    )

    @Suppress("unused")
    @JvmStatic
    fun wrongExplicitHibernateCoreVersion() = listOf(
        Arguments.of(javaProjectDetails(projectDir).copy(buildScript = wrongExplicitHibernateCoreVersionBuildFile())),
        Arguments.of(kotlinProjectDetails(projectDir).copy(buildScript = wrongExplicitHibernateCoreVersionBuildFile()))
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

  @ParameterizedTest
  @MethodSource("wrongTransitiveHibernateCoreVersion")
  fun `Wrong transitive version of hibernate core (Spring data jpa) should be overridden by the plugin`(projectDetails: ProjectDetails) {
    makeProject(projectDetails.copy())

    val result = buildProject(projectDir, "bootJar")
    assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val file = findJar(projectDir, projectDetails.projectName)
    val jarFile = JarFile(file)
    assertThat(jarContainsHibernateCoreVersion(jarFile, "5.4.17")).isFalse()
    assertThat(jarContainsHibernateCoreVersion(jarFile, "5.4.18")).isTrue()
  }

  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `hibernate core is not imported by the plugin for no reason`(projectDetails: ProjectDetails) {
    makeProject(projectDetails.copy())

    val result = buildProject(projectDir, "bootJar")
    assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val file = findJar(projectDir, projectDetails.projectName)
    val jarFile = JarFile(file)
    assertThat(jarContainsHibernateCoreVersion(jarFile, "5.4.17")).isFalse()
  }

  // Demonstrates that you can still override the forced version by explicitly declaring in your build script
  @ParameterizedTest
  @MethodSource("wrongExplicitHibernateCoreVersion")
  fun `If explicitly requested the version of hibernate core is not forced`(projectDetails: ProjectDetails) {
    makeProject(projectDetails.copy())

    val result = buildProject(projectDir, "bootJar")
    assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val file = findJar(projectDir, projectDetails.projectName)
    val jarFile = JarFile(file)
    assertThat(jarContainsHibernateCoreVersion(jarFile, "5.4.17")).isTrue()
    assertThat(jarContainsHibernateCoreVersion(jarFile, "5.4.18")).isFalse()
  }

  private fun jarContainsHibernateCoreVersion(jar: JarFile, version: String): Boolean =
      jar.getJarEntry("BOOT-INF/lib/hibernate-core-$version.Final.jar") != null
}

private fun wrongTransitiveReactorNettyVersionBuildFile() = """
    plugins {
      id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1"
    }
    dependencies {
      implementation("org.springframework.boot:spring-boot-starter-webflux") // This imports reactor netty 0.9.8.RELEASE
    }
  """.trimIndent()

private fun wrongExplicitReactorNettyVersionBuildFile() = """
    plugins {
      id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1"
    }
    dependencies {
      implementation("io.projectreactor.netty:reactor-netty:0.9.8.RELEASE")
    }
  """.trimIndent()

private fun wrongTransitiveHibernateCoreVersionBuildFile() = """
    plugins {
      id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1"
    }
    dependencies {
      implementation("org.springframework.boot:spring-boot-starter-data-jpa") // This imports org.hibernate:hibernate-core -> 5.4.17.Final
    }
  """.trimIndent()

private fun wrongExplicitHibernateCoreVersionBuildFile() = """
    plugins {
      id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1"
    }
    dependencies {
      implementation("org.hibernate:hibernate-core:5.4.17.Final")
    }
  """.trimIndent()
