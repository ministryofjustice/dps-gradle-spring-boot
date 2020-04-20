package uk.gov.justice.digital.hmpps.gradle

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URL
import java.nio.file.Files
import kotlin.streams.asStream

const val PROJECT_NAME_JAVA = "spring-boot-project-java"
const val MAIN_CLASS_JAVA = "ApplicationJava"

class JavaFuncTest {

  @TempDir
  lateinit var projectDir: File

  @Test
  fun `Spring Boot jar is up and healthy`() {
    val healthResponse = URL("http://localhost:8080/actuator/health").readText()
    assertThat(healthResponse).isEqualTo("""{"status":"UP"}""")
  }

  @Test
  fun `Spring Boot info endpoint is available`() {
    val infoResponse = URL("http://localhost:8080/actuator/info").readText()
    assertThat(infoResponse).isEqualTo("{}")
  }

  companion object {

    @TempDir
    @JvmStatic
    lateinit var tempDir: File

    var jarProcess: Process? = null

    @BeforeAll
    @JvmStatic
    fun `Create and run project`() {
      makeBuildScript()
      makeSrcFile()
      makeSettingsScript()
      val jar = createJar()
      jarProcess = runJar(jar)
    }

    @AfterAll
    @JvmStatic
    fun `End running jar`() {
      jarProcess?.destroyForcibly()
    }

    private fun createJar(): File {
      val result = buildProject("bootJar")

      assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
      val jar = File(tempDir, "build/libs/$PROJECT_NAME_JAVA.jar")
      assertThat(jar.exists()).isTrue()

      return jar
    }

    private fun runJar(jar: File): Process {
      val process = ProcessBuilder("java", "-jar", jar.absolutePath).start()
      val outputReader = BufferedReader(InputStreamReader(process.inputStream))
      val startedOk = outputReader.useLines {
        it.asStream()
          .peek { line -> println(line) }
          .anyMatch { line -> line.contains("Started $MAIN_CLASS_JAVA") }
      }
      assertThat(startedOk).isTrue().withFailMessage("Unable to start the Spring Boot jar")
      return process
    }

    private fun makeSrcFile() {
      val srcDir = File(tempDir, "src/main/java/uk/gov/justice/digital/hmpps/app")
      srcDir.mkdirs()
      val srcFile = File(srcDir, "ApplicationJava.java")
      val srcFileScript = """
        package uk.gov.justice.digital.hmpps.app;

        import org.springframework.boot.SpringApplication;
        import org.springframework.boot.autoconfigure.SpringBootApplication;
  
        @SpringBootApplication
        public class $MAIN_CLASS_JAVA {
  
            public static void main(String[] args) {
                SpringApplication.run(ApplicationJava.class, args);
            }
        }
      """.trimIndent()
      Files.writeString(srcFile.toPath(), srcFileScript)
    }

    private fun makeBuildScript() {
      val buildFile = File(tempDir, "build.gradle.kts")
      val buildScript = """
        plugins {
          id("uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot") version "0.0.1-SNAPSHOT"
        }
      """.trimIndent()
      Files.writeString(buildFile.toPath(), buildScript)
    }

    private fun makeSettingsScript() {
      val settingsFile = File(tempDir, "settings.gradle.kts")
      val settingsScript = """
        rootProject.name = "$PROJECT_NAME_JAVA"
      """.trimIndent()
      Files.writeString(settingsFile.toPath(), settingsScript)
    }

    private fun buildProject(task: String): BuildResult {
      return GradleRunner.create()
        .withProjectDir(tempDir)
        .withArguments(task)
        .withPluginClasspath()
        .build()

    }
  }

}
