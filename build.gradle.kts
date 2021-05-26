import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.5.10"
  id("com.gradle.plugin-publish") version "0.15.0"
  id("java-gradle-plugin")
  id("maven-publish")
  id("com.github.ben-manes.versions") version "0.38.0"
  id("se.patrikerdes.use-latest-versions") version "0.2.16"
  id("org.owasp.dependencycheck") version "6.1.6"
  id("com.adarshr.test-logger") version "3.0.0"
  id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
}

repositories {
  mavenLocal()
  mavenCentral()
  jcenter()
  maven {
    url = uri("https://plugins.gradle.org/m2/")
  }
}

fun isNonStable(version: String): Boolean {
  val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
  val regex = "^[0-9,.v-]+(-r)?$".toRegex()
  val isStable = stableKeyword || regex.matches(version)
  return isStable.not()
}

group = "uk.gov.justice.hmpps.gradle"
version = "3.3.0"

gradlePlugin {
  plugins {
    create("dpsSpringBootPlugin") {
      id = "uk.gov.justice.hmpps.gradle-spring-boot"
      implementationClass = "uk.gov.justice.digital.hmpps.gradle.DpsSpringBootPlugin"

      displayName = "HMPPS Spring Boot Plugin"
      description = "Plugin for HMPPS Spring Boot microservice configuration"
    }
  }
}

pluginBundle {
  website = "https://github.com/ministryofjustice/dps-gradle-spring-boot"
  vcsUrl = "https://github.com/ministryofjustice/dps-gradle-spring-boot"
  tags = listOf("hmpps", "spring-boot")
}

dependencies {
  implementation(kotlin("reflect"))

  implementation("org.springframework.boot:spring-boot-gradle-plugin:2.5.0")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
  implementation("io.spring.dependency-management:io.spring.dependency-management.gradle.plugin:1.0.11.RELEASE")
  implementation("org.owasp:dependency-check-gradle:6.1.6")
  implementation("com.github.ben-manes:gradle-versions-plugin:0.38.0")
  implementation("com.gorylenko.gradle-git-properties:com.gorylenko.gradle-git-properties.gradle.plugin:2.3.1")
  implementation("com.adarshr.test-logger:com.adarshr.test-logger.gradle.plugin:3.0.0")
  implementation("se.patrikerdes.use-latest-versions:se.patrikerdes.use-latest-versions.gradle.plugin:0.2.16")
  implementation("org.jlleitschuh.gradle.ktlint:org.jlleitschuh.gradle.ktlint.gradle.plugin:10.0.0")

  testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
  testImplementation("org.mockito:mockito-junit-jupiter:3.10.0")
  testImplementation("org.assertj:assertj-core:3.19.0")
  testImplementation("net.javacrumbs.json-unit:json-unit-assertj:2.26.0")
  testImplementation("com.google.code.gson:gson:2.8.7")
  testImplementation("org.eclipse.jgit:org.eclipse.jgit:5.11.1.202105131744-r")
}

tasks {
  test {
    useJUnitPlatform()
  }

  withType<KotlinCompile> {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_11.toString()
    }
  }

  withType<DependencyUpdatesTask> {
    rejectVersionIf {
      isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
  }
}

tasks.named("check") {
  dependsOn(":ktlintCheck")
}
