package uk.gov.justice.digital.hmpps.gradle.functional.pluginmanagers

import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import uk.gov.justice.digital.hmpps.gradle.functional.createAndRunJar
import uk.gov.justice.digital.hmpps.gradle.functional.javaProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.kotlinProjectDetails
import java.io.File
import java.net.URL

class JavaSpringBootPluginManagerJarTest : SpringBootPluginManagerJarTest() {

  companion object {
    @BeforeAll
    @JvmStatic
    fun `Create and run project`() {
      jarProcess = createAndRunJar(javaProjectDetails(projectDir))
    }
  }
}

class KotlinSpringBootPluginManagerJarTest : SpringBootPluginManagerJarTest() {

  companion object {
    @BeforeAll
    @JvmStatic
    fun `Create and run project`() {
      jarProcess = createAndRunJar(kotlinProjectDetails(projectDir))
    }
  }
}

abstract class SpringBootPluginManagerJarTest {

  companion object {

    @TempDir
    @JvmStatic
    lateinit var projectDir: File

    var jarProcess: Process? = null

    @AfterAll
    @JvmStatic
    fun `End running jar`() {
      jarProcess?.destroyForcibly()
    }
  }

  @Test
  fun `Spring Boot jar is up and healthy`() {
    val healthResponse = URL("http://localhost:8080/actuator/health").readText()
    assertThatJson(healthResponse).node("status").isEqualTo("UP")
  }

  @Test
  fun `Spring Boot info endpoint is available`() {
    val infoResponse = URL("http://localhost:8080/actuator/info").readText()
    assertThatJson(infoResponse).node("build.by").isEqualTo(System.getProperty("user.name"))
  }

  @Test
  fun `Spring Boot info endpoint contains git info`() {
    val infoResponse = URL("http://localhost:8080/actuator/info").readText()
    assertThatJson(infoResponse).node("git.branch").isNotNull
  }
}
