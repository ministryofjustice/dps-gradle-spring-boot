package uk.gov.justice.digital.hmpps.gradle

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.asStream

data class ProjectDetails(
    val projectDir: File, val projectName: String, val packageDir: String, val mainClassName: String, val mainClass: String,
    val buildScriptName: String, val buildScript: String, val settingsFileName: String
)

fun createAndRunJar(projectDetails: ProjectDetails): Process {
  makeProject(projectDetails)
  with(projectDetails) {
    val jar = createJar(projectDir, projectName)
    return runJar(jar, mainClassName)
  }
}

fun makeProject(projectDetails: ProjectDetails) {
  with(projectDetails) {
    makeBuildScript(projectDir, buildScriptName, buildScript)
    makeSrcFile(projectDir, packageDir, mainClassName, mainClass)
    makeSettingsScript(projectDir, settingsFileName, projectName)
  }
}

fun getDependencyVersion(projectDir: File, dependency: String): String {
  val result = buildProject(projectDir, "dependencyInsight", "--dependency", dependency)
  assertThat(result.task(":dependencyInsight")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
  val (version) = Regex("org.springframework.boot:$dependency:(.*)\\s\\(selected by rule\\)").find(result.output)!!.destructured
  return version
}

fun findJar(projectDir: File, projectName: String): File {
  return Files.walk(Paths.get(projectDir.absolutePath + "/build/libs")).use { paths ->
    paths.filter { path -> path.toString().contains(projectName) }
        .findFirst()
        .map { jarPath -> jarPath.toFile() }
        .orElseThrow()
  }
}

fun findFile(projectDir: File, fileName: String): File {
  return Files.walk(Paths.get(projectDir.absolutePath )).use { paths ->
    paths.filter { path -> path.toString().contains(fileName) }
        .findFirst()
        .map { filePath -> filePath.toFile() }
        .orElseThrow()
  }
}

private fun createJar(projectDir: File, projectName: String): File {
  val result = buildProject(projectDir, "bootJar")

  assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
  val jar = findJar(projectDir, projectName)
  assertThat(jar.exists()).isTrue()

  return jar
}

private fun runJar(jar: File, mainClassName: String): Process {
  val process = ProcessBuilder("java", "-jar", jar.absolutePath).start()
  val outputReader = BufferedReader(InputStreamReader(process.inputStream))
  val startedOk = outputReader.useLines {
    it.asStream()
        .peek { line -> println(line) }
        .anyMatch { line -> line.contains("Started ${mainClassName.substringBefore(".")}") }
  }
  assertThat(startedOk).isTrue().withFailMessage("Unable to start the Spring Boot jar")
  return process
}

private fun makeSrcFile(projectDir: File, packageDir: String, mainClassName: String, mainClass: String) {
  val srcDir = File(projectDir, packageDir)
  srcDir.mkdirs()
  val srcFile = File(srcDir, mainClassName)
  Files.writeString(srcFile.toPath(), mainClass)
}

private fun makeBuildScript(projectDir: File, buildScriptName: String, buildScript: String) {
  val buildFile = File(projectDir, buildScriptName)
  Files.writeString(buildFile.toPath(), buildScript)
}

private fun makeSettingsScript(projectDir: File, settingsFileName: String, projectName: String) {
  val settingsFile = File(projectDir, settingsFileName)
  val settingsScript = """
        rootProject.name = "$projectName"
      """.trimIndent()
  Files.writeString(settingsFile.toPath(), settingsScript)
}

fun buildProject(projectDir: File, vararg arguments: String): BuildResult {
  return GradleRunner.create()
      .withProjectDir(projectDir)
      .withArguments(*arguments)
      .withPluginClasspath()
      .build()
}
