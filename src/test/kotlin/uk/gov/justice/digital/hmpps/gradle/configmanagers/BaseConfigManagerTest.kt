package uk.gov.justice.digital.hmpps.gradle.configmanagers

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.assertj.core.groups.Tuple
import org.gradle.api.JavaVersion
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.internal.extensibility.DefaultConvention
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.gradle.UnitTest

@Disabled("Disabled until tests are re-written to use gradle 7!")
class BaseConfigManagerTest : UnitTest() {

  @Test
  fun `Should apply maven repositories`() {
    assertThat(project.repositories).extracting<String> { it.name }.containsExactlyInAnyOrder("MavenLocal", "MavenRepo")
  }

  @Test
  fun `Should set the group`() {
    assertThat(project.group).isEqualTo("uk.gov.justice.digital.hmpps")
  }

  @Test
  fun `Should apply miscellaneous dependencies`() {
    assertThat(project.configurations.getByName("implementation").dependencies)
      .extracting<Tuple> { tuple(it.group, it.name) }
      .contains(
        tuple("com.fasterxml.jackson.module", "jackson-module-kotlin"),
        tuple("com.google.guava", "guava"),
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
    assertThat(javaPluginConvention.sourceCompatibility).isEqualTo(JavaVersion.VERSION_17)
  }
}
