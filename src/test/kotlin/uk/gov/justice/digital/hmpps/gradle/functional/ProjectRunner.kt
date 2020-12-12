package uk.gov.justice.digital.hmpps.gradle.functional

import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.asStream

data class ProjectDetails(
  val projectDir: File,
  val projectName: String,
  val packageDir: String,
  val mainClassName: String,
  val mainClass: String,
  val buildScriptName: String,
  val buildScript: String,
  val settingsFileName: String,
  val testClass: String
) {
  override fun toString(): String = projectName
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

@Suppress("ComplexRedundantLet")
fun createAndRunJar(projectDetails: ProjectDetails): Process =
  makeProject(projectDetails)
    .run {
      with(projectDetails) {
        runJar(createJar(projectDir, projectName), mainClassName)
      }
    }

fun buildProject(projectDir: File, vararg arguments: String): BuildResult =
  projectBuilder(projectDir, *arguments).build()

fun buildProjectAndFail(projectDir: File, vararg arguments: String): BuildResult =
  projectBuilder(projectDir, *arguments).buildAndFail()

@Suppress("SimpleRedundantLet")
fun getDependencyVersion(projectDir: File, dependency: String): String =
  buildProject(projectDir, "dependencyInsight", "--dependency", dependency)
    .also { result -> assertThat(result.task(":dependencyInsight")?.outcome).isEqualTo(TaskOutcome.SUCCESS) }
    .let { result -> result.output.replace("\n", " ") }
    .let { flattenedResult -> findVersion(dependency, flattenedResult) }
    .let { (version) -> version.takeWhile { it != ' ' } }

private fun findVersion(dependency: String, flattenedResult: String): MatchResult.Destructured =
  Regex("$dependency:(.*)\\s").find(flattenedResult)!!.destructured

fun findJar(projectDir: File, partialJarName: String): File =
  Files.walk(Paths.get(projectDir.absolutePath + "/build/libs")).use { paths ->
    paths.filter { path -> path.toString().contains(partialJarName) }
      .findFirst()
      .map { jarPath -> jarPath.toFile() }
      .orElseThrow()
  }

fun findFile(projectDir: File, fileName: String): File =
  Files.walk(Paths.get(projectDir.absolutePath)).use { paths ->
    paths.filter { path -> path.toString().contains(fileName) }
      .findFirst()
      .map { filePath -> filePath.toFile() }
      .orElseThrow()
  }

private fun createJar(projectDir: File, projectName: String): File =
  buildProject(projectDir, "bootJar")
    .also { result -> assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS) }
    .let { findJar(projectDir, projectName) }
    .also { jar -> assertThat(jar.exists()).isTrue }

private fun runJar(jar: File, mainClassName: String): Process =
  ProcessBuilder("java", "-jar", jar.absolutePath).start()
    .also { process ->
      findIfStartedOk(process, mainClassName)
        .also { startedOk -> assertThat(startedOk).isTrue.withFailMessage("Unable to start the Spring Boot jar") }
    }

private fun findIfStartedOk(process: Process, mainClassName: String): Boolean =
  BufferedReader(InputStreamReader(process.inputStream))
    .let { outputReader ->
      outputReader.useLines {
        it.asStream()
          .peek { line -> println(line) }
          .anyMatch { line -> line.contains("Started ${mainClassName.substringBefore(".")}") }
      }
    }

private fun makeSrcFile(projectDir: File, packageDir: String, mainClassName: String, mainClass: String): Path =
  File(projectDir, packageDir)
    .also { srcDir -> srcDir.mkdirs() }
    .let { srcDir -> File(srcDir, mainClassName) }
    .let { srcFile -> Files.writeString(srcFile.toPath(), mainClass) }

private fun makeTestSrcFile(projectDir: File, packageDir: String, mainClassName: String, testClass: String): Path =
  makeSrcFile(
    projectDir,
    packageDir.replace("main", "test"),
    mainClassName.replace(".java", "Test.java").replace(".kt", "Test.kt"),
    testClass
  )

private fun makeBuildScript(projectDir: File, buildScriptName: String, buildScript: String): File =
  File(projectDir, buildScriptName)
    .also { buildFile -> Files.writeString(buildFile.toPath(), buildScript) }

private fun makeSettingsScript(projectDir: File, settingsFileName: String, projectName: String): Path {
  val settingsFile = File(projectDir, settingsFileName)
  val settingsScript =
    """
        pluginManagement {
          repositories {
            mavenLocal()
            gradlePluginPortal()
          }
        }
        rootProject.name = "$projectName"

    """.trimIndent()
  return Files.writeString(settingsFile.toPath(), settingsScript)
}

private fun makeGitRepo(projectDir: File) {
  FileRepositoryBuilder.create(File(projectDir, ".git"))
    .also { repo -> repo.create() }
    .let { repo -> Git(repo) }
    .also { git -> git.add().addFilepattern("*").call() }
    .also { git -> git.commit().setSign(false).setMessage("Commit everything").call() }
}

// To debug functional tests change the default value of debug to true
private fun projectBuilder(projectDir: File, vararg arguments: String, debug: Boolean = false): GradleRunner =
  GradleRunner.create()
    .withProjectDir(projectDir)
    .withArguments("clean", *arguments)
    .withPluginClasspath()
    .withDebug(debug)
