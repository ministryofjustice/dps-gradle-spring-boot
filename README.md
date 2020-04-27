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

## JUnit 4

We have deliberately excluded the transitive JUnit 4 depdendency from Spring Boot Test as JUnit 5 is preferred. Ideally teams should be converting JUnit 4 tests to JUnit 5 as part of their technical debt paydown.

### But I *really* need JUnit 4

If you must use JUnit 4 in your project then add the following to your Gradle build file's dependencies closure:

```
// Adjust the version number as appropriate
testImplementation("org.junit.vintage:junit-vintage-engine:5.6.2")
```


## OWASP Dependency Check and Suppression Files

The plugin `org.owasp.dependencycheck` is applied by this plugin.  It has a task `dependencyCheckAnalyze` which is used to check for vulnerabilities in dependencies.

Any vulnerabilities introduced by this plugin that have been mitigated can be suppressed in file `src/main/resources/dps-gradle-spring-boot-suppressions.xml`.  See the OWASP dependency check plugin's homepage for further details.

### Additional Suppression Files

If you are using this plugin and need additional suppressions for vulnerabilities introduced by your dependencies then you can specify additional suppression xmls in your Gradle build file:
```
dependencyCheck {
  suppressionFiles.add("<your-suppresion-file>.xml")
}
```

***WARNING***

It is possible to overwrite the `suppressionFiles` list like this:
```
// This removes the plugin's default suppression file
dependencyCheck {
  suppressionFiles = listOf("<your-suppresion-file>.xml")
}
```
 This will remove the suppression file supplied by this plugin which is probably not the intention.
 
## Dependency Update Checks

The plugin `com.github.ben-manes.versions` has task `dependencyUpdates` which checks the main Gradle build file for out of date versions.

However, we also need to know if dependencies applied by the DPS plugin are up to date.

### How to check if dependencies applied by the DPS plugin are up to date

There is a test in class `DependencyUpdatesFuncTest` which:
* builds a sample project
* applies the DPS plugin
* runs the `dependencyUpdates` task against the sample project
* copies the reports generated into directory `build/dependencyUpdates/projectsUsingPlugin`
  
You can run the test with command:
```
./gradlew test --tests uk.gov.justice.digital.hmpps.gradle.DependencyUpdatesFuncTest
```
and check the report generated at `build/dependencyUpdates/projectsUsingPlugin/report.txt`
 