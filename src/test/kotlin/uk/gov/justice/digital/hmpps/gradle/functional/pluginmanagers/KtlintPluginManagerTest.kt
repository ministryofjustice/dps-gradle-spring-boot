package uk.gov.justice.digital.hmpps.gradle.functional.pluginmanagers

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import uk.gov.justice.digital.hmpps.gradle.functional.GradleBuildTest
import uk.gov.justice.digital.hmpps.gradle.functional.ProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.buildProject
import uk.gov.justice.digital.hmpps.gradle.functional.buildProjectAndFail
import uk.gov.justice.digital.hmpps.gradle.functional.findFile
import uk.gov.justice.digital.hmpps.gradle.functional.kotlinProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.makeProject

class KtlintPluginManagerTest : GradleBuildTest() {
  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `Ktlint succeeds if no errors in project`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val result = buildProject(projectDir, "check")
    assertThat(result.task(":check")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
  }

  @Test
  fun `Ktlint fails if project contains errors`() {
    makeProject(
      kotlinProjectDetails(projectDir).copy(
        mainClass =
          """
        package uk.gov.justice.digital.hmpps.app
  
        import org.springframework.boot.autoconfigure.*
        import org.springframework.boot.runApplication
  
        @SpringBootApplication
        open class Application
  
        fun main(args: Array<String>) {
            runApplication<Application>(*args)
        }

          """.trimIndent(),
      )
    )

    val result = buildProjectAndFail(projectDir, "check")
    assertThat(result.output)
      .contains("Wildcard import (cannot be auto-corrected)")
      .contains("Unexpected indentation (4) (should be 2)")
  }

  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `The sonar properties file is copied into the project`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val result = buildProject(Companion.projectDir, "tasks")
    assertThat(result.task(":tasks")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val sonarFile = findFile(projectDir, "sonar-project.properties")
    assertThat(sonarFile).exists()
  }
}
