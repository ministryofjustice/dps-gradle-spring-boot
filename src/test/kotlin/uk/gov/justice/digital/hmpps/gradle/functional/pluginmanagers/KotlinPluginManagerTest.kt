package uk.gov.justice.digital.hmpps.gradle.functional.pluginmanagers

import org.assertj.core.api.Assertions
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import uk.gov.justice.digital.hmpps.gradle.functional.GradleBuildTest
import uk.gov.justice.digital.hmpps.gradle.functional.ProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.buildProject
import uk.gov.justice.digital.hmpps.gradle.functional.kotlinProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.makeProject

class KotlinPluginManagerTest : GradleBuildTest() {

  companion object {
    @JvmStatic
    fun kotlinOnlyProject() = listOf(
      Arguments.of(kotlinProjectDetails(projectDir))
    )
  }

  @ParameterizedTest
  @MethodSource("kotlinOnlyProject")
  fun `Sets JVM target for Kotlin compile tasks`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    // This compile would fail at JVM 1.6 which is the default
    val result = buildProject(projectDir, "compileKotlin")
    Assertions.assertThat(result.task(":compileKotlin")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
  }
}
