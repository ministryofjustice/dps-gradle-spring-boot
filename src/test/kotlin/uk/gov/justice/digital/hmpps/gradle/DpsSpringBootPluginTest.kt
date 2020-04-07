package uk.gov.justice.digital.hmpps.gradle

import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import org.gradle.testfixtures.ProjectBuilder
import org.springframework.boot.gradle.plugin.SpringBootPlugin

class DpsSpringBootPluginTest : WordSpec ({

    "Using the plugin" should {
        "Apply the SpringBoot plugin" {
            val project = ProjectBuilder.builder().build()
            project.pluginManager.apply("uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot")

            project.plugins.getPlugin(SpringBootPlugin::class.java) shouldNotBe null
        }
    }
})