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
import java.io.File
import java.nio.file.Files

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
  fun `The editor config file is copied into the project`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val result = buildProject(Companion.projectDir, "tasks")
    assertThat(result.task(":tasks")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val editorConfig = findFile(projectDir, ".editorconfig")
    assertThat(editorConfig).exists()
  }

  @Test
  fun `ktlint doesn't break multi-project builds`() {
    val app1Dir = File(projectDir, "app1").also { appDir -> appDir.mkdirs() }
    val app2Dir = File(projectDir, "app2").also { appDir -> appDir.mkdirs() }
    makeProject(kotlinProjectDetails(app1Dir))
    makeProject(kotlinProjectDetails(app2Dir))
    makeFile(
      "settings.gradle.kts",
      """
              rootProject.name = "multi-project"
              include("app1")
              include("app2")
            """.trimMargin()
    )
    makeFile("build.gradle.kts", "")

    val result = buildProject(projectDir, "check", "-m")
    assertThat(result.output).contains("BUILD SUCCESSFUL")
    assertThat(result.output).contains("app1:ktlintCheck")
    assertThat(result.output).contains("app2:ktlintCheck")
  }

  private fun makeFile(fileName: String, contents: String) {
    File(projectDir, fileName)
      .let { settingsFile -> Files.writeString(settingsFile.toPath(), contents) }
  }
}
