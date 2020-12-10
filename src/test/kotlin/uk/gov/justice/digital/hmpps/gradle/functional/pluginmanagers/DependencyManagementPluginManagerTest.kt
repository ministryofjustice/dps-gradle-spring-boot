package uk.gov.justice.digital.hmpps.gradle.functional.pluginmanagers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import uk.gov.justice.digital.hmpps.gradle.functional.GradleBuildTest
import uk.gov.justice.digital.hmpps.gradle.functional.ProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.getDependencyVersion
import uk.gov.justice.digital.hmpps.gradle.functional.javaProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.kotlinProjectDetails
import uk.gov.justice.digital.hmpps.gradle.functional.makeProject

class DependencyManagementPluginManagerTest : GradleBuildTest() {

  companion object {
    @JvmStatic
    fun projectDetailsWithSpringData() = listOf(
      Arguments.of(javaProjectDetails(projectDir).copy(buildScript = javaBuildScriptWithSpringData())),
      Arguments.of(kotlinProjectDetails(projectDir).copy(buildScript = kotlinBuildScriptWithSpringData()))
    )
  }

  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `Spring dependency versions are defaulted from the dependency management plugin`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val webVersion = getDependencyVersion(projectDir, "org.springframework.boot:spring-boot-starter-web")

    assertThat(webVersion).isNotBlank

    val actuatorVersion = getDependencyVersion(projectDir, "org.springframework.boot:spring-boot-starter-actuator")
    val validatorVersion = getDependencyVersion(projectDir, "org.springframework.boot:spring-boot-starter-validation")

    assertThat(webVersion).isEqualTo(actuatorVersion).isEqualTo(validatorVersion)
  }

  // This test can be deleted once we stop forcing the version of tomcat
  @ParameterizedTest
  @MethodSource("defaultProjectDetails")
  fun `Forced version of tomcat is included in the jar`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val tomcatVersion = getDependencyVersion(projectDir, "org.apache.tomcat.embed:tomcat-embed-core")
    assertThat(tomcatVersion).isEqualTo("9.0.40")
  }

  // This test can be deleted once we stop forcing the version of hibernate
  @ParameterizedTest
  @MethodSource("projectDetailsWithSpringData")
  fun `Forced version of hibernate is included in the jar`(projectDetails: ProjectDetails) {
    makeProject(projectDetails)

    val hibernateVersion = getDependencyVersion(projectDir, "org.hibernate:hibernate-core")
    assertThat(hibernateVersion).isEqualTo("5.4.24.Final")
  }
}

private fun javaBuildScriptWithSpringData() =
  """
    plugins {
      id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1"
    }
    
    dependencies {
      implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    }
  """.trimIndent()

private fun kotlinBuildScriptWithSpringData() =
  """
    plugins {
      id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.1"
    }
    
    dependencies {
      implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    }
  """.trimIndent()
