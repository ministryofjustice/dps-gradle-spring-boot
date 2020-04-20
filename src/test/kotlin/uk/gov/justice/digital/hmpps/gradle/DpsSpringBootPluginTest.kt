package uk.gov.justice.digital.hmpps.gradle

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.gradle.plugin.SpringBootPlugin

class DpsSpringBootPluginTest {

    val project: Project = ProjectBuilder.builder().build()

    @BeforeEach
    fun `Create project`() {
        project.pluginManager.apply("uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot")
    }

    @Nested
    inner class Plugins {

        @Test
        fun `Should apply the Spring Boot plugin`() {
            assertThat(project.plugins.getPlugin(SpringBootPlugin::class.java)).isNotNull
        }

        @Test
        fun `Should apply the Kotlin plugin`() {
            assertThat(project.plugins.getPlugin(KotlinPluginWrapper::class.java)).isNotNull
        }
    }

    @Test
    fun `Should apply maven repositories`() {
        assertThat(project.repositories).extracting("name").containsExactlyInAnyOrder("MavenLocal", "MavenRepo")
    }

    @Test
    fun `Should set the group`() {
        assertThat(project.group).isEqualTo("uk.gov.justice.digital.hmpps")
    }

    @Nested
    inner class Dependencies {

        @Test
        fun `Should apply Kotlin standard libraries`() {
            assertThat(project.configurations.getByName("implementation").dependencies)
                .extracting("group", "name")
                .contains(
                    Tuple.tuple("org.jetbrains.kotlin", "kotlin-stdlib-jdk8"),
                    Tuple.tuple("org.jetbrains.kotlin", "kotlin-reflect")
                )
        }

        @Test
        fun `Should apply Spring Boot standard libraries`() {
            assertThat(project.configurations.getByName("implementation").dependencies)
                .extracting("group", "name", "version")
                .contains(
                    Tuple.tuple("org.springframework.boot", "spring-boot-starter-web", "2.2.6.RELEASE"),
                    Tuple.tuple("org.springframework.boot", "spring-boot-starter-actuator", "2.2.6.RELEASE")
                )
        }
    }

    @Test
    fun `Should set jvm target for Kotlin`() {
        assertThat((project.tasks.getByPath("compileKotlin") as KotlinCompile).kotlinOptions.jvmTarget).isEqualTo("11")
        assertThat((project.tasks.getByPath("compileTestKotlin") as KotlinCompile).kotlinOptions.jvmTarget).isEqualTo("11")
    }
}
