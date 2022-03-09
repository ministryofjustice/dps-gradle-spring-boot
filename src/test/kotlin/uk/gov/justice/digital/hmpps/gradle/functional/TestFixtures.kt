package uk.gov.justice.digital.hmpps.gradle.functional

import java.io.File

fun javaProjectDetails(projectDir: File) =
  ProjectDetails(
    projectName = "spring-boot-project-java",
    projectDir = projectDir,
    mainClassName = "Application.java",
    mainClass =
    """
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
    buildScript =
    """
          plugins {
            id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
          }
    """.trimIndent(),
    settingsFileName = "settings.gradle",
    testClass =
    """
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

fun kotlinProjectDetails(projectDir: File, addKotlinerHookTask: Boolean = false): ProjectDetails {
  val task = if (addKotlinerHookTask)
    """tasks.register<org.jmailen.gradle.kotlinter.tasks.InstallPrePushHookTask>("installKotlinterPrePushHook") { }"""
  else
    ""

  return ProjectDetails(
    projectName = "spring-boot-project-kotlin",
    projectDir = projectDir,
    mainClassName = "Application.kt",
    mainClass =
    """
        package uk.gov.justice.digital.hmpps.app
  
        import org.springframework.boot.autoconfigure.SpringBootApplication
        import org.springframework.boot.runApplication
  
        @SpringBootApplication
        open class Application
  
        fun main(args: Array<String>) {
          runApplication<Application>(*args)
        }

    """.trimIndent(),
    buildScriptName = "build.gradle.kts",
    buildScript =
    """
          plugins {
            id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1"
          }

          $task

    """.trimIndent(),

    settingsFileName = "settings.gradle.kts",
    testClass =
    """
        package uk.gov.justice.digital.hmpps.app
        
        import org.assertj.core.api.Assertions.assertThat
        import org.junit.jupiter.api.Test
        
        class ApplicationTest {
          @Test
          fun `A Test`() {
            assertThat("anything").isEqualTo("anything")
          }
        }

    """.trimIndent()
  )
}
