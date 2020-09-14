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

class BaseConfigManagerGradleTest : GradleBuildTest() {
  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `The sonar properties file is copied into the project`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val result = buildProject(Companion.projectDir, "tasks")
    assertThat(result.task(":tasks")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val sonarFile = findFile(GradleBuildTest.projectDir, "sonar-project.properties")
    assertThat(sonarFile).exists()
  }

  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `The sonar properties file is not copied into the project`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val sonarScript = """
# some coverage rules
sonar.coverage.exclusions=**/*.java,**/*.kt
      """.trimIndent()
    makeSonarFile(projectDetails, sonarScript)

    val result = buildProject(Companion.projectDir, "tasks")
    assertThat(result.task(":tasks")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val sonarFile = findFile(GradleBuildTest.projectDir, "sonar-project.properties")
    assertThat(sonarFile).exists()
    val firstLine = sonarFile.useLines { it.firstOrNull() }
    assertThat(firstLine).startsWith("# some coverage rules")
  }

  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `The sonar properties file is overwritten in the project if WARNING exists`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)
    val sonarScript = """
# WARNING - contents will be overwritten
sonar.coverage.exclusions=**/*.java,**/*.kt
      """.trimIndent()
    makeSonarFile(projectDetails, sonarScript)

    val result = buildProject(Companion.projectDir, "tasks")
    assertThat(result.task(":tasks")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val sonarFile = findFile(GradleBuildTest.projectDir, "sonar-project.properties")
    assertThat(sonarFile).exists()
    val firstLine = sonarFile.useLines { it.firstOrNull() }
    assertThat(firstLine).startsWith("# WARNING - THIS FILE WAS GENERATED")
  }

  private fun makeSonarFile(projectDetails: ProjectDetails, sonarScript: String) {
    val sonarFile = File(projectDir, "sonar-project.properties")
    Files.writeString(sonarFile.toPath(), sonarScript)
  }
}
