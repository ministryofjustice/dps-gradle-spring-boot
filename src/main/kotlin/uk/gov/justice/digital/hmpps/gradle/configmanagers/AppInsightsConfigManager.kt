package uk.gov.justice.digital.hmpps.gradle.configmanagers

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import uk.gov.justice.digital.hmpps.gradle.ConfigManager

private const val APP_INSIGHTS_VERSION = "3.4.17"
const val OPENTELEMETRY_VERSION = "1.29.0"

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
    project.dependencies.add("implementation", "com.microsoft.azure:applicationinsights-core:$APP_INSIGHTS_VERSION")
    // version controlled by spring opentelemetry.version instead - see DependencyManagementPluginManager
    project.dependencies.add("implementation", "io.opentelemetry:opentelemetry-api")

    project.dependencies.add("agentDeps", "com.microsoft.azure:applicationinsights-agent:$APP_INSIGHTS_VERSION")
  }
}
