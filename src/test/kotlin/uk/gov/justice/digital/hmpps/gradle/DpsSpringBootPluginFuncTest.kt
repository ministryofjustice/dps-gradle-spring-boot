package uk.gov.justice.digital.hmpps.gradle

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
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

  var jarProcess: Process? = null

  @BeforeEach
  fun `Create source files`() {
    makeBuildScript()
    makeSrcFile()
    makeSettingsScript()
    makeSpringPropertiesFile()
  }

  @AfterEach
  fun `End running jar`() {
    jarProcess?.destroyForcibly()
  }

  @Test
  fun `Can create and start a Spring Boot jar`() {
    val result = buildProject("bootJar")

    assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
    val jar = File(projectDir, "build/libs/$PROJECT_NAME.jar")
    assertThat(jar.exists()).isTrue()

    jarProcess = runJar(jar)
    val healthResponse = URL("http://localhost:8080/actuator/health").readText()
    assertThat(healthResponse).isEqualTo("""{"status":"UP"}""")
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
    val srcDir = File(projectDir, "src/main/kotlin/example")
    srcDir.mkdirs()
    val srcFile = File(srcDir, "Application.kt")
    val srcFileScript = """
        package example
  
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

  // TODO DT-727 Next task is to move everything below the plugins section to the plugin and make sure that these tests still pass
  private fun makeBuildScript() {
    val buildFile = File(projectDir, "build.gradle.kts")
    val buildScript = """
        import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
  
        plugins {
          kotlin("jvm") version "1.3.71"
          id("uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot") version "0.0.1-SNAPSHOT"
        }
  
        tasks.withType<KotlinCompile> {
          kotlinOptions {
            jvmTarget = "11"
          }
        }
  
        group = "example"
        
        dependencies {
          implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
          implementation("org.jetbrains.kotlin:kotlin-reflect")
        
          implementation("org.springframework.boot:spring-boot-starter-web:2.2.6.RELEASE")
          implementation("org.springframework.boot:spring-boot-starter-actuator:2.2.6.RELEASE")
        }
  
      """.trimIndent()
    Files.writeString(buildFile.toPath(), buildScript)
  }

  private fun makeSettingsScript() {
    val settingsFile = File(projectDir, "settings.gradle.kts")
    val settingsScript = """
        rootProject.name = "$PROJECT_NAME"
      """.trimIndent()
    Files.writeString(settingsFile.toPath(), settingsScript)
  }

  private fun makeSpringPropertiesFile() {
    val propertiesDir = File(projectDir, "src/main/kotlin/resource")
    propertiesDir.mkdirs()
    val propertiesFile = File(projectDir, "application.yaml")
    val properties = """
        management:
          endpoints:
            web:
              base-path: /
              exposure:
                include: 'health'
      """.trimIndent()
    Files.writeString(propertiesFile.toPath(), properties)
  }

  private fun buildProject(task: String): BuildResult {
    return GradleRunner.create()
      .withProjectDir(projectDir)
      .withArguments(task)
      .withPluginClasspath()
      .build()

  }
}
