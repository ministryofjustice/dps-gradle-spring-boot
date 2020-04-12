package uk.gov.justice.digital.hmpps.gradle

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption

const val PROJECT_NAME = "spring-boot-project"

class DpsSpringBootPluginFuncTest {

  @TempDir
  lateinit var projectDir: File

  @BeforeEach
  fun `Create source files`() {
    makeBuildScript()
    makeSrcFile()
    makeSettingsScript()
  }

  @Test
  fun `Can create a Spring Boot jar`() {
    val result = buildProject("bootJar")

    assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
    val jar = File(projectDir, "build/libs/$PROJECT_NAME.jar")
    assertThat(jar.exists()).isTrue()
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
        class Application
  
        fun main(args: Array<String>) {
          runApplication<Application>(*args)
        }
      """.trimIndent()
    Files.writeString(srcFile.toPath(), srcFileScript)
  }

  private fun makeBuildScript() {
    val buildFile = File(projectDir, "build.gradle.kts")
    val buildScript = """
        import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
  
        plugins {
          kotlin("jvm") version "1.3.71"
          id("uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot") version "0.0.1-SNAPSHOT"
        }
        
        repositories {
          mavenLocal()
          mavenCentral()
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

  private fun buildProject(task: String): BuildResult {
    return GradleRunner.create()
      .withProjectDir(projectDir)
      .withArguments(task)
      .withPluginClasspath()
      .build()

  }
}