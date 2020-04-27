package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.assertj.core.groups.Tuple
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.tasks.buildinfo.BuildInfo
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class SpringBootPluginManagerTest {

  val project: Project = ProjectBuilder.builder().build()

  @BeforeEach
  fun `Create project`() {
    project.pluginManager.apply("uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot")
  }
  @Test
  fun `Should apply the Spring Boot plugin`() {
    project.plugins.getPlugin(SpringBootPlugin::class.java)
  }

  @Test
  fun `Should apply Spring Boot standard libraries`() {
    assertThat(project.configurations.getByName("implementation").dependencies)
        .extracting("group", "name", "version")
        .contains(
            tuple("org.springframework.boot", "spring-boot-starter-web", null),
            tuple("org.springframework.boot", "spring-boot-starter-actuator", null),
            tuple("com.github.timpeeters", "spring-boot-graceful-shutdown", "2.2.1")
        )
  }

  @Test
  fun `Should apply Spring Boot test Dependencies`() {
    assertThat(project.configurations.getByName("testImplementation").dependencies)
        .extracting("group", "name", "version")
        .contains(
            Tuple.tuple("org.springframework.boot", "spring-boot-starter-test", null)

        )
  }

  @Test
  fun `Should set build info`() {
    val properties = (project.tasks.getByPath("bootBuildInfo") as BuildInfo).properties
    assertThat(properties.additional).extracting("by").isEqualTo(System.getProperty("user.name"))
    assertThat(properties.additional).extracting("operatingSystem").isNotNull()
    assertThat(properties.additional).extracting("machine").isNotNull()

    assertThat(LocalDate.ofInstant(properties.time, ZoneId.systemDefault())).isEqualTo(LocalDate.now())

    assertThat(properties.version).isEqualTo(LocalDate.now().format(DateTimeFormatter.ISO_DATE))
  }

  @Test
  fun `Should set manifest version and title in BootJar`() {
    val manifestAttributes = (project.tasks.getByName("bootJar") as BootJar).manifest.attributes

    assertThat(manifestAttributes).extracting("Implementation-Version", "Implementation-Title").contains(project.version, project.name)
  }

}