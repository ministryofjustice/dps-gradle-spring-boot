# DPS Spring Boot Gradle Plugin



## Overview

This Gradle plugin is used to orchestrate DPS Spring Boot projects such that:
* All projects use a common set of plugins at versions that have been tested together
* All projects use a common set of dependencies at versions that have been tested together
* Any common build configuration shared by projects is performed in this plugin and not the project itself (removing duplication and subsequent drift)
* CVEs causing `dependencyCheckAnalyze` failures are mitigated in a single place rather than in each and every project

## Release Notes

##### [0.4.7](release-notes/0.4.7.md)
##### [0.4.6](release-notes/0.4.6.md)
##### [0.4.5](release-notes/0.4.5.md)
##### [0.4.4](release-notes/0.4.4.md)
##### [0.4.3](release-notes/0.4.3.md)
##### [0.4.2](release-notes/0.4.2.md)
##### [0.4.1](release-notes/0.4.1.md)
##### [0.4.0](release-notes/0.4.0.md)

## How to use this plugin
In your `build.gradle.kts` (or `build.gradle` for Java) add the following line to the plugin section:
```
plugins {
  ...
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "0.3.0"
  ...
}
```
Where the `plugin-version` can be found by going to https://plugins.gradle.org/plugin/uk.gov.justice.hmpps.gradle-spring-boot

### Duplicated build logic

This plugin provides various build logic that may already exist in your project, and using this plugin may:
* Have no effect - maybe your project's build logic overrides the plugin
* Cause an error - maybe something in the plugin recognises the duplication and complains

Either way you will need to remove some of this duplications from your `build.gradle.kts` file `TODO when we have published our first version, point to a PR that implements the plugin as an example of what to remove`

## How do I find out what this plugin is doing?

Read the code!

In the main you will probably need to know the following so you don't duplicate in your own build script:

#### The plugins and versions automatically applied by this plugin
The class DpsSpringBootPlugin contains all 3rd party plugins that are applied by the DPS plugin - check there first.  Then look in the file `build.gradle.kts` in the `dependencies/implementation` section.  There you can find each plugin and its version.

E.g. `org.springframework.boot:spring-boot-gradle-plugin` is the Spring Boot plugin and its version is part of the configuration

#### Any configuration relating to those plugins
Look in the package `uk.gov.justice.digital.hmpps.gradle.pluginmanagers` and you should find the class that orchestrates each plugin.

E.g. Class `SpringBootPluginManager` has a method called `setSpringBootInfo` which controls the contents of the `/info` endpoint

#### Any dependencies applied by this plugin automatically
Search the package `uk.gov.justice.digital.hmpps.gradle` for `project.dependencies.add`.  This should list all dependencies this plugin automatically applies.

E.g. Class `SpringBootPluginManager` applies `spring-boot-starter-actuator` (amongst others) so you do not need to declare that dependency in your project's `build.gradle.kts` file.

## JUnit 4

We no longer exclude JUnit 4 as part of this plugin as it wasn't compatible with Gradle 6.4.  If you want to exclude it from your project then add the following to your Gradle build file:

```
configurations {
  testImplementation { exclude(mapOf("group" to "org.junit.vintage", "group" to "junit")) }
}
```

This will ensure that code that includes junit `@Test` annotation doesn't compile and also that the junit vintage engine isn't included.

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

There is a test in class `VersionsPluginManagerTest` which:
* builds a sample project
* applies the DPS plugin
* runs the `dependencyUpdates` task against the sample project
* copies the reports generated into directory `build/dependencyUpdates/projectsUsingPlugin`

You can run the test with command:
```
./gradlew clean test --tests uk.gov.justice.digital.hmpps.gradle.functional.pluginmanagers.VersionsPluginManagerTest
```
and check the report generated at `build/dependencyUpdates/projectsUsingPlugin/report.txt`

## Testing the plugin locally on other projects

* Firstly bump the version of the plugin.
* Then publish the plugin to local maven
```
./gradlew publishToMavenLocal
```
* Then change the settings of the dependent project to read plugins from local storage.  In settings.gradle.kts
```
pluginManagement {
  repositories {
    mavenLocal()
    gradlePluginPortal()
  }
}
```

## Releasing the plugin

Semantic versioning is used for the plugin i.e. `MAJOR.MINOR.PATCH`. A `MAJOR` change is a breaking change and will
require changes to any existing projects that wish to adopt the `MAJOR` change.

At present the version number increment is a manual step in `build.gradle.kts`.  Increment that at the same time
as making changes.

When the plugin is ready to be released and PR approved, merge into main.  Then go into circleci at
https://app.circleci.com/pipelines/github/ministryofjustice/dps-gradle-spring-boot and approve the publish step.  This
will publish the plugin to the gradle plugins repository.

## TODO

* Get feedback from the wider team
* Automate incremental version numbers (we could manage this manually but safer if it is impossible overwrite previous versions)
* Publish version 1
