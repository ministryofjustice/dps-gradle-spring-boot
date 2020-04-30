package uk.gov.justice.digital.hmpps.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach

abstract class UnitTest {

  protected val project: Project = ProjectBuilder.builder().build()

  @BeforeEach
  fun `Create project`() {
    project.pluginManager.apply("uk.gov.justice.hmpps.gradle-spring-boot")
  }
}
