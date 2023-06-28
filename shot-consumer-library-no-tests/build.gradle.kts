// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
  repositories {
    mavenLocal()
    google()
    mavenCentral()
  }
  dependencies {
    classpath(libs.android.gradle.plugin)
    classpath(libs.kotlin.gradle.plugin)
    classpath(libs.shot.gradle.plugin)

    // NOTE: Do not place your application dependencies here; they belong
    // in the individual module build.gradle.kts files
  }
}

tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}
