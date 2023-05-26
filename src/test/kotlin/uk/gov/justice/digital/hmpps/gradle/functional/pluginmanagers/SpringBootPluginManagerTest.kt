package uk.gov.justice.digital.hmpps.gradle.functional.pluginmanagers

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import uk.gov.justice.digital.hmpps.gradle.functional.GradleBuildTest
import uk.gov.justice.digital.hmpps.gradle.functional.ProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.buildProject
import uk.gov.justice.digital.hmpps.gradle.functional.buildProjectAndFail
import uk.gov.justice.digital.hmpps.gradle.functional.findJar
import uk.gov.justice.digital.hmpps.gradle.functional.javaProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.kotlinProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.makeProject
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.jar.JarEntry
import java.util.jar.JarFile

class SpringBootPluginManagerTest : GradleBuildTest() {

  companion object {
    @JvmStatic
    fun projectDetailsWithJunit4TestsAndDependency() = listOf(
      Arguments.of(
        javaProjectDetails(projectDir).copy(
          buildScript = javaJunit4Dependency(),
          testClass = javaJunit4Test(),
        ),
      ),
      Arguments.of(
        kotlinProjectDetails(projectDir).copy(
          testClass = kotlinJunit4Test(),
        ),
      ),
    )

    @JvmStatic
    fun projectDetailsWithArmWebfluxDependency() = listOf(
      Arguments.of(
        javaProjectDetails(projectDir).copy(buildScript = javaArmWebfluxDependency()),
      ),
      Arguments.of(
        kotlinProjectDetails(projectDir).copy(buildScript = kotlinArmWebfluxDependency()),
      ),
    )

    @JvmStatic
    fun projectDetailsWithArmNonWebfluxDependency() = listOf(
      Arguments.of(
        javaProjectDetails(projectDir).copy(buildScript = javaArmNonWebfluxDependency()),
      ),
      Arguments.of(
        kotlinProjectDetails(projectDir).copy(buildScript = kotlinArmNonWebfluxDependency()),
      ),
    )
  }

  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `Manifest file contains project name and version`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val result = buildProject(projectDir, "assemble")
    assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val file = findJar(projectDir, projectDetails.projectName)
    val jarFile = JarFile(file)
    assertThat(jarFile.manifest.mainAttributes.getValue("Implementation-Version")).isEqualTo(
      LocalDate.now().format(DateTimeFormatter.ISO_DATE),
    )
    assertThat(jarFile.manifest.mainAttributes.getValue("Implementation-Title")).isEqualTo(projectDetails.projectName)
  }

  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `Jar file task is disabled so only one jar gets created`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val result = buildProject(projectDir, "assemble")
    assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)

    val jarFiles = Files.walk(Paths.get("${projectDir.absolutePath}/build/libs")).use { paths ->
      paths.map { it.toString().substringAfter("${projectDir.absolutePath}/build/libs/") }
        .filter { it.contains(projectDetails.projectName) }
        .toList()
    }

    assertThat(jarFiles).containsExactly(
      "${projectDetails.projectName}-${LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)}.jar",
    )
  }

  @Test
  fun `Java - Junit 4 tests will not even compile`() {
    makeProject(
      javaProjectDetails(projectDir).copy(
        buildScript = javaExcludeJunit4Dependency(),
        testClass = javaJunit4Test(),
      ),
    )

    buildProjectAndFail(projectDir, "compileTestJava")
  }

  @Test
  fun `Kotlin - Junit 4 tests will not even compile`() {
    makeProject(
      kotlinProjectDetails(projectDir).copy(
        buildScript = kotlinExcludeJunit4Dependency(),
        testClass = kotlinJunit4Test(),
      ),
    )

    buildProjectAndFail(projectDir, "compileTestKotlin")
  }

  @ParameterizedTest
  @MethodSource("projectDetailsWithJunit4TestsAndDependency")
  fun `Junit 4 tests will compile and run if Junit 4 dependency added`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val result = buildProject(projectDir, "test")
    assertThat(result.output)
      .contains("Task :test")
      .contains("BUILD SUCCESSFUL")
      .contains("1 tests")
  }

  @ParameterizedTest
  @MethodSource("projectDetailsWithArmWebfluxDependency")
  fun `when apple silicon arm 64 should include osx-aarch_64 as well as default osx-x86_64 version`(projectDetails: ProjectDetails) {
    val jarFile = jarFileForProject(projectDetails, architecture = "aarch64")

    val version = getNettyResolverDNSNativeMacosVersion(jarFile)

    assertThat(jarContainsNettyResolverDNSNativeMacos(jarFile, version, "osx-aarch_64")).isTrue
    assertThat(jarContainsNettyResolverDNSNativeMacos(jarFile, version, "osx-x86_64")).isTrue
  }

  @ParameterizedTest
  @MethodSource("projectDetailsWithArmWebfluxDependency")
  internal fun `when intel should not include osx-aarch_64 just default osx-x86_64 version`(projectDetails: ProjectDetails) {
    val jarFile = jarFileForProject(projectDetails, architecture = "amd64")

    val version = getNettyResolverDNSNativeMacosVersion(jarFile)

    assertThat(jarContainsNettyResolverDNSNativeMacos(jarFile, version, "osx-aarch_64")).isFalse
    assertThat(jarContainsNettyResolverDNSNativeMacos(jarFile, version, "osx-x86_64")).isTrue
  }

  @ParameterizedTest
  @MethodSource("projectDetailsWithArmWebfluxDependency")
  internal fun `when any other architecture should not include osx-aarch_64 just default osx-x86_64 version`(projectDetails: ProjectDetails) {
    val jarFile = jarFileForProject(projectDetails, architecture = "s390")

    val version = getNettyResolverDNSNativeMacosVersion(jarFile)

    assertThat(jarContainsNettyResolverDNSNativeMacos(jarFile, version, "osx-aarch_64")).isFalse
    assertThat(jarContainsNettyResolverDNSNativeMacos(jarFile, version, "osx-x86_64")).isTrue
  }

  @ParameterizedTest
  @MethodSource("projectDetailsWithArmNonWebfluxDependency")
  internal fun `should not add netty mac arm64 version when webflux not a dependency`(projectDetails: ProjectDetails) {
    val jarFile = jarFileForProject(projectDetails)

    assertThat(getNettyResolverDNSNativeMacosJarEntry(jarFile, "osx-x86_64")).isNull()
    assertThat(getNettyResolverDNSNativeMacosJarEntry(jarFile, "osx-aarch_64")).isNull()
  }

  private fun jarFileForProject(projectDetails: ProjectDetails, architecture: String = "aarch64"): JarFile {
    makeProject(projectDetails)
    val result = buildProject(projectDir, "bootJar", "-Dos.arch=$architecture")
    assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
    return JarFile(findJar(projectDir, projectDetails.projectName))
  }
}

private fun javaExcludeJunit4Dependency(): String =
  """
      plugins {
          id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
      }
      
      configurations {
          testImplementation { 
              exclude(group = "org.junit.vintage") 
              exclude(group = "junit")
          }
      }
  """.trimIndent()

private fun kotlinExcludeJunit4Dependency(): String =
  """
      plugins {
          id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
      }
      
      configurations {
          testImplementation { exclude(mapOf("group" to "org.junit.vintage", "group" to "junit")) }
      }
  """.trimIndent()

private fun javaJunit4Dependency(): String =
  """
      plugins {
          id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
      }
      
      dependencies {
        testImplementation 'junit:junit:4.13'
      }
  """.trimIndent()

private fun kotlinJunit4Dependency(): String =
  """
      plugins {
          id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
      }
      
      dependencies {
        testImplementation("junit:junit:4.13")
      }
  """.trimIndent()

private fun javaJunit4Test(): String {
  return """
      package uk.gov.justice.digital.hmpps.app;
      
      import org.junit.Test;

      import static org.assertj.core.api.Assertions.assertThat;

      public class ApplicationTest {
          @Test
          public void aTest() {
              assertThat("anything").isEqualTo("anything");
          }
      }
  """.trimIndent()
}

private fun kotlinJunit4Test(): String {
  return """
      package uk.gov.justice.digital.hmpps.app
      
      import org.assertj.core.api.Assertions.assertThat
      import org.junit.Test
      
      class ApplicationTest {
          @Test
          fun `A Test`() {
              assertThat("anything").isEqualTo("anything")
          }
      }
  """.trimIndent()
}

private fun javaArmWebfluxDependency(): String =
  """
    plugins {
      id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
    }
    dependencies {
        implementation "org.springframework.boot:spring-boot-starter-webflux"
        implementation "org.springframework.boot:spring-boot-starter-oauth2-client"
        
    }
  """.trimIndent()

private fun kotlinArmWebfluxDependency(): String =
  """
    plugins {
      id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
    }
    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-webflux")
        implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
        
    }
  """.trimIndent()

private fun javaArmNonWebfluxDependency(): String =
  """
    plugins {
      id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
    }
    dependencies {
      implementation "org.springframework.boot:spring-boot-starter-web"
      implementation "org.springframework.boot:spring-boot-starter-oauth2-client"
        
    }
  """.trimIndent()

private fun kotlinArmNonWebfluxDependency(): String =
  """
    plugins {
      id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
    }
    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
        
    }
  """.trimIndent()

private fun jarContainsNettyResolverDNSNativeMacos(jar: JarFile, version: String, architecture: String): Boolean =
  jar.getJarEntry("BOOT-INF/lib/netty-resolver-dns-native-macos-$version.Final-$architecture.jar") != null

private fun getNettyResolverDNSNativeMacosJarEntry(jar: JarFile, architecture: String): JarEntry? =
  jar.entries().asSequence()
    .filter { it.name.startsWith("BOOT-INF/lib/netty-resolver-dns-native-macos") }
    .filter { it.name.contains(".Final-$architecture.jar") }
    .firstOrNull()

private fun getNettyResolverDNSNativeMacosVersion(jar: JarFile): String {
  val entry = getNettyResolverDNSNativeMacosJarEntry(jar, "osx-x86_64")
  val versionExtractor = "netty-resolver-dns-native-macos-([0-9.]+).Final-osx-x86_64.jar".toRegex()
  return entry?.let { versionExtractor.find(entry.name)?.groupValues?.get(1) }
    ?: throw IllegalStateException("Could not extract version from $entry")
}
