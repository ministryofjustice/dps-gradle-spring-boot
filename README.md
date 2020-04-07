# DPS Spring Boot Gradle Plugin

**THIS IS A WORK IN PROGRESS**

To test this plugin:

* Check out this repo and run command `./gradlew publishToMavenLocal`
* Add the following to your project's settings.gradle:

```
pluginManagement {
  repositories {
    mavenLocal()
    gradlePluginPortal()
  }
}
```
* Add the following to the plugins section of your build.gradle file: `id("uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot") version "0.0.1-SNAPSHOT"`
* Remove the following from your build.gradle file: `id("org.springframework.boot") version "2.2.6.RELEASE"`
* Run a gradle command (e.g. `./gradlew compileJava`)

You should find that the project compiles because the dps gradle plugin has imported the Spring Boot plugin on your behalf

