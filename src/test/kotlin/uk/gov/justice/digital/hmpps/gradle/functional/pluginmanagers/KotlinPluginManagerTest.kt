package uk.gov.justice.digital.hmpps.gradle.functional.pluginmanagers

import org.assertj.core.api.Assertions
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import uk.gov.justice.digital.hmpps.gradle.PluginProperty.IS_KOTLIN_PROJECT
import uk.gov.justice.digital.hmpps.gradle.functional.GradleBuildTest
import uk.gov.justice.digital.hmpps.gradle.functional.ProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.buildProject
import uk.gov.justice.digital.hmpps.gradle.functional.findJar
import uk.gov.justice.digital.hmpps.gradle.functional.javaProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.makeProject
import java.util.jar.JarFile

class KotlinPluginManagerTest : GradleBuildTest() {

  companion object {
    @JvmStatic
    fun javaProjectWithKotlinTurnedOff() = listOf(
        Arguments.of(javaProjectDetails(projectDir).copy(properties = """${IS_KOTLIN_PROJECT.key}=false""")),
    )
  }

  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `The Kotlin plugin is applied by default`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val result = buildProject(projectDir, "assemble")
    Assertions.assertThat(result.task(":assemble")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val file = findJar(projectDir, projectDetails.projectName)
    val jarFile = JarFile(file)
    Assertions.assertThat(jarContainsKotlinDependency(jarFile)).isTrue

  }

  @ParameterizedTest
  @MethodSource("javaProjectWithKotlinTurnedOff")
  fun `The Kotlin plugin can be turned off`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val result = buildProject(projectDir, "assemble")
    Assertions.assertThat(result.task(":assemble")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val file = findJar(projectDir, projectDetails.projectName)
    val jarFile = JarFile(file)
    Assertions.assertThat(jarContainsKotlinDependency(jarFile)).isFalse

  }

  private fun jarContainsKotlinDependency(jar: JarFile): Boolean =
      jar.versionedStream().anyMatch { it.realName.contains("kotlin") }
}
