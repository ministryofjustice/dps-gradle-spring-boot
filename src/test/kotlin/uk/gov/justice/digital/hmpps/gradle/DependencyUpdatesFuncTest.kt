package uk.gov.justice.digital.hmpps.gradle

import org.assertj.core.api.Assertions.assertThat
import org.gradle.internal.impldep.org.codehaus.plexus.util.FileUtils
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class DependencyUpdatesFuncTest {

  @TempDir
  lateinit var projectDir: File

  @AfterEach
  fun `Delete project`() {
    FileUtils.cleanDirectory(projectDir)
  }

  @Test
  fun `The latest version check report is generated`() {
    makeProject(javaProjectDetails(projectDir))

    val result = buildProject(projectDir, "dependencyUpdates")
    println(result.output)
    assertThat(result.task(":dependencyUpdates")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val reportDir = File("$projectDir/build/dependencyUpdates")
    reportDir.copyRecursively(File("build/dependencyUpdates/projectsUsingPlugin"), overwrite = true)
    assertThat(File("build/dependencyUpdates/projectsUsingPlugin/report.txt")).exists()
  }

}
