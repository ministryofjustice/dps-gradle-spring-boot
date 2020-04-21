package uk.gov.justice.digital.hmpps.gradle

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.assertj.core.groups.Tuple
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.internal.extensibility.DefaultConvention
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.tasks.buildinfo.BuildInfo

class DpsSpringBootPluginTest {

  val project: Project = ProjectBuilder.builder().build()

  @BeforeEach
  fun `Create project`() {
    project.pluginManager.apply("uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot")
  }

  @Nested
  inner class Plugins {

    @Test
    fun `Should apply the Spring Boot plugin`() {
      val thrown = catchThrowable { project.plugins.getPlugin(SpringBootPlugin::class.java) }
      assertThat(thrown).isNull()
    }

    @Test
    fun `Should apply the Kotlin plugin`() {
      val thrown = catchThrowable { project.plugins.getPlugin(KotlinPluginWrapper::class.java) }
      assertThat(thrown).isNull()
    }

    @Test
    fun `Should apply the Java plugin`() {
      val thrown = catchThrowable { project.plugins.getPlugin(JavaPlugin::class.java) }
      assertThat(thrown).isNull()
    }

    @Test
    fun `Should apply the Spring Dependency plugin`() {
      val thrown = catchThrowable { project.plugins.getPlugin(DependencyManagementPlugin::class.java) }
      assertThat(thrown).isNull()
    }
  }

  @Test
  fun `Should apply maven repositories`() {
    assertThat(project.repositories).extracting("name").containsExactlyInAnyOrder("MavenLocal", "MavenRepo")
  }

  @Test
  fun `Should set the group`() {
    assertThat(project.group).isEqualTo("uk.gov.justice.digital.hmpps")
  }

  @Nested
  inner class Dependencies {

    @Test
    fun `Should apply Kotlin standard libraries`() {
      assertThat(project.configurations.getByName("implementation").dependencies)
        .extracting("group", "name")
        .contains(
          Tuple.tuple("org.jetbrains.kotlin", "kotlin-stdlib-jdk8"),
          Tuple.tuple("org.jetbrains.kotlin", "kotlin-reflect")
        )
    }

    @Test
    fun `Should apply Spring Boot standard libraries`() {
      assertThat(project.configurations.getByName("implementation").dependencies)
        .extracting("group", "name")
        .contains(
          Tuple.tuple("org.springframework.boot", "spring-boot-starter-web"),
          Tuple.tuple("org.springframework.boot", "spring-boot-starter-actuator")
        )
    }
  }

  @Nested
  inner class JvmVersion {

    @Test
    fun `Should set jvm target for Kotlin tasks`() {
      assertThat((project.tasks.getByPath("compileKotlin") as KotlinCompile).kotlinOptions.jvmTarget).isEqualTo("11")
      assertThat((project.tasks.getByPath("compileTestKotlin") as KotlinCompile).kotlinOptions.jvmTarget).isEqualTo("11")
    }

    @Test
    fun `Should default Java version on the Java Plugin`() {
      val javaConvention = project.extensions as DefaultConvention
      val javaPluginConvention = javaConvention.plugins["java"] as JavaPluginConvention
      assertThat(javaPluginConvention.sourceCompatibility).isEqualTo(JavaVersion.VERSION_11)
    }

  }

  @Test
  fun `Should set the build info`() {
    val additionalProperties = (project.tasks.getByPath("bootBuildInfo") as BuildInfo).properties.additional
    assertThat(additionalProperties).extracting("by").isEqualTo(System.getProperty("user.name"))
    assertThat(additionalProperties).extracting("operatingSystem").isNotNull()
    assertThat(additionalProperties).extracting("machine").isNotNull()

    assertThat((project.tasks.getByPath("bootBuildInfo") as BuildInfo).properties.time).isNotNull()
  }

}
