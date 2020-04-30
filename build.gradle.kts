import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.3.72"
  id("java-gradle-plugin")
  id("maven-publish")
  id("com.github.ben-manes.versions") version "0.28.0"
  id("se.patrikerdes.use-latest-versions") version "0.2.13"
  id("org.owasp.dependencycheck") version "5.3.2.1"
  id("com.adarshr.test-logger") version "2.0.0"
}

repositories {
  mavenLocal()
  mavenCentral()
  jcenter()
  maven {
    url = uri("https://plugins.gradle.org/m2/")
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

group = "uk.gov.justice.digital.hmpps.gradle"
version = "0.0.1-SNAPSHOT"

dependencies {
  implementation(kotlin("stdlib-jdk8"))
  implementation(kotlin("reflect"))

  implementation("org.springframework.boot:spring-boot-gradle-plugin:2.2.6.RELEASE")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
  implementation("io.spring.dependency-management:io.spring.dependency-management.gradle.plugin:1.0.9.RELEASE@pom")
  implementation("org.owasp:dependency-check-gradle:5.3.2.1")
  implementation("com.github.ben-manes:gradle-versions-plugin:0.28.0")
  implementation("com.gorylenko.gradle-git-properties:com.gorylenko.gradle-git-properties.gradle.plugin:2.2.2")
  implementation("com.adarshr.test-logger:com.adarshr.test-logger.gradle.plugin:2.0.0")
  implementation("se.patrikerdes.use-latest-versions:se.patrikerdes.use-latest-versions.gradle.plugin:0.2.13")

  testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
  testImplementation("org.mockito:mockito-junit-jupiter:3.3.3")
  testImplementation("org.assertj:assertj-core:3.15.0")
  testImplementation("net.javacrumbs.json-unit:json-unit-assertj:2.17.0")
  testImplementation("com.google.code.gson:gson:2.8.6")
  testImplementation("org.eclipse.jgit:org.eclipse.jgit:5.7.0.202003110725-r")
}

gradlePlugin {
  plugins {
    create("DpsSpringBoot") {
      id = "uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot"
      implementationClass = "uk.gov.justice.digital.hmpps.gradle.DpsSpringBootPlugin"
    }
  }
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
}
