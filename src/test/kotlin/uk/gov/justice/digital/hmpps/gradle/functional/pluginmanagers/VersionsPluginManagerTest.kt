package uk.gov.justice.digital.hmpps.gradle.functional.pluginmanagers

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import uk.gov.justice.digital.hmpps.gradle.functional.GradleBuildTest
import uk.gov.justice.digital.hmpps.gradle.functional.ProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.buildProject
import uk.gov.justice.digital.hmpps.gradle.functional.makeProject
import java.io.File

class VersionsPluginManagerTest : GradleBuildTest() {

  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `The versions plugin dependencyUpdates task is available`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val result = buildProject(projectDir, "dependencyUpdates", "-m")
    assertThat(result.output)
      .contains(":dependencyUpdates SKIPPED")
      .contains("SUCCESSFUL")
  }

  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `The latest version check report is generated`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val result = buildProject(projectDir, "dependencyUpdates")
    println(result.output)
    assertThat(result.task(":dependencyUpdates")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val reportDir = File("$projectDir/build/dependencyUpdates")
    reportDir.copyRecursively(File("build/dependencyUpdates/projectsUsingPlugin"), overwrite = true)
    assertThat(File("build/dependencyUpdates/projectsUsingPlugin/report.txt")).exists()
  }
}
