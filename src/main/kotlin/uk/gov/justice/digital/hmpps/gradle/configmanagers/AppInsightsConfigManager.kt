package uk.gov.justice.digital.hmpps.gradle.configmanagers

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import uk.gov.justice.digital.hmpps.gradle.ConfigManager

private const val APP_INSIGHTS_SDK_VERSION = "2.6.4"
private const val APP_INSIGHTS_AGENT_VERSION = "3.4.6"

class AppInsightsConfigManager(override val project: Project) : ConfigManager {

  override fun configure() {
    addAgentDepsConfiguration()
    createCopyAgentTask()
    addDependencies()
  }

  private fun addAgentDepsConfiguration() {
    project.configurations.create("agentDeps").isTransitive = false
  }

  private fun createCopyAgentTask() {
    val copyAgentTask = project.tasks.register("copyAgent", Copy::class.java) {
      it.from(project.configurations.getByName("agentDeps"))
      it.into("${project.buildDir}/libs")
    }
    project.tasks.getByName("assemble").dependsOn(copyAgentTask)
  }

  private fun addDependencies() {
    project.dependencies.add("implementation", "com.microsoft.azure:applicationinsights-spring-boot-starter:$APP_INSIGHTS_SDK_VERSION")
    project.dependencies.add("implementation", "com.microsoft.azure:applicationinsights-logging-logback:$APP_INSIGHTS_SDK_VERSION")

    project.dependencies.add("agentDeps", "com.microsoft.azure:applicationinsights-agent:$APP_INSIGHTS_AGENT_VERSION")
  }
}
