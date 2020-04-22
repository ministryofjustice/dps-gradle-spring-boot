package uk.gov.justice.digital.hmpps.gradle

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.jar.JarFile

class KotlinBuildFuncTest {

  companion object {

    @TempDir
    @JvmStatic
    lateinit var projectDir: File

    @BeforeAll
    @JvmStatic
    fun `Create and run project`() {
      makeProject(kotlinProjectDetails(projectDir))
    }

  }

  @Test
  fun `Spring dependency versions are defaulted from the dependency management plugin`() {
    val webVersion = getDependencyVersion(projectDir, "spring-boot-starter-web")
    val actuatorVersion = getDependencyVersion(projectDir, "spring-boot-starter-actuator")

    assertThat(webVersion).isEqualTo(actuatorVersion)
  }

  @Test
  fun `Manifest file contains project name and version`() {
    val result = buildProject(projectDir, "bootJar")
    assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val file = findJar(projectDir, "spring-boot-project-kotlin")
    val jarFile = JarFile(file)

    assertThat(jarFile.manifest.mainAttributes.getValue("Implementation-Version")).isEqualTo(LocalDate.now().format(DateTimeFormatter.ISO_DATE))
    assertThat(jarFile.manifest.mainAttributes.getValue("Implementation-Title")).isEqualTo("spring-boot-project-kotlin")
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
    val result = buildProject(projectDir, "tasks")
    assertThat(result.task(":tasks")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val suppressionFile = findFile(projectDir, DEPENDENCY_SUPPRESSION_FILENAME)
    assertThat(suppressionFile).exists()
  }

  @Test
  fun `The gradle version dependency dependencyUpdates task is available`() {
    val result = buildProject(projectDir, "dependencyUpdates", "-m")
    assertThat(result.output)
        .contains(":dependencyUpdates SKIPPED")
        .contains("SUCCESSFUL")
  }

  @Test
  fun `The application insights jar is copied into the build lib`() {
    val result = buildProject(projectDir, "assemble")
    assertThat(result.task(":assemble")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val file = findJar(projectDir, "applicationinsights-agent")
    assertThat(file).exists()
  }

}
