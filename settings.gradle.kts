rootProject.name = "dps-gradle-spring-boot"

pluginManagement {
  // did not upgrade to 3.2.0 because experienced ListenerNotificationException - same issue as https://github.com/radarsh/gradle-test-logger-plugin/issues/241
  // this version is also set in build.gradle.kts as needed by the plugins block
  val testLoggerVersion by extra("3.0.0")
    plugins {
      id("com.adarshr.test-logger") version testLoggerVersion
    }
}
