package uk.gov.justice.digital.hmpps.gradle.functional.pluginmanagers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import uk.gov.justice.digital.hmpps.gradle.functional.GradleBuildTest
import uk.gov.justice.digital.hmpps.gradle.functional.ProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.buildProject
import uk.gov.justice.digital.hmpps.gradle.functional.makeProject

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

}