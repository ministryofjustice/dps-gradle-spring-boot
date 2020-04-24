package uk.gov.justice.digital.hmpps.gradle

import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.net.URL

class JavaRunFuncTest {

  companion object {

    @TempDir
    @JvmStatic
    lateinit var projectDir: File

    var jarProcess: Process? = null

    @BeforeAll
    @JvmStatic
    fun `Create and run project`() {
      jarProcess = createAndRunJar(javaProjectDetails(projectDir))
    }

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
    assertThatJson(infoResponse).node("git.branch").isEqualTo("master")
  }

}

fun javaProjectDetails(projectDir: File) =
    ProjectDetails(
        projectName = "spring-boot-project-java",
        projectDir = projectDir,
        packageDir = "src/main/java/uk/gov/justice/digital/hmpps/app",
        mainClassName = "Application.java",
        mainClass = """
          package uk.gov.justice.digital.hmpps.app;
  
          import org.springframework.boot.SpringApplication;
          import org.springframework.boot.autoconfigure.SpringBootApplication;
    
          @SpringBootApplication
          public class Application {
    
              public static void main(String[] args) {
                  SpringApplication.run(Application.class, args);
              }
          }
        """.trimIndent(),
        buildScriptName = "build.gradle",
        buildScript = """
          plugins {
            id("uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot") version "0.0.1-SNAPSHOT"
          }
        """.trimIndent(),
        settingsFileName = "settings.gradle",
        testClass = """
          package uk.gov.justice.digital.hmpps.app;
          
          import org.junit.jupiter.api.Test;
          
          import static org.assertj.core.api.Assertions.assertThat;
          
          public class ApplicationTest {
              @Test
              public void aTest() {
                  assertThat("anything").isEqualTo("anything");
              }
          }
        """.trimIndent()
    )