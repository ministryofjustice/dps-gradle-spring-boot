package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class KotlinPluginManagerTest {

  val project: Project = ProjectBuilder.builder().build()

  @BeforeEach
  fun `Create project`() {
    project.pluginManager.apply("uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot")
  }

  @Test
  fun `Should apply the Kotlin plugin`() {
    project.plugins.getPlugin(KotlinPluginWrapper::class.java)
  }

  @Test
  fun `Should apply Kotlin dependencies`() {
    assertThat(project.configurations.getByName("implementation").dependencies)
        .extracting("group", "name")
        .contains(
            tuple("org.jetbrains.kotlin", "kotlin-stdlib-jdk8"),
            tuple("org.jetbrains.kotlin", "kotlin-reflect")
        )
  }

  @Test
  fun `Should apply Kotlin test dependencies`() {
    assertThat(project.configurations.getByName("testImplementation").dependencies)
        .extracting("group", "name", "version")
        .contains(
            tuple("com.nhaarman.mockitokotlin2", "mockito-kotlin", "2.2.0")
        )
  }

  @Test
  fun `Should set jvm target for Kotlin tasks`() {
    assertThat((project.tasks.getByPath("compileKotlin") as KotlinCompile).kotlinOptions.jvmTarget).isEqualTo("11")
    assertThat((project.tasks.getByPath("compileTestKotlin") as KotlinCompile).kotlinOptions.jvmTarget).isEqualTo("11")
  }

}