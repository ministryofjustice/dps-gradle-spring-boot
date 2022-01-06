package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.gradle.UnitTest

class SpringBootPluginManagerTest : UnitTest() {
  @Test
  fun `Should apply Spring Boot standard libraries`() {
    assertThat(project.configurations.getByName("implementation").dependencies)
      .extracting<Tuple> { tuple(it.group, it.name) }
      .contains(
        tuple("org.springframework.boot", "spring-boot-starter-web"),
        tuple("org.springframework.boot", "spring-boot-starter-actuator"),
        tuple("com.github.timpeeters", "spring-boot-graceful-shutdown"),
        tuple("org.springframework.boot", "spring-boot-starter-validation")
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
}
