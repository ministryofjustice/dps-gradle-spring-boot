import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.3.61"
  id("java-gradle-plugin")
  id("maven-publish")
}

repositories {
  mavenLocal()
  mavenCentral()
  jcenter()
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    jvmTarget = "11"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

group = "uk.gov.justice.digital.hmpps.gradle"
version = "0.0.1-SNAPSHOT"

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib")
  implementation("org.jetbrains.kotlin:kotlin-reflect")

  implementation("org.springframework.boot:spring-boot-gradle-plugin:2.2.6.RELEASE")

  testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.1")
}

gradlePlugin {
  plugins {
    create("DpsSpringBoot") {
      id = "uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot"
      implementationClass = "uk.gov.justice.digital.hmpps.gradle.DpsSpringBootPlugin"
    }
  }
}
