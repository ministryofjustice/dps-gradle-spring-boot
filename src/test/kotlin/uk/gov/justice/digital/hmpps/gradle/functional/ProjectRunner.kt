package uk.gov.justice.digital.hmpps.gradle.functional

import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
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
    val buildScriptName: String, val buildScript: String, val settingsFileName: String, val testClass: String) {
  override fun toString(): String {
    return "ProjectDetails(projectName='$projectName')"
  }
}

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
    makeTestSrcFile(projectDir, packageDir, mainClassName, testClass)
    makeSettingsScript(projectDir, settingsFileName, projectName)
    makeGitRepo(projectDir)
  }
}

fun getDependencyVersion(projectDir: File, dependency: String): String {
  val result = buildProject(projectDir, "dependencyInsight", "--dependency", dependency)
  assertThat(result.task(":dependencyInsight")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
  val (version) = Regex("org.springframework.boot:$dependency:(.*)\\s\\(selected by rule\\)").find(result.output)!!.destructured
  return version
}

fun findJar(projectDir: File, partialJarName: String): File {
  return Files.walk(Paths.get(projectDir.absolutePath + "/build/libs")).use { paths ->
    paths.filter { path -> path.toString().contains(partialJarName) }
        .findFirst()
        .map { jarPath -> jarPath.toFile() }
        .orElseThrow()
  }
}

fun findFile(projectDir: File, fileName: String): File {
  return Files.walk(Paths.get(projectDir.absolutePath)).use { paths ->
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

private fun makeTestSrcFile(projectDir: File, packageDir: String, mainClassName: String, testClass: String) {
  val srcDir = File(projectDir, packageDir.replace("main", "test"))
  srcDir.mkdirs()
  val srcFile = File(srcDir, mainClassName.replace(".java", "Test.java").replace(".kt", "Test.kt"))
  Files.writeString(srcFile.toPath(), testClass)
}

private fun makeBuildScript(projectDir: File, buildScriptName: String, buildScript: String) {
  val buildFile = File(projectDir, buildScriptName)
  Files.writeString(buildFile.toPath(), buildScript)
}

private fun makeSettingsScript(projectDir: File, settingsFileName: String, projectName: String) {
  val settingsFile = File(projectDir, settingsFileName)
  val settingsScript = """
        pluginManagement {
          repositories {
            mavenLocal()
            gradlePluginPortal()
          }
        }
        rootProject.name = "$projectName"
      """.trimIndent()
  Files.writeString(settingsFile.toPath(), settingsScript)
}

private fun makeGitRepo(projectDir: File) {
  val repo = FileRepositoryBuilder.create(File(projectDir, ".git"))
  repo.create()
  val git = Git(repo)
  git.add().addFilepattern("*").call()
  git.commit().setSign(false).setMessage("Commit everything").call()
}

fun buildProject(projectDir: File, vararg arguments: String) =
    projectBuilder(projectDir, *arguments).build()

fun buildProjectAndFail(projectDir: File, vararg arguments: String) =
    projectBuilder(projectDir, *arguments).buildAndFail()

private fun projectBuilder(projectDir: File, vararg arguments: String) =
    GradleRunner.create()
        .withProjectDir(projectDir)
        .withArguments("clean", *arguments)
        .withPluginClasspath()
