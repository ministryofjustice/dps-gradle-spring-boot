package uk.gov.justice.digital.hmpps.gradle.functional.pluginmanagers

import org.assertj.core.api.Assertions.assertThat
import org.gradle.internal.impldep.org.codehaus.plexus.util.FileUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import uk.gov.justice.digital.hmpps.gradle.functional.ProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.getDependencyVersion
import uk.gov.justice.digital.hmpps.gradle.functional.javaProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.kotlinProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.makeProject
import java.io.File

class DependencyManagementPluginManagerTest {

  @AfterEach
  fun `Delete project`() {
    FileUtils.cleanDirectory(projectDir)
  }

  companion object {

    @TempDir
    @JvmStatic
    lateinit var projectDir: File

    @JvmStatic
    fun defaultProjectDetails() = listOf(
        Arguments.of(javaProjectDetails(projectDir)),
        Arguments.of(kotlinProjectDetails(projectDir))
    )
  }

  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `Spring dependency versions are defaulted from the dependency management plugin`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val webVersion = getDependencyVersion(projectDir, "spring-boot-starter-web")
    val actuatorVersion = getDependencyVersion(projectDir, "spring-boot-starter-actuator")

    assertThat(webVersion).isEqualTo(actuatorVersion)
  }
}