package uk.gov.justice.digital.hmpps.gradle.pluginmanagers

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.assertj.core.groups.Tuple
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.gradle.UnitTest
import uk.gov.justice.digital.hmpps.gradle.functional.GradleBuildTest
import uk.gov.justice.digital.hmpps.gradle.functional.buildProject
import uk.gov.justice.digital.hmpps.gradle.functional.findJar
import uk.gov.justice.digital.hmpps.gradle.functional.kotlinProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.makeProject
import java.util.jar.JarEntry
import java.util.jar.JarFile

@Disabled("Disabled until tests are re-written to use gradle 7!")
class SpringBootPluginManagerTest : UnitTest() {
  @Test
  fun `Should apply Spring Boot standard libraries`() {
    assertThat(project.configurations.getByName("implementation").dependencies)
      .extracting<Tuple> { tuple(it.group, it.name) }
      .contains(
        tuple("org.springframework.boot", "spring-boot-starter-web"),
        tuple("org.springframework.boot", "spring-boot-starter-actuator"),
        tuple("org.springframework.boot", "spring-boot-starter-validation")
      )
  }

  @Test
  fun `Should apply Spring Boot test Dependencies`() {
    assertThat(project.configurations.getByName("testImplementation").dependencies)
      .extracting<Tuple> { tuple(it.group, it.name) }
      .contains(
        Tuple.tuple("org.springframework.boot", "spring-boot-starter-test")

      )
  }

  class SpringBootPluginManagerGradleTest : GradleBuildTest() {
    @Nested
    @DisplayName("When WebFlux is included in project")
    inner class WithWebFlux {
      private val project = """
          plugins {
            id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
          }
          dependencies {
              implementation("org.springframework.boot:spring-boot-starter-webflux");
              implementation("org.springframework.boot:spring-boot-starter-oauth2-client");
              
          }
      """.trimIndent()

      @Test
      internal fun `when apple silicon arm 64 should include osx-aarch_64 as well as default osx-x86_64 version`() {
        val jarFile = jarFileForProject(project, architecture = "aarch64")

        val version = getNettyResolverDNSNativeMacosVersion(jarFile)

        assertThat(jarContainsNettyResolverDNSNativeMacos(jarFile, version, "osx-aarch_64")).isTrue
        assertThat(jarContainsNettyResolverDNSNativeMacos(jarFile, version, "osx-x86_64")).isTrue
      }

      @Test
      internal fun `when intel should not include osx-aarch_64 just default osx-x86_64 version`() {
        val jarFile = jarFileForProject(project, architecture = "amd64")

        val version = getNettyResolverDNSNativeMacosVersion(jarFile)

        assertThat(jarContainsNettyResolverDNSNativeMacos(jarFile, version, "osx-aarch_64")).isFalse
        assertThat(jarContainsNettyResolverDNSNativeMacos(jarFile, version, "osx-x86_64")).isTrue
      }

      @Test
      internal fun `when any other architecture should not include osx-aarch_64 just default osx-x86_64 version`() {
        val jarFile = jarFileForProject(project, architecture = "s390")

        val version = getNettyResolverDNSNativeMacosVersion(jarFile)

        assertThat(jarContainsNettyResolverDNSNativeMacos(jarFile, version, "osx-aarch_64")).isFalse
        assertThat(jarContainsNettyResolverDNSNativeMacos(jarFile, version, "osx-x86_64")).isTrue
      }
    }

    @Nested
    @DisplayName("When WebFlux is not included in project")
    inner class WithNoWebFlux {

      @Test
      internal fun `should not add netty mac arm64 version when webflux not a dependency`() {
        val project = """
          plugins {
            id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1.0"
          }
          dependencies {
              implementation("org.springframework.boot:spring-boot-starter-web");
              implementation("org.springframework.boot:spring-boot-starter-oauth2-client");
              
          }
        """.trimIndent()

        val jarFile = jarFileForProject(project)

        assertThat(getNettyResolverDNSNativeMacosJarEntry(jarFile, "osx-x86_64")).isNull()
        assertThat(getNettyResolverDNSNativeMacosJarEntry(jarFile, "osx-aarch_64")).isNull()
      }
    }

    private fun jarFileForProject(gradleContents: String, architecture: String = "aarch64"): JarFile {
      val projectDetails = kotlinProjectDetails(projectDir).copy(buildScript = gradleContents)
      makeProject(projectDetails)
      val result = buildProject(projectDir, "bootJar", "-Dos.arch=$architecture")
      assertThat(result.task(":bootJar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
      return JarFile(findJar(projectDir, projectDetails.projectName))
    }
  }
}

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
