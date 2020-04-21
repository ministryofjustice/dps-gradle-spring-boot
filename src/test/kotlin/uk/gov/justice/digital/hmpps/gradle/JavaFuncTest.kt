package uk.gov.justice.digital.hmpps.gradle

import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_DATE
import java.util.jar.JarFile

class JavaFuncTest {

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
  fun `Spring dependency versions are defaulted from the dependency management plugin`() {
    val webVersion = getDependencyVersion(projectDir, "spring-boot-starter-web")
    val actuatorVersion = getDependencyVersion(projectDir, "spring-boot-starter-actuator")

    assertThat(webVersion).isEqualTo(actuatorVersion)
  }

  @Test
  fun `Manifest file contains project name and version`() {
    val file = findJar(projectDir, "spring-boot-project-java")
    val jarFile = JarFile(file)
    assertThat(jarFile.manifest.mainAttributes.getValue("Implementation-Version")).isEqualTo(LocalDate.now().format(ISO_DATE))
    assertThat(jarFile.manifest.mainAttributes.getValue("Implementation-Title")).isEqualTo("spring-boot-project-java")
  }

  @Test
  fun `The Owasp dependency analyze task is available`() {
    val result = buildProject(projectDir, "dependencyCheckAnalyze", "-m")
    assertThat(result.output)
        .contains(":dependencyCheckAnalyze SKIPPED")
        .contains("SUCCESSFUL")
  }

  @Test
  fun `The Owasp dependency check suppression file is copied into the project`() {
    val suppressionFile = findFile(projectDir, "dependency-check-suppress-spring.xml")
    assertThat(suppressionFile).exists()
  }

  @Test
  fun `The gradle version dependency dependencyUpdates task is available`() {
    val result = buildProject(projectDir, "dependencyUpdates", "-m")
    assertThat(result.output)
        .contains(":dependencyUpdates SKIPPED")
        .contains("SUCCESSFUL")
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
        settingsFileName = "settings.gradle"
    )