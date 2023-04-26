package uk.gov.justice.digital.hmpps.gradle.configmanagers

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import uk.gov.justice.digital.hmpps.gradle.functional.GradleBuildTest
import uk.gov.justice.digital.hmpps.gradle.functional.ProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.buildProject
import uk.gov.justice.digital.hmpps.gradle.functional.findFile
import uk.gov.justice.digital.hmpps.gradle.functional.makeProject
import java.io.File
import java.nio.file.Files

class BaseConfigManagerGradlePropertiesTest : GradleBuildTest() {
  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `The gradle properties file is copied into the project`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val result = buildProject(Companion.projectDir, "tasks")
    assertThat(result.task(":tasks")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val gradlePropertiesFile = findFile(projectDir, "gradle.properties")
    assertThat(gradlePropertiesFile).exists()
  }

  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `The gradle properties file is not copied into the project`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val gradleProperties =
      """
# some configuration
kotlin.incremental.useClasspathSnapshot=false
      """.trimIndent()
    makeGradlePropertiesFile(gradleProperties)

    val result = buildProject(Companion.projectDir, "tasks")
    assertThat(result.task(":tasks")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val gradlePropertiesFile = findFile(projectDir, "gradle.properties")
    assertThat(gradlePropertiesFile).exists()
    val firstLine = gradlePropertiesFile.useLines { it.firstOrNull() }
    assertThat(firstLine).startsWith("# some configuration")
  }

  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `The gradle properties file is overwritten in the project if WARNING exists`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)
    val gradleProperties =
      """
# WARNING - contents will be overwritten
kotlin.incremental.useClasspathSnapshot=false
      """.trimIndent()
    makeGradlePropertiesFile(gradleProperties)

    val result = buildProject(Companion.projectDir, "tasks")
    assertThat(result.task(":tasks")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val gradlePropertiesFile = findFile(projectDir, "gradle.properties")
    assertThat(gradlePropertiesFile).exists()
    val firstLine = gradlePropertiesFile.useLines { it.firstOrNull() }
    assertThat(firstLine).startsWith("# WARNING - THIS FILE WAS GENERATED")
  }

  private fun makeGradlePropertiesFile(gradleProperties: String) {
    val gradlePropertiesFile = File(projectDir, "gradle.properties")
    Files.writeString(gradlePropertiesFile.toPath(), gradleProperties)
  }
}
