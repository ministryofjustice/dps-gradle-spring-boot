package uk.gov.justice.digital.hmpps.gradle.functional.pluginmanagers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import uk.gov.justice.digital.hmpps.gradle.functional.GradleBuildTest
import uk.gov.justice.digital.hmpps.gradle.functional.ProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.getDependencyVersion
import uk.gov.justice.digital.hmpps.gradle.functional.makeProject

class DependencyManagementPluginManagerTest : GradleBuildTest() {
  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `Spring dependency versions are defaulted from the dependency management plugin`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val webVersion = getDependencyVersion(projectDir, "spring-boot-starter-web")

    assertThat(webVersion).isNotBlank

    val actuatorVersion = getDependencyVersion(projectDir, "spring-boot-starter-actuator")
    val validatorVersion = getDependencyVersion(projectDir, "spring-boot-starter-validation")

    assertThat(webVersion).isEqualTo(actuatorVersion).isEqualTo(validatorVersion)
  }
}
