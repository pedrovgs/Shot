// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
  repositories {
    mavenLocal()
    google()
    mavenCentral()
  }
  dependencies {
    classpath("com.android.tools.build:gradle:7.1.0-alpha06")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
    classpath("com.karumi:shot:5.13.0")

    // NOTE: Do not place your application dependencies here; they belong
    // in the individual module build.gradle.kts files
  }
}

tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}
