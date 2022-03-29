package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.gradle.functional.*
import java.util.jar.JarFile

class DependencyManagementPluginManagerTest : GradleBuildTest() {
  @Test
  fun `Should apply the Spring Dependency Management plugin`() {
    project.plugins.getPlugin(DependencyManagementPlugin::class.java)
  }

  @Test
  fun `Wrong transitive version of jackson-databind should be overridden by the plugin`() {
    val project = javaProjectDetails(projectDir).copy(buildScript = """
        plugins {
          id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
        }
        dependencies {
            implementation("org.springframework.boot:spring-boot-starter-web:2.6.5");
        }
    """.trimIndent())
    makeProject(project)

    val result = buildProject(projectDir, "bootJar")
    assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val jarFile = JarFile(findJar(projectDir, project.projectName))
    assertThat(jarContains(jarFile, "jackson-databind-2.13.2")).isFalse
    assertThat(jarContains(jarFile, "jackson-databind-2.13.2.2")).isTrue
  }

  private fun jarContains(jar: JarFile, dependency: String): Boolean =
    jar.getJarEntry("BOOT-INF/lib/$dependency.jar") != null
}