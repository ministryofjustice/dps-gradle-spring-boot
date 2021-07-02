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

  private fun jarContainsSpringSecurityConfig(jar: JarFile, version: String): Boolean =
    jar.getJarEntry("BOOT-INF/lib/spring-security-config-$version.jar") != null

  private fun jarContainsSpringSecurityCore(jar: JarFile, version: String): Boolean =
    jar.getJarEntry("BOOT-INF/lib/spring-security-core-$version.jar") != null

  private fun jarContainsSpringSecurityOauth2Client(jar: JarFile, version: String): Boolean =
    jar.getJarEntry("BOOT-INF/lib/spring-security-oauth2-client-$version.jar") != null

  private fun jarContainsSpringSecurityOauth2Core(jar: JarFile, version: String): Boolean =
    jar.getJarEntry("BOOT-INF/lib/spring-security-oauth2-core-$version.jar") != null

  private fun jarContainsSpringSecurityWeb(jar: JarFile, version: String): Boolean =
    jar.getJarEntry("BOOT-INF/lib/spring-security-web-$version.jar") != null

  private fun jarContainsSpringSecurityCrypto(jar: JarFile, version: String): Boolean =
    jar.getJarEntry("BOOT-INF/lib/spring-security-web-$version.jar") != null

  private fun jarContainsSpringSecurityOauth2Jose(jar: JarFile, version: String): Boolean =
    jar.getJarEntry("BOOT-INF/lib/spring-security-oauth2-jose-$version.jar") != null

  @ParameterizedTest
  @MethodSource("wrongTransitiveSpringSecurityVersion")
  fun `Wrong transitive version of spring security should be overridden by the plugin`(projectDetails: ProjectDetails) {
    makeProject(projectDetails.copy())

    val result = buildProject(projectDir, "bootJar")
    assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val file = findJar(projectDir, projectDetails.projectName)
    val jarFile = JarFile(file)
    assertThat(jarContainsSpringSecurityConfig(jarFile, "5.5.1")).isFalse
    assertThat(jarContainsSpringSecurityConfig(jarFile, "5.5.0")).isTrue
    assertThat(jarContainsSpringSecurityCore(jarFile, "5.5.1")).isFalse
    assertThat(jarContainsSpringSecurityCore(jarFile, "5.5.0")).isTrue
    assertThat(jarContainsSpringSecurityOauth2Client(jarFile, "5.5.1")).isFalse
    assertThat(jarContainsSpringSecurityOauth2Client(jarFile, "5.5.0")).isTrue
    assertThat(jarContainsSpringSecurityOauth2Core(jarFile, "5.5.1")).isFalse
    assertThat(jarContainsSpringSecurityOauth2Core(jarFile, "5.5.0")).isTrue
    assertThat(jarContainsSpringSecurityWeb(jarFile, "5.5.1")).isFalse
    assertThat(jarContainsSpringSecurityWeb(jarFile, "5.5.0")).isTrue
    assertThat(jarContainsSpringSecurityCrypto(jarFile, "5.5.1")).isFalse
    assertThat(jarContainsSpringSecurityCrypto(jarFile, "5.5.0")).isTrue
    assertThat(jarContainsSpringSecurityOauth2Jose(jarFile, "5.5.1")).isFalse
    assertThat(jarContainsSpringSecurityOauth2Jose(jarFile, "5.5.0")).isTrue
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
