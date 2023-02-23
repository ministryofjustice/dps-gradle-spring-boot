package uk.gov.justice.digital.hmpps.gradle.functional

import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.provider.Arguments
import java.io.File

abstract class GradleBuildTest {

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
      Arguments.of(kotlinProjectDetails(projectDir)),
    )
  }
}
