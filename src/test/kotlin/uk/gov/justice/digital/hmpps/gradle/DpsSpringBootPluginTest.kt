package uk.gov.justice.digital.hmpps.gradle

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.springframework.boot.gradle.plugin.SpringBootPlugin

class DpsSpringBootPluginTest () {

    @Test
    fun `Using the DPS plugin should apply the spring boot plugin`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot")

        assertThat(project.plugins.getPlugin(SpringBootPlugin::class.java)).isNotNull
    }

    @Test
    fun `Using the DPS plugin should apply maven repositories`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot")

        assertThat(project.repositories).extracting("name").containsExactlyInAnyOrder("MavenLocal", "MavenRepo")
    }

    @Test
    fun `Using the DPS plugin should apply the group`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot")

        assertThat(project.group).isEqualTo("uk.gov.justice.digital.hmpps")
    }

}
