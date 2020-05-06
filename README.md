# DPS Spring Boot Gradle Plugin (WIP) 



## Overview

This Gradle plugin is used to orchestrate DPS Spring Boot projects such that:
* All projects use a common set of plugins at versions that have been tested together
* All projects use a common set of dependencies at versions that have been tested together
* Any common build configuration shared by projects is performed in this plugin and not the project itself (removing duplication and subsequent drift)
* CVEs causing `dependencyCheckAnalyze` failures are mitigated in a single place rather than in each and every project

## How to use this plugin
In your `build.gradle.kts` (or `build.gradle` for Java) add the following line to the plugin section:
```
plugins {
  ...
  id("uk.gov.justice.digital.hmpps.gradle.DpsSpringBoot") version "<plugin-version>"
  ...
}
``` 
Where the `plugin-version` is `TODO when we start publishing versions indicate how to find latest version here`

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

We have deliberately excluded the transitive JUnit 4 dependency from Spring Boot Test as JUnit 5 is preferred. Ideally teams should be converting JUnit 4 tests to JUnit 5 as part of their technical debt paydown.

### But I *really* need JUnit 4

If you must use JUnit 4 in your project then add the following to your Gradle build file's dependencies closure:

```
// Adjust the version number as appropriate
testImplementation("junit:junit:4.12")
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
./gradlew test --tests uk.gov.justice.digital.hmpps.gradle.functional.pluginmanagers.DependencyCheckPluginManagerTest
```
and check the report generated at `build/dependencyUpdates/projectsUsingPlugin/report.txt`

# THIS IS A WORK IN PROGRESS

To test this plugin:

* Check out this repo and run command `./gradlew publishToMavenLocal`
* Check out the `prison-estate` repo and switch to branch `mh-DT-727-dps-gradle-plugin`
* Compare with the master branch to see what has changed in `build.gradle.kts`
* Run the project and test - it should behave as now
* Check that the health and info endpoints still work
* Check that existing tasks still work

And for the adventurous:
* Make a change to the `dps-gradle-spring-boot` project and publish
* E.g. You could revert back to Spring Boot version `2.2.0.RELEASE` in `build.gradle.kts`
* Then publish with command `./gradlew publishToMavenLocal`
* Then back in the `prison-estate` run task `./gradlew dependencyCheckAnalyze`
* And you should see you now have loads of vulnerabilities
* That's almost exactly the same as happens each time we move to the next Spring Boot version - but now we have a single place to mitigate them

### TODO
* Get feedback from the wider team
* Work out somewhere to publish the plugin to (possibly Maven Central or Github Packages) - see [DT-787](https://dsdmoj.atlassian.net/browse/DT-787)
* Automate incremental version numbers (we could manage this manually but safer if it is impossible overwrite previous versions)
* Publish version 1
