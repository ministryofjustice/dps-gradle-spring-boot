package uk.gov.justice.digital.hmpps.gradle.functional.pluginmanagers

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import uk.gov.justice.digital.hmpps.gradle.functional.*
import java.util.jar.JarFile

class DependencyManagementPluginManagerTest : GradleBuildTest() {

  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `Spring dependency versions are defaulted from the dependency management plugin`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val webVersion = getDependencyVersion(projectDir, "org.springframework.boot:spring-boot-starter-web")

    assertThat(webVersion).isNotBlank

    val actuatorVersion = getDependencyVersion(projectDir, "org.springframework.boot:spring-boot-starter-actuator")
    val validatorVersion = getDependencyVersion(projectDir, "org.springframework.boot:spring-boot-starter-validation")

    assertThat(webVersion).isEqualTo(actuatorVersion).isEqualTo(validatorVersion)
  }

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
    assertThat(jarContains(jarFile, "snakeyaml-1.31")).isFalse
    assertThat(jarContains(jarFile, "snakeyaml-1.33")).isTrue
  }

  private fun jarContains(jar: JarFile, dependency: String): Boolean =
    jar.getJarEntry("BOOT-INF/lib/$dependency.jar") != null
}
