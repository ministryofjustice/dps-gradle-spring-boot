package uk.gov.justice.digital.hmpps.gradle

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Files

private const val ADDITIONAL_SUPPRESSION_FILENAME = "jose-nimbus-suppression.xml"

class DependencySuppressionFuncTest {

  companion object {

    @TempDir
    @JvmStatic
    lateinit var projectDir: File

    @BeforeAll
    @JvmStatic
    fun `Create and run project with additional dependency check suppression file`() {
      makeAdditionalSuppressionFile(projectDir)
      makeProject(javaProjectDetails(projectDir).copy(buildScript = """
          plugins {
            id("uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot") version "0.0.1-SNAPSHOT"
          }
           
          dependencies {
            implementation("org.springframework.boot:spring-boot-starter-security")   // CVEs from this lib are suppressed in the plugin's DEPENDENCY_SUPPRESSION_FILENAME
            implementation("com.nimbusds:nimbus-jose-jwt:7.8")                        // CVEs from this lib are suppressed in ADDITIONAL_SUPPRESSION_FILENAME
          }
          
          dependencyCheck {
            suppressionFiles.add("$projectDir/$ADDITIONAL_SUPPRESSION_FILENAME")      // We only need the project directory here because the GradleRunner's working directory is not the same as the project directory.  Normally we run Gradle tasks from the project directory so the files will not need the directory prefix.
          }
      """.trimIndent()))
    }
  }

  @Test
  fun `Additional suppression file should be applied and not override plugin suppression file`() {
    val result = buildProject(projectDir, "dependencyCheckAnalyze")
    assertThat(result.task(":dependencyCheckAnalyze")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
  }

}

private fun makeAdditionalSuppressionFile(projectDir: File) {
  val suppressionFile = File(projectDir, ADDITIONAL_SUPPRESSION_FILENAME).toPath()
  Files.writeString(suppressionFile, """
    <?xml version="1.0" encoding="UTF-8"?>
    <suppressions xmlns="https://jeremylong.github.io/DependencyCheck/dependency-suppression.1.3.xsd">
      <suppress>
        <notes><![CDATA[
          file name: nimbus-jose-jwt-7.8.jar
          ]]></notes>
        <packageUrl regex="true">^pkg:maven/com\.nimbusds/nimbus\-jose\-jwt@.*${'$'}</packageUrl>
        <cve>CVE-2019-17195</cve>
      </suppress>
    </suppressions>
  """.trimIndent())
}
