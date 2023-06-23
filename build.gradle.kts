import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.configurationcache.extensions.serviceOf
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.8.22"
  id("com.gradle.plugin-publish") version "1.2.0"
  id("java-gradle-plugin")
  id("maven-publish")
  id("com.github.ben-manes.versions")
  id("se.patrikerdes.use-latest-versions") version "0.2.18"
  id("org.owasp.dependencycheck") version "8.3.1"
  id("com.adarshr.test-logger")
  id("org.jlleitschuh.gradle.ktlint") version "11.4.2"
}

repositories {
  mavenLocal()
  mavenCentral()
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
version = "4.9.0"

gradlePlugin {
  website.set("https://github.com/ministryofjustice/dps-gradle-spring-boot")
  vcsUrl.set("https://github.com/ministryofjustice/dps-gradle-spring-boot")
  plugins {
    create("dpsSpringBootPlugin") {
      id = "uk.gov.justice.hmpps.gradle-spring-boot"
      implementationClass = "uk.gov.justice.digital.hmpps.gradle.DpsSpringBootPlugin"

      displayName = "HMPPS Spring Boot Plugin"
      description = "Plugin for HMPPS Spring Boot microservice configuration"
      tags.set(listOf("hmpps", "spring-boot"))
    }
  }
}

// did not upgrade to 3.2.0 because experienced ListenerNotificationException - same issue as https://github.com/radarsh/gradle-test-logger-plugin/issues/241
// this version is also set in settings.gradle.kts as needed by the plugins block
val testLoggerVersion by extra("3.0.0")

// Versions plugin requires gradle 7 so pin to 0.42.0 until can upgrade
// https://github.com/ben-manes/gradle-versions-plugin/releases/tag/v0.43.0
// this version is also set in settings.gradle.kts as needed by the plugins block
val versionsVersion by extra("0.42.0")

// This is the spring 2 branch - so pin to a v2 version
val springBootVersion by extra("2.7.13")

dependencies {
  implementation(kotlin("reflect"))

  implementation("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")
  implementation("io.spring.dependency-management:io.spring.dependency-management.gradle.plugin:1.1.0")
  implementation("org.owasp:dependency-check-core:8.3.1")
  implementation("org.owasp:dependency-check-gradle:8.3.1")
  implementation("com.github.ben-manes:gradle-versions-plugin:$versionsVersion")
  implementation("com.gorylenko.gradle-git-properties:com.gorylenko.gradle-git-properties.gradle.plugin:2.4.1")
  implementation("com.adarshr.test-logger:com.adarshr.test-logger.gradle.plugin:$testLoggerVersion")
  implementation("se.patrikerdes.use-latest-versions:se.patrikerdes.use-latest-versions.gradle.plugin:0.2.18")
  implementation("org.jlleitschuh.gradle.ktlint:org.jlleitschuh.gradle.ktlint.gradle.plugin:11.4.2")

  testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
  testImplementation("org.mockito:mockito-junit-jupiter:5.4.0")
  testImplementation("org.assertj:assertj-core:3.24.2")
  testImplementation("net.javacrumbs.json-unit:json-unit-assertj:2.38.0")
  testImplementation("com.google.code.gson:gson:2.10.1")
  testImplementation("org.eclipse.jgit:org.eclipse.jgit:6.6.0.202305301015-r")
  // Had to include this when I had the same error as https://youtrack.jetbrains.com/issue/KT-49547, this links to https://github.com/gradle/gradle/issues/16774 which has includes a workaround
  testRuntimeOnly(
    files(
      serviceOf<org.gradle.api.internal.classpath.ModuleRegistry>().getModule("gradle-tooling-api-builders")
        .classpath.asFiles.first()
    )
  )
}

tasks {
  test {
    useJUnitPlatform()
  }

  withType<KotlinCompile> {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_17.toString()
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
