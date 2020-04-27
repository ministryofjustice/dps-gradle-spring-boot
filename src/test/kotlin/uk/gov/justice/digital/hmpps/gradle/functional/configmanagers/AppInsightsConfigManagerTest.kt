package uk.gov.justice.digital.hmpps.gradle.functional.configmanagers

import org.assertj.core.api.Assertions
import org.gradle.internal.impldep.org.codehaus.plexus.util.FileUtils
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import uk.gov.justice.digital.hmpps.gradle.functional.ProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.buildProject
import uk.gov.justice.digital.hmpps.gradle.functional.findJar
import uk.gov.justice.digital.hmpps.gradle.functional.javaProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.kotlinProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.makeProject
import java.io.File

class AppInsightsConfigManagerTest {

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
  fun `The application insights jar is copied into the build lib`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val result = buildProject(projectDir, "assemble")
    Assertions.assertThat(result.task(":assemble")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val file = findJar(projectDir, "applicationinsights-agent")
    Assertions.assertThat(file).exists()
  }

}