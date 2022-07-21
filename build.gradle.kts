import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.configurationcache.extensions.serviceOf
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.7.0"
  id("com.gradle.plugin-publish") version "0.21.0"
  id("java-gradle-plugin")
  id("maven-publish")
  id("com.github.ben-manes.versions") version "0.42.0"
  id("se.patrikerdes.use-latest-versions") version "0.2.18"
  id("org.owasp.dependencycheck") version "7.1.1"
  id("com.adarshr.test-logger") version "3.0.0" // did not upgrade to 3.2.0 because experienced ListenerNotificationException - same issue as https://github.com/radarsh/gradle-test-logger-plugin/issues/241
  id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
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
version = "4.3.4-beta"

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

  implementation("org.springframework.boot:spring-boot-gradle-plugin:2.7.2")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
  implementation("io.spring.dependency-management:io.spring.dependency-management.gradle.plugin:1.0.12.RELEASE")
  implementation("org.owasp:dependency-check-core:7.1.1")
  implementation("org.owasp:dependency-check-gradle:7.1.1")
  implementation("com.github.ben-manes:gradle-versions-plugin:0.42.0")
  implementation("com.gorylenko.gradle-git-properties:com.gorylenko.gradle-git-properties.gradle.plugin:2.4.1")
  implementation("com.adarshr.test-logger:com.adarshr.test-logger.gradle.plugin:3.0.0") // did not upgrade to 3.2.0 because experienced ListenerNotificationException - same issue as https://github.com/radarsh/gradle-test-logger-plugin/issues/241
  implementation("se.patrikerdes.use-latest-versions:se.patrikerdes.use-latest-versions.gradle.plugin:0.2.18")
  implementation("org.jlleitschuh.gradle.ktlint:org.jlleitschuh.gradle.ktlint.gradle.plugin:10.3.0")

  testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
  testImplementation("org.mockito:mockito-junit-jupiter:4.6.1")
  testImplementation("org.assertj:assertj-core:3.23.1")
  testImplementation("net.javacrumbs.json-unit:json-unit-assertj:2.35.0")
  testImplementation("com.google.code.gson:gson:2.9.0")
  testImplementation("org.eclipse.jgit:org.eclipse.jgit:6.2.0.202206071550-r")
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
