package uk.gov.justice.digital.hmpps.gradle

import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import org.gradle.testfixtures.ProjectBuilder
import org.springframework.boot.gradle.plugin.SpringBootPlugin

class SpringBootPluginTest : WordSpec ({

    "Using the plugin" should {
        "Apply the SpringBoot plugin" {
            val project = ProjectBuilder.builder().build()

            project.plugins.getPlugin(SpringBootPlugin::class.java) shouldNotBe null
        }
    }
})