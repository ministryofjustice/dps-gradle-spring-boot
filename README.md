# DPS Spring Boot Gradle Plugin

## Overview

This Gradle plugin is used to orchestrate DPS Spring Boot projects such that:
* All projects use a common set of plugins at versions that have been tested together
* All projects use a common set of dependencies at versions that have been tested together
* Any common build configuration shared by projects is performed in this plugin and not the project itself (removing duplication and subsequent drift)
* CVEs causing `dependencyCheckAnalyze` failures are mitigated in a single place rather than in each and every project

This plugin targets Spring Boot 3.  See the `spring-boot-2` branch for the Spring Boot 2 version.

## Release Notes
##### [5.8.0](release-notes/5.x/5.8.0.md)
##### [5.7.0](release-notes/5.x/5.7.0.md)
##### [5.6.0](release-notes/5.x/5.6.0.md)
##### [5.5.1](release-notes/5.x/5.5.1.md)
##### [5.5.0](release-notes/5.x/5.5.0.md)
##### [5.4.1](release-notes/5.x/5.4.1.md)
##### [5.4.0](release-notes/5.x/5.4.0.md)
##### [5.3.0](release-notes/5.x/5.3.0.md)
##### [5.2.4](release-notes/5.x/5.2.4.md)
##### [5.2.3](release-notes/5.x/5.2.3.md)
##### [5.2.2](release-notes/5.x/5.2.2.md)
##### [5.2.1](release-notes/5.x/5.2.1.md)
##### [5.2.0](release-notes/5.x/5.2.0.md)
##### [5.1.4](release-notes/5.x/5.1.4.md)
##### [5.1.3](release-notes/5.x/5.1.3.md)
##### [5.1.2](release-notes/5.x/5.1.2.md)
##### [5.1.1](release-notes/5.x/5.1.1.md)
##### [5.1.0](release-notes/5.x/5.1.0.md)
##### [5.0.1](release-notes/5.x/5.0.1.md)
##### [5.0.0](release-notes/5.x/5.0.0.md)

<details>
  <summary>4.#</summary>

##### [4.13.0](release-notes/4.x/4.13.0.md)
##### [4.12.1](release-notes/4.x/4.12.1.md)
##### [4.12.0](release-notes/4.x/4.12.0.md)
##### [4.11.1](release-notes/4.x/4.11.1.md)
##### [4.11.0](release-notes/4.x/4.11.0.md)
##### [4.10.0](release-notes/4.x/4.10.0.md)
##### [4.9.3](release-notes/4.x/4.9.3.md)
##### [4.9.2](release-notes/4.x/4.9.2.md)
##### [4.9.1](release-notes/4.x/4.9.1.md)
##### [4.9.0](release-notes/4.x/4.9.0.md)
##### [4.8.8](release-notes/4.x/4.8.8.md)
##### [4.8.7](release-notes/4.x/4.8.7.md)
##### [4.8.6](release-notes/4.x/4.8.6.md)
##### [4.8.5](release-notes/4.x/4.8.5.md)
##### [4.8.4](release-notes/4.x/4.8.4.md)
##### [4.8.3](release-notes/4.x/4.8.3.md)
##### [4.8.2](release-notes/4.x/4.8.2.md)
##### [4.8.1](release-notes/4.x/4.8.1.md)
##### [4.8.0](release-notes/4.x/4.8.0.md)
##### [4.7.4](release-notes/4.x/4.7.4.md)
##### [4.7.3](release-notes/4.x/4.7.3.md)
##### [4.7.2](release-notes/4.x/4.7.2.md)
##### [4.7.1](release-notes/4.x/4.7.1.md)
##### [4.7.0](release-notes/4.x/4.7.0.md)
##### [4.6.0](release-notes/4.x/4.6.0.md)
##### [4.5.7](release-notes/4.x/4.5.7.md)
##### [4.5.6](release-notes/4.x/4.5.6.md)
##### [4.5.5](release-notes/4.x/4.5.5.md)
##### [4.5.4](release-notes/4.x/4.5.4.md)
##### [4.5.3](release-notes/4.x/4.5.3.md)
##### [4.5.2](release-notes/4.x/4.5.2.md)
##### [4.5.1](release-notes/4.x/4.5.1.md)
##### [4.5.0](release-notes/4.x/4.5.0.md)
##### [4.4.3](release-notes/4.x/4.4.3.md)
##### [4.4.2](release-notes/4.x/4.4.2.md)
##### [4.4.1](release-notes/4.x/4.4.1.md)
##### [4.3.4](release-notes/4.x/4.3.4.md)
##### [4.3.3](release-notes/4.x/4.3.3.md)
##### [4.3.2](release-notes/4.x/4.3.2.md)
##### [4.3.1](release-notes/4.x/4.3.1.md)
##### [4.3.0](release-notes/4.x/4.3.0.md)
##### [4.2.3](release-notes/4.x/4.2.3.md)
##### [4.2.2](release-notes/4.x/4.2.2.md)
##### [4.2.1](release-notes/4.x/4.2.1.md)
##### [4.2.0](release-notes/4.x/4.2.0.md)
##### [4.1.7](release-notes/4.x/4.1.7.md)
##### [4.1.6](release-notes/4.x/4.1.6.md)
##### [4.1.5](release-notes/4.x/4.1.5.md)
##### [4.1.4](release-notes/4.x/4.1.4.md)
##### [4.1.3](release-notes/4.x/4.1.3.md)
##### [4.1.2](release-notes/4.x/4.1.2.md)
##### [4.1.1](release-notes/4.x/4.1.1.md)
##### [4.1.0](release-notes/4.x/4.1.0.md)
##### [4.0.4](release-notes/4.x/4.0.4.md)
##### [4.0.3](release-notes/4.x/4.0.3.md)
##### [4.0.2](release-notes/4.x/4.0.2.md)
##### [4.0.1](release-notes/4.x/4.0.1.md)
##### [4.0.0](release-notes/4.x/4.0.0.md)

</details>

<details>
  <summary>3.#</summary>
  
##### [3.3.16](release-notes/3.x/3.3.16.md)
##### [3.3.15](release-notes/3.x/3.3.15.md)
##### [3.3.14](release-notes/3.x/3.3.14.md)
##### [3.3.13](release-notes/3.x/3.3.13.md)
##### [3.3.12](release-notes/3.x/3.3.12.md)
##### [3.3.11](release-notes/3.x/3.3.11.md)
##### [3.3.10](release-notes/3.x/3.3.10.md)
##### [3.3.9](release-notes/3.x/3.3.9.md)
##### [3.3.8](release-notes/3.x/3.3.8.md)
##### [3.3.7](release-notes/3.x/3.3.7.md)
##### [3.3.6](release-notes/3.x/3.3.6.md)
##### [3.3.5](release-notes/3.x/3.3.5.md)
##### [3.3.4](release-notes/3.x/3.3.4.md)
##### [3.3.3](release-notes/3.x/3.3.3.md)
##### [3.3.2](release-notes/3.x/3.3.2.md)
##### [3.3.1](release-notes/3.x/3.3.1.md)
##### [3.3.0](release-notes/3.x/3.3.0.md)
##### [3.2.0](release-notes/3.x/3.2.0.md)
##### [3.1.7](release-notes/3.x/3.1.7.md)
##### [3.1.6](release-notes/3.x/3.1.6.md)
##### [3.1.5](release-notes/3.x/3.1.5.md)
##### [3.1.4](release-notes/3.x/3.1.4.md)
##### [3.1.3](release-notes/3.x/3.1.3.md)
##### [3.1.2](release-notes/3.x/3.1.2.md)
##### [3.1.1](release-notes/3.x/3.1.1.md)
##### [3.1.0](release-notes/3.x/3.1.0.md)
##### [3.0.1](release-notes/3.x/3.0.1.md)
##### [3.0.0](release-notes/3.x/3.0.0.md)

</details>

<details>
  <summary>2.#</summary>
  
##### [2.1.2](release-notes/2.x/2.1.2.md)
##### [2.1.1](release-notes/2.x/2.1.1.md)
##### [2.1.0](release-notes/2.x/2.1.0.md)
##### [2.0.2](release-notes/2.x/2.0.2.md)
##### [2.0.1](release-notes/2.x/2.0.1.md)
##### [2.0.0](release-notes/2.x/2.0.0.md)

</details>

<details>
  <summary>1.#</summary>
  
##### [1.1.2](release-notes/1.x/1.1.2.md)
##### [1.1.1](release-notes/1.x/1.1.1.md)
##### [1.1.0](release-notes/1.x/1.1.0.md)
##### [1.0.7](release-notes/1.x/1.0.7.md)
##### [1.0.6](release-notes/1.x/1.0.6.md)
##### [1.0.5](release-notes/1.x/1.0.5.md)
##### [1.0.4](release-notes/1.x/1.0.4.md)
##### [1.0.3](release-notes/1.x/1.0.3.md)
##### [1.0.2](release-notes/1.x/1.0.2.md)
##### [1.0.1](release-notes/1.x/1.0.1.md)
##### [1.0.0](release-notes/1.x/1.0.0.md)

</details>

## How to use this plugin
In your `build.gradle.kts` (or `build.gradle` for Java) add the following line to the plugin section:
```
plugins {
  ...
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "5.8.0"
  ...
}
```
Where the `plugin-version` can be [found here](https://plugins.gradle.org/plugin/uk.gov.justice.hmpps.gradle-spring-boot)

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

## Runbook

This runbook details how to upgrade the dependencies of this plugin and publish it for others to use.

A new version may be needed for a few different reasons:
- Dependencies have been flagged in #dps_alerts_security as having security vulnerabilities
  - See [Slack message](https://mojdt.slack.com/archives/C69NWE339/p1639384200271500) for guidance on vulnerability patching
  - These could also be false positives and can be suppressed. Typically these are raised as issues against the [DependencyCheck](https://github.com/jeremylong/DependencyCheck/issues) project
- There is a new major releases of Java, Kotlin or Spring and we want to provide the option for teams to bump to these versions

### Upgrading dependencies

1. Create a Jira ticket
2. git clone this repo
3. Create a git branch including the Jira ticket in the name
4. Open the project in your favourite code editor
5. Bump the library version to the next version and append `-beta`
   1. See [Releasing the plugin](#releasing-the-plugin) for help on which version to bump
6. If the dependencies have security vulnerabilities then it can be useful to check that locally so you can see the state of them before and after your changes 
   1. Run the following Gradle task: `./gradlew dependencyCheckAnalyze`
   2. If you are using IntelliJ it can be found by going to _Gradle -> dps-gradle-spring-boot -> Tasks -> owasp dependency-check -> dependencyCheckAnalyze_ 
   3. Additional testing can be done with [Dependency Update Checks](#dependency-update-checks)
7. Upgrade all the dependencies in `build.gradle.kts` by running the following Gradle task: `./gradlew useLatestVersions`
   1. If you are using IntelliJ it can be found by going to _Gradle -> dps-gradle-spring-boot -> Tasks -> help -> useLatestVersions_
8. Review the pinned versions in `KotlinPluginManager`, `AppInsightsConfigManager`, and any others
   1. You may need to manually bump, or remove them if they are no longer required

### Testing locally

1. Follow [Testing the plugin locally on other projects](#testing-the-plugin-locally-on-other-projects)
2. In the other project you should run all tests including integration and start it locally
   1. At this stage you may run into issues such as transitive dependency resolution, unexpected test errors, or a new version of a dependency may not play nice with the project
   2. General advice is to do a bit of Googling to see if other people had the same issue. If it isn't critical to bump that 
   dependency then you can revert that dependency to the previous version and add a note in the build.gradle.kts
   as to why you didn't upgrade it so the next person knows. Hopefully the issue will be fixed and we'll be able to bump it next time

### Releasing the beta version

Depending on the scope of the new version this step may not be required. For example if you are suppressing false positives only then you
can release a new version without creating a beta version first.

1. In the [release-notes](/release-notes) directory create a new Markdown file for your version 
   1. Look at previous examples - we list the version upgrades which you can retrieve from the report generated by `useLatestVersions`
2. Add a link to the file you just created to [Release Notes](#release-notes)
3. You're now ready to commit these changes and raise a PR 🎉
4. Post the PR link on #dps_dev for folks to review
5. After merging to main it will automatically be published to the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/uk.gov.justice.hmpps.gradle-spring-boot)

### Smoketesting

We initially release a beta version to indicate to other teams that this version hasn't yet been tested in the wild, i.e. it hasn't
been included in a service deployed an environment. We do this to ensure that the new dependencies haven't caused any unexpected issues.

1. Pick a non-critical project which is not released to prod very frequently
   1. The DPS Tech Team used [court-register](https://github.com/ministryofjustice/court-register) and [prison-to-nhs-update](https://github.com/ministryofjustice/prison-to-nhs-update)
2. Update the `uk.gov.justice.hmpps.gradle-spring-boot` version to the beta one which was just published
3. Raise a PR and once approved deploy it to dev
4. Validate that the service has started up and is working as expected
5. Leave it running for a day and monitor that no unexpected alerts have been raised

### Releasing the new version

After smoketesting did not surface any issues the plugin is now ready for other teams to use.

1. Create a new git branch
2. Drop the `-beta` suffix from the version number in `build.gradle.kts`
3. Commit and raise a PR
4. Post the PR link on #dps_dev for folks to review
5. After the new version has been published you can upgrade your remaining projects to the new non-beta version 
   1. A script to do this across a number of projects can be found in [HMPPS Tech scripts](https://github.com/ministryofjustice/hmpps-tech-docs/tree/main/scripts/dps-spring-boot-gradle-plugin)
   2. Please also upgrade [hmpps-template-kotlin](https://github.com/ministryofjustice/hmpps-template-kotlin)
