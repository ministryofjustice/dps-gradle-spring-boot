package uk.gov.justice.digital.hmpps.gradle.configmanagers

import org.gradle.api.Project
import org.gradle.api.tasks.Copy

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
    project.dependencies.add("implementation", "net.logstash.logback:logstash-logback-encoder:6.3")
    project.dependencies.add("implementation", "com.microsoft.azure:applicationinsights-spring-boot-starter:2.6.0")
    project.dependencies.add("implementation", "com.microsoft.azure:applicationinsights-logging-logback:2.6.0")

    project.dependencies.add("agentDeps", "com.microsoft.azure:applicationinsights-agent:2.6.0")
  }

}