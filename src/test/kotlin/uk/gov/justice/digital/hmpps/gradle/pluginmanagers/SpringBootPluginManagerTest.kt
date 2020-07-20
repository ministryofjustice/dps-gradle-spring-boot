package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Test
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.tasks.buildinfo.BuildInfo
import org.springframework.boot.gradle.tasks.bundling.BootJar
import uk.gov.justice.digital.hmpps.gradle.UnitTest
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class SpringBootPluginManagerTest : UnitTest() {

  @Test
  fun `Should apply the Spring Boot plugin`() {
    project.plugins.getPlugin(SpringBootPlugin::class.java)
  }

  @Test
  fun `Should apply Spring Boot standard libraries`() {
    assertThat(project.configurations.getByName("implementation").dependencies)
        .extracting<Tuple> { tuple(it.group, it.name, it.version) }
        .contains(
            tuple("org.springframework.boot", "spring-boot-starter-web", null),
            tuple("org.springframework.boot", "spring-boot-starter-actuator", null),
            tuple("com.github.timpeeters", "spring-boot-graceful-shutdown", "2.2.2"),
            tuple("org.springframework.boot", "spring-boot-starter-validation", null),
            tuple("org.apache.tomcat.embed", "tomcat-embed-core", "9.0.37"),
            tuple("org.apache.tomcat.embed", "tomcat-embed-websocket", "9.0.37")
        )
  }

  @Test
  fun `Should apply Spring Boot test Dependencies`() {
    assertThat(project.configurations.getByName("testImplementation").dependencies)
        .extracting<Tuple> { tuple(it.group, it.name) }
        .contains(
            Tuple.tuple("org.springframework.boot", "spring-boot-starter-test")

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
