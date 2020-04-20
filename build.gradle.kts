import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.3.72"
  id("java-gradle-plugin")
  id("maven-publish")
  id("com.github.ben-manes.versions") version "0.28.0"
  id("se.patrikerdes.use-latest-versions") version "0.2.13"
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
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")

  testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
  testImplementation("org.mockito:mockito-junit-jupiter:3.3.3")
  testImplementation("org.assertj:assertj-core:3.15.0")
}

gradlePlugin {
  plugins {
    create("DpsSpringBoot") {
      id = "uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot"
      implementationClass = "uk.gov.justice.digital.hmpps.gradle.DpsSpringBootPlugin"
    }
  }
}
