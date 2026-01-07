plugins {
  // this is necessary to avoid the plugins to be loaded multiple times
  // in each subproject's classloader
  alias(libs.plugins.androidApplication) apply false
  alias(libs.plugins.androidLibrary) apply false
  alias(libs.plugins.composeMultiplatform) apply false
  alias(libs.plugins.composeCompiler) apply false
  alias(libs.plugins.kotlinMultiplatform) apply false
  alias(libs.plugins.spotless)
}

spotless {
  kotlin {
    target("**/*.kt")
    targetExclude("**/build/**/*.kt", "**/spotless/**/*.kt")
    ktlint("1.3.1")
      .editorConfigOverride(
        mapOf(
          "ktlint_standard_function-naming" to "disabled",
          "ktlint_standard_no-wildcard-imports" to "disabled",
        ),
      )
    licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
  }
  kotlinGradle {
    target("**/*.gradle.kts")
    targetExclude("**/build/**/*.gradle.kts")
    ktlint("1.3.1")
  }
}
