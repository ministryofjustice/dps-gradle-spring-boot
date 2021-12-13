# DPS Spring Boot Gradle Plugin

## Overview

This Gradle plugin is used to orchestrate DPS Spring Boot projects such that:
* All projects use a common set of plugins at versions that have been tested together
* All projects use a common set of dependencies at versions that have been tested together
* Any common build configuration shared by projects is performed in this plugin and not the project itself (removing duplication and subsequent drift)
* CVEs causing `dependencyCheckAnalyze` failures are mitigated in a single place rather than in each and every project

## Release Notes

##### [4.0.0-beta](release-notes/4.0.0.md)
##### [3.3.14](release-notes/3.3.14.md)
##### [3.3.13](release-notes/3.3.13.md)
##### [3.3.12](release-notes/3.3.12.md)
##### [3.3.11](release-notes/3.3.11.md)
##### [3.3.10](release-notes/3.3.10.md)
##### [3.3.9](release-notes/3.3.9.md)
##### [3.3.8](release-notes/3.3.8.md)
##### [3.3.7](release-notes/3.3.7.md)
##### [3.3.6](release-notes/3.3.6.md)
##### [3.3.5](release-notes/3.3.5.md)
##### [3.3.4](release-notes/3.3.4.md)
##### [3.3.3](release-notes/3.3.3.md)
##### [3.3.2](release-notes/3.3.2.md)
##### [3.3.1](release-notes/3.3.1.md)
##### [3.3.0](release-notes/3.3.0.md)
##### [3.2.0](release-notes/3.2.0.md)
##### [3.1.7](release-notes/3.1.7.md)
##### [3.1.6](release-notes/3.1.6.md)
##### [3.1.5](release-notes/3.1.5.md)
##### [3.1.4](release-notes/3.1.4.md)
##### [3.1.3](release-notes/3.1.3.md)
##### [3.1.2](release-notes/3.1.2.md)
##### [3.1.1](release-notes/3.1.1.md)
##### [3.1.0](release-notes/3.1.0.md)
##### [3.0.1](release-notes/3.0.1.md)
##### [3.0.0](release-notes/3.0.0.md)
##### [2.1.2](release-notes/2.1.2.md)
##### [2.1.1](release-notes/2.1.1.md)
##### [2.1.0](release-notes/2.1.0.md)
##### [2.0.2](release-notes/2.0.2.md)
##### [2.0.1](release-notes/2.0.1.md)
##### [2.0.0](release-notes/2.0.0.md)
##### [1.1.2](release-notes/1.1.2.md)
##### [1.1.1](release-notes/1.1.1.md)
##### [1.1.0](release-notes/1.1.0.md)
##### [1.0.7](release-notes/1.0.7.md)
##### [1.0.6](release-notes/1.0.6.md)
##### [1.0.5](release-notes/1.0.5.md)
##### [1.0.4](release-notes/1.0.4.md)
##### [1.0.3](release-notes/1.0.3.md)
##### [1.0.2](release-notes/1.0.2.md)
##### [1.0.1](release-notes/1.0.1.md)
##### [1.0.0](release-notes/1.0.0.md)

## How to use this plugin
In your `build.gradle.kts` (or `build.gradle` for Java) add the following line to the plugin section:
```
plugins {
  ...
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "3.3.10"
  ...
}
```
Where the `plugin-version` can be found by going to https://plugins.gradle.org/plugin/uk.gov.justice.hmpps.gradle-spring-boot

### Duplicated build logic

This plugin provides various build logic that may already exist in your project, and using this plugin may:
* Have no effect - maybe your project's build logic overrides the plugin
* Cause an error - maybe something in the plugin recognises the duplication and complains

Either way you will need to remove some of this duplication from your `build.gradle.kts` file

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

## Using JUnit 4

JUnit 4 is no longer included by Spring Boot.  If you have tests running on JUnit 4 make sure to explicitly declare the dependency (check for latest version):

```
configuration {
    testImplementation 'junit:junit:4.13.1'
}
```

Note that this plugin will automatically apply the junit vintage dependency to ensure compatibility with JUnit 5.

## Excluding JUnit 4

We no longer exclude JUnit 4 as part of this plugin as it wasn't compatible with Gradle 6.7.  If you want to exclude it from your project then add the following to your Gradle build file:

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

### Code style & formatting
```bash
./gradlew ktlintApplyToIdea addKtlintFormatGitPreCommitHook
```
will apply ktlint styles to intellij and also add a pre-commit hook to format all changed kotlin files.

Note that the .editorconfig in the root of this project is for this project only, the one in src/main/resources will be copied to other projects to enforce style.

To setup dependent projects with ktlint:
1. (Optional) run the above gradle command to apply to intellij and add the pre commit hook.
2. If you don't plan on making changes to .editorconfig (created on first run) then add to .gitignore.
3. Ensure that the continuous integration tool runs the `check` task instead of `test`.

## TODO

* Automate incremental version numbers (we could manage this manually but safer if it is impossible overwrite previous versions)
