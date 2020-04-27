package uk.gov.justice.digital.hmpps.gradle.functional.pluginmanagers

import org.assertj.core.api.Assertions.assertThat
import org.gradle.internal.impldep.org.codehaus.plexus.util.FileUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import uk.gov.justice.digital.hmpps.gradle.functional.ProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.buildProject
import uk.gov.justice.digital.hmpps.gradle.functional.javaProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.kotlinProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.makeProject
import java.io.File

class VersionsPluginManagerTest {

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
  fun `The versions plugin dependencyUpdates task is available`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val result = buildProject(projectDir, "dependencyUpdates", "-m")
    assertThat(result.output)
        .contains(":dependencyUpdates SKIPPED")
        .contains("SUCCESSFUL")
  }

}