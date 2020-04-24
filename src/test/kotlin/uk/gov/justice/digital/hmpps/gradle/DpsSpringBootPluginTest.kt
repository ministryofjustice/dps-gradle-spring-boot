package uk.gov.justice.digital.hmpps.gradle

import com.github.benmanes.gradle.versions.VersionsPlugin
import com.gorylenko.GitPropertiesPlugin
import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.assertj.core.groups.Tuple
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.Copy
import org.gradle.internal.extensibility.DefaultConvention
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.owasp.dependencycheck.gradle.DependencyCheckPlugin
import org.owasp.dependencycheck.gradle.extension.DependencyCheckExtension
import org.owasp.dependencycheck.reporting.ReportGenerator
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.tasks.buildinfo.BuildInfo
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter.ISO_DATE

class DpsSpringBootPluginTest {

  val project: Project = ProjectBuilder.builder().build()

  @BeforeEach
  fun `Create project`() {
    project.pluginManager.apply("uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot")
  }

  @Nested
  inner class Plugins {

    // All of these tests throw an exception if they fail - hence no assertions
    @Test
    fun `Should apply the Spring Boot plugin`() {
      project.plugins.getPlugin(SpringBootPlugin::class.java)
    }

    @Test
    fun `Should apply the Kotlin plugin`() {
      project.plugins.getPlugin(KotlinPluginWrapper::class.java)
    }

    @Test
    fun `Should apply the Java plugin`() {
      project.plugins.getPlugin(JavaPlugin::class.java)
    }

    @Test
    fun `Should apply the Spring Dependency plugin`() {
      project.plugins.getPlugin(DependencyManagementPlugin::class.java)
    }

    @Test
    fun `Should apply the Owasp Dependency check plugin`() {
      project.plugins.getPlugin(DependencyCheckPlugin::class.java)
    }

    @Test
    fun `Should apply the gradle versions check plugin`() {
      project.plugins.getPlugin(VersionsPlugin::class.java)
    }

    @Test
    fun `Should apply the gradle git properties plugin`() {
      project.plugins.getPlugin(GitPropertiesPlugin::class.java)
    }
  }

  @Test
  fun `Should apply maven repositories`() {
    assertThat(project.repositories)
        .extracting<String> { it.name }
        .containsExactlyInAnyOrder("MavenLocal", "MavenRepo")
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
          .extracting<Tuple> { tuple(it.group, it.name) }
          .contains(
              tuple("org.jetbrains.kotlin", "kotlin-stdlib-jdk8"),
              tuple("org.jetbrains.kotlin", "kotlin-reflect")
          )
    }

    @Test
    fun `Should apply Spring Boot standard libraries`() {
      assertThat(project.configurations.getByName("implementation").dependencies)
          .extracting<Tuple> { tuple(it.group, it.name) }
          .contains(
              tuple("org.springframework.boot", "spring-boot-starter-web"),
              tuple("org.springframework.boot", "spring-boot-starter-actuator")
          )
    }

    @Test
    fun `Should apply logging libraries`() {
      assertThat(project.configurations.getByName("implementation").dependencies)
          .extracting<Tuple> { tuple(it.group, it.name, it.version) }
          .contains(
              tuple("net.logstash.logback", "logstash-logback-encoder", "6.3"),
              tuple("com.microsoft.azure", "applicationinsights-spring-boot-starter", "2.6.0"),
              tuple("com.microsoft.azure", "applicationinsights-logging-logback", "2.6.0")
          )
    }

    @Test
    fun `Should apply miscellaneous dependencies`() {
      assertThat(project.configurations.getByName("implementation").dependencies)
          .extracting<Tuple> { tuple(it.group, it.name, it.version) }
          .contains(
              tuple("com.github.timpeeters", "spring-boot-graceful-shutdown", "2.2.1"),
              tuple("com.fasterxml.jackson.module", "jackson-module-kotlin", null),
              tuple("com.google.guava", "guava", "29.0-jre")
          )
    }

    @Test
    fun `Should apply Test Dependencies`() {
      assertThat(project.configurations.getByName("testImplementation").dependencies)
          .extracting<Tuple> { tuple(it.group, it.name, it.version) }
          .contains(
              Tuple.tuple("org.springframework.boot", "spring-boot-starter-test", null),
              Tuple.tuple("com.nhaarman.mockitokotlin2", "mockito-kotlin", "2.2.0")
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
  fun `Should set build info`() {
    val properties = (project.tasks.getByPath("bootBuildInfo") as BuildInfo).properties
    assertThat(properties.additional).extracting("by").isEqualTo(System.getProperty("user.name"))
    assertThat(properties.additional).extracting("operatingSystem").isNotNull()
    assertThat(properties.additional).extracting("machine").isNotNull()

    assertThat(LocalDate.ofInstant(properties.time, ZoneId.systemDefault())).isEqualTo(LocalDate.now())

    assertThat(properties.version).isEqualTo(LocalDate.now().format(ISO_DATE))
  }

  @Test
  fun `Should set manifest version and title in BootJar`() {
    val manifestAttributes = (project.tasks.getByName("bootJar") as BootJar).manifest.attributes

    assertThat(manifestAttributes).extracting("Implementation-Version", "Implementation-Title").contains(project.version, project.name)
  }

  @Test
  fun `Should apply owasp dependency check configuration`() {
    val extension = project.extensions.getByName("dependencyCheck") as DependencyCheckExtension
    assertThat(extension.failBuildOnCVSS).isEqualTo(5f)
    assertThat(extension.suppressionFiles).containsExactly(DEPENDENCY_SUPPRESSION_FILENAME)
    assertThat(extension.format).isEqualTo(ReportGenerator.Format.ALL)
    assertThat(extension.analyzers.assemblyEnabled).isFalse()
  }

  @Nested
  inner class AgentDeps {
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
      assertThat(dependsOn).extracting<String> { it.name}.contains("copyAgent")
    }
  }

}
