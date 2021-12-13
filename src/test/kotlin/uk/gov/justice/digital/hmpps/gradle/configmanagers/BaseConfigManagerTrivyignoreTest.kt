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

class BaseConfigManagerTrivyignoreTest : GradleBuildTest() {
  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `The trivy ignore file is copied into the project`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val result = buildProject(Companion.projectDir, "tasks")
    assertThat(result.task(":tasks")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val trivyFile = findFile(projectDir, ".trivyignore")
    assertThat(trivyFile).exists()
  }

  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `The trivy ignore file is not copied into the project`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val trivyScript =
      """
# some coverage rules
trivy.coverage.exclusions=**/*.java,**/*.kt
      """.trimIndent()
    makeTrivyFile(trivyScript)

    val result = buildProject(Companion.projectDir, "tasks")
    assertThat(result.task(":tasks")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val trivyFile = findFile(projectDir, ".trivyignore")
    assertThat(trivyFile).exists()
    val firstLine = trivyFile.useLines { it.firstOrNull() }
    assertThat(firstLine).startsWith("# some coverage rules")
  }

  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `The trivy ignore file is overwritten in the project if WARNING exists`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)
    val trivyScript =
      """
# WARNING - contents will be overwritten
trivy.coverage.exclusions=**/*.java,**/*.kt
      """.trimIndent()
    makeTrivyFile(trivyScript)

    val result = buildProject(Companion.projectDir, "tasks")
    assertThat(result.task(":tasks")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val trivyFile = findFile(projectDir, ".trivyignore")
    assertThat(trivyFile).exists()
    val firstLine = trivyFile.useLines { it.firstOrNull() }
    assertThat(firstLine).startsWith("# WARNING - THIS FILE WAS GENERATED")
  }

  private fun makeTrivyFile(trivyScript: String) {
    val trivyFile = File(projectDir, ".trivyignore")
    Files.writeString(trivyFile.toPath(), trivyScript)
  }
}
