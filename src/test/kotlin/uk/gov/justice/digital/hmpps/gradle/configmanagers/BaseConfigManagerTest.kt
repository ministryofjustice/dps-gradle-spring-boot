package uk.gov.justice.digital.hmpps.gradle.configmanagers

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.internal.extensibility.DefaultConvention
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BaseConfigManagerTest {

  private val project: Project = ProjectBuilder.builder().build()

  @BeforeEach
  fun `Create project`() {
    project.pluginManager.apply("uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot")
  }

  @Test
  fun `Should apply maven repositories`() {
    assertThat(project.repositories).extracting("name").containsExactlyInAnyOrder("MavenLocal", "MavenRepo")
  }

  @Test
  fun `Should set the group`() {
    assertThat(project.group).isEqualTo("uk.gov.justice.digital.hmpps")
  }

  @Test
  fun `Should apply miscellaneous dependencies`() {
    assertThat(project.configurations.getByName("implementation").dependencies)
        .extracting("group", "name", "version")
        .contains(
            tuple("com.fasterxml.jackson.module", "jackson-module-kotlin", null),
            tuple("com.google.guava", "guava", "29.0-jre")
        )
  }

  @Test
  fun `Should apply the Java plugin`() {
    project.plugins.getPlugin(JavaPlugin::class.java)
  }

  @Test
  fun `Should default Java version on the Java Plugin`() {
    val javaConvention = project.extensions as DefaultConvention
    val javaPluginConvention = javaConvention.plugins["java"] as JavaPluginConvention
    assertThat(javaPluginConvention.sourceCompatibility).isEqualTo(JavaVersion.VERSION_11)
  }

}