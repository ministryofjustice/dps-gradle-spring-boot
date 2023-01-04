package uk.gov.justice.digital.hmpps.gradle.configmanagers

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.assertj.core.groups.Tuple
import org.gradle.api.tasks.Copy
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.gradle.UnitTest

@Disabled("Disabled until tests are re-written to use gradle 7!")
class AppInsightsConfigManagerTest : UnitTest() {

  @Test
  fun `Should apply app insights libraries`() {
    assertThat(project.configurations.getByName("implementation").dependencies)
      .extracting<Tuple> { tuple(it.group, it.name) }
      .contains(
        tuple("com.microsoft.azure", "applicationinsights-core"),
        tuple("io.opentelemetry", "opentelemetry-api"),
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
    assertThat(dependsOn).extracting<String> { it.name }.contains("copyAgent")
  }
}
