package uk.gov.justice.digital.hmpps.gradle.configmanagers

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import uk.gov.justice.digital.hmpps.gradle.functional.GradleBuildTest
import uk.gov.justice.digital.hmpps.gradle.functional.ProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.buildProject
import uk.gov.justice.digital.hmpps.gradle.functional.findJar
import uk.gov.justice.digital.hmpps.gradle.functional.makeProject

class AppInsightsConfigManagerTest : GradleBuildTest() {

  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `The application insights jar is copied into the build lib`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val result = buildProject(projectDir, "assemble")
    assertThat(result.task(":assemble")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val file = findJar(projectDir, "applicationinsights-agent")
    assertThat(file).exists()
  }
}
