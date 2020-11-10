package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.assertj.core.groups.Tuple
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.gradle.UnitTest

class KotlinPluginManagerTest : UnitTest() {

  @Test
  fun `Should apply the Kotlin plugin`() {
    project.plugins.getPlugin(KotlinPluginWrapper::class.java)
  }

  @Test
  fun `Should apply Kotlin dependencies`() {
    assertThat(project.configurations.getByName("implementation").dependencies)
      .extracting<Tuple> { tuple(it.group, it.name) }
      .contains(
        tuple("org.jetbrains.kotlin", "kotlin-reflect")
      )
  }

  @Test
  fun `Should apply Kotlin test dependencies`() {
    assertThat(project.configurations.getByName("testImplementation").dependencies)
      .extracting<Tuple> { tuple(it.group, it.name) }
      .contains(
        tuple("com.nhaarman.mockitokotlin2", "mockito-kotlin")
      )
  }

  @Test
  fun `Should set jvm target for Kotlin tasks`() {
    assertThat((project.tasks.getByPath("compileKotlin") as KotlinCompile).kotlinOptions.jvmTarget).isEqualTo("11")
    assertThat((project.tasks.getByPath("compileTestKotlin") as KotlinCompile).kotlinOptions.jvmTarget).isEqualTo("11")
  }
}
