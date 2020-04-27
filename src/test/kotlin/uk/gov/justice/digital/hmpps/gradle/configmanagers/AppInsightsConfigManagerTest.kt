package uk.gov.justice.digital.hmpps.gradle.configmanagers

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AppInsightsConfigManagerTest {

  private val project: Project = ProjectBuilder.builder().build()

  @BeforeEach
  fun `Create project`() {
    project.pluginManager.apply("uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot")
  }

  @Test
  fun `Should apply app insights libraries`() {
    assertThat(project.configurations.getByName("implementation").dependencies)
        .extracting("group", "name", "version")
        .contains(
            tuple("net.logstash.logback", "logstash-logback-encoder", "6.3"),
            tuple("com.microsoft.azure", "applicationinsights-spring-boot-starter", "2.6.0"),
            tuple("com.microsoft.azure", "applicationinsights-logging-logback", "2.6.0")
        )
  }

  // The test fails if an exception is thrown
  @Test
  fun `Should create agentDeps configuration`() {
    project.configurations.getByName("agentDeps")
  }

  @Test
  fun `Should create copyAgent task`() {
    val copyAgentTask = project.tasks.getByName("copyAgent") as Copy
    assertThat(copyAgentTask.destinationDir.absolutePath).contains("lib")
    assertThat(copyAgentTask.source.singleFile.name).contains("applicationinsights")
  }

  @Test
  fun `assemble task should depend on copyAgent task`() {
    val assembleTask = project.tasks.getByName("assemble")
    val dependsOn = assembleTask.taskDependencies.getDependencies(assembleTask)
    assertThat(dependsOn).extracting("name").contains("copyAgent")
  }

}