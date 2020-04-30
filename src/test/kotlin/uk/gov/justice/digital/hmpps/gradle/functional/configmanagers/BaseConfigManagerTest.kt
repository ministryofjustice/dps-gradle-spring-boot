package uk.gov.justice.digital.hmpps.gradle.functional.configmanagers

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import uk.gov.justice.digital.hmpps.gradle.functional.GradleBuildTest
import uk.gov.justice.digital.hmpps.gradle.functional.ProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.buildProject
import uk.gov.justice.digital.hmpps.gradle.functional.makeProject

class BaseConfigManagerTest : GradleBuildTest() {

  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `Junit 5 tests can be executed`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val result = buildProject(projectDir, "test")
    assertThat(result.task(":test")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
    assertThat(checkTestsWereExecuted(result.output)).isGreaterThan(0)
  }

  // Note that this relies on the output from the TestLoggerPlugin
  private fun checkTestsWereExecuted(buildOutput: String): Int {
    val (numberOfTestsExecuted) = """Executed (\d) test""".toRegex().find(buildOutput)!!.destructured
    return numberOfTestsExecuted.toIntOrNull() ?: 0
  }
}
