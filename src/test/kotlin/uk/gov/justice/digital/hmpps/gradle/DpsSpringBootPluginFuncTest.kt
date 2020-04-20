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

const val PROJECT_NAME = "spring-boot-project"
const val MAIN_CLASS = "Application"

class DpsSpringBootPluginFuncTest {

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
    fun `Create source files`() {
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
      val jar = File(tempDir, "build/libs/$PROJECT_NAME.jar")
      assertThat(jar.exists()).isTrue()

      return jar
    }

    private fun runJar(jar: File): Process {
      val process = ProcessBuilder("java", "-jar", jar.absolutePath).start()
      val outputReader = BufferedReader(InputStreamReader(process.inputStream))
      val startedOk = outputReader.useLines {
        it.asStream()
          .peek { line -> println(line) }
          .anyMatch { line -> line.contains("Started ${MAIN_CLASS}Kt") }
      }
      assertThat(startedOk).isTrue().withFailMessage("Unable to start the Spring Boot jar")
      return process
    }

    private fun makeSrcFile() {
      val srcDir = File(tempDir, "src/main/kotlin/uk/gov/justice/digital/hmpps/app")
      srcDir.mkdirs()
      val srcFile = File(srcDir, "Application.kt")
      val srcFileScript = """
        package uk.gov.justice.digital.hmpps.app
  
        import org.springframework.boot.autoconfigure.SpringBootApplication
        import org.springframework.boot.runApplication
  
        @SpringBootApplication
        open class $MAIN_CLASS
  
        fun main(args: Array<String>) {
          runApplication<Application>(*args)
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
        rootProject.name = "$PROJECT_NAME"
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
