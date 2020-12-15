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
import uk.gov.justice.digital.hmpps.gradle.functional.javaProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.kotlinProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.makeProject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.jar.JarFile

class SpringBootPluginManagerTest : GradleBuildTest() {

  companion object {
    @JvmStatic
    fun projectDetailsWithJunit4TestsAndDependency() = listOf(
      Arguments.of(javaProjectDetails(projectDir).copy(buildScript = javaJunit4Dependency(), testClass = javaJunit4Test())),
      Arguments.of(kotlinProjectDetails(projectDir).copy(buildScript = kotlinJunit4Dependency(), testClass = kotlinJunit4Test()))
    )
  }

  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `Manifest file contains project name and version`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val result = buildProject(projectDir, "bootJar")
    assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val file = findJar(projectDir, projectDetails.projectName)
    val jarFile = JarFile(file)
    assertThat(jarFile.manifest.mainAttributes.getValue("Implementation-Version")).isEqualTo(LocalDate.now().format(DateTimeFormatter.ISO_DATE))
    assertThat(jarFile.manifest.mainAttributes.getValue("Implementation-Title")).isEqualTo(projectDetails.projectName)
  }

  @ParameterizedTest
  @MethodSource("projectDetailsWithJunit4TestsAndDependency")
  fun `Junit 4 tests will compile and run if Junit 4 dependency added`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val result = buildProject(projectDir, "test")
    assertThat(result.output)
      .contains("Task :test")
      .contains("BUILD SUCCESSFUL")
      .contains("1 tests")
  }
}

private fun javaJunit4Dependency(): String =
  """
      plugins {
          id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
      }
      
      dependencies {
        testImplementation 'junit:junit:4.13'
      }
  """.trimIndent()

private fun kotlinJunit4Dependency(): String =
  """
      plugins {
          id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
      }
      
      dependencies {
        testImplementation("junit:junit:4.13")
      }
  """.trimIndent()

private fun javaJunit4Test(): String {
  return """
      package uk.gov.justice.digital.hmpps.app;
      
      import org.junit.Test;

      import static org.assertj.core.api.Assertions.assertThat;

      public class ApplicationTest {
          @Test
          public void aTest() {
              assertThat("anything").isEqualTo("anything");
          }
      }
  """.trimIndent()
}

private fun kotlinJunit4Test(): String {
  return """
      package uk.gov.justice.digital.hmpps.app
      
      import org.assertj.core.api.Assertions.assertThat
      import org.junit.Test
      
      class ApplicationTest {
          @Test
          fun `A Test`() {
              assertThat("anything").isEqualTo("anything")
          }
      }
  """.trimIndent()
}
