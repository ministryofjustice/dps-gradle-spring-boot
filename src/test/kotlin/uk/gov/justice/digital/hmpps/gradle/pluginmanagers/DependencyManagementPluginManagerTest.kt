package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.gradle.functional.GradleBuildTest
import uk.gov.justice.digital.hmpps.gradle.functional.buildProject
import uk.gov.justice.digital.hmpps.gradle.functional.findJar
import uk.gov.justice.digital.hmpps.gradle.functional.javaProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.makeProject
import java.util.jar.JarFile

class DependencyManagementPluginManagerTest : GradleBuildTest() {

  @Test
  fun `Wrong transitive version of snakeyaml should be overridden by the plugin`() {
    val project = javaProjectDetails(projectDir).copy(
      buildScript = """
        plugins {
          id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
        }
        dependencies {
            implementation("org.springframework.boot:spring-boot-starter-web");
        }
      """.trimIndent(),
    )
    makeProject(project)

    val result = buildProject(projectDir, "bootJar")
    assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val jarFile = JarFile(findJar(projectDir, project.projectName))
    assertThat(jarContains(jarFile, "snakeyaml-1.30")).isFalse
    assertThat(jarContains(jarFile, "snakeyaml-1.33")).isTrue
  }

  @Test
  fun `Upgrade of spring security should be overridden by the plugin`() {
    val project = javaProjectDetails(projectDir).copy(
      buildScript = """
        plugins {
          id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
        }
        dependencies {
          implementation("org.springframework.boot:spring-boot-starter-security");
          implementation("org.springframework.boot:spring-boot-starter-oauth2-client");
        }
      """.trimIndent(),
    )
    makeProject(project)

    val result = buildProject(projectDir, "bootJar")
    assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val jarFile = JarFile(findJar(projectDir, project.projectName))
    assertThat(jarContains(jarFile, "spring-security-core-5.7.4")).isFalse
    assertThat(jarContains(jarFile, "spring-security-core-5.7.5")).isTrue
    assertThat(jarContains(jarFile, "spring-security-oauth2-client-5.7.4")).isFalse
    assertThat(jarContains(jarFile, "spring-security-oauth2-client-5.7.5")).isTrue
    assertThat(jarContains(jarFile, "spring-security-oauth2-core-5.7.4")).isFalse
    assertThat(jarContains(jarFile, "spring-security-oauth2-core-5.7.5")).isTrue
    assertThat(jarContains(jarFile, "spring-security-web-5.7.4")).isFalse
    assertThat(jarContains(jarFile, "spring-security-web-5.7.5")).isTrue
    assertThat(jarContains(jarFile, "spring-security-oauth2-jose-5.7.4")).isFalse
    assertThat(jarContains(jarFile, "spring-security-oauth2-jose-5.7.5")).isTrue
  }

  private fun jarContains(jar: JarFile, dependency: String): Boolean =
    jar.getJarEntry("BOOT-INF/lib/$dependency.jar") != null
}
