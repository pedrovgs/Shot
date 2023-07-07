plugins {
  id("com.android.application")
  id("kotlin-android")
}

android {
  compileSdk = libs.versions.targetsdk.get().toInt()

  defaultConfig {
    applicationId = "com.bpawlowski.shotagpbug"
    minSdk = libs.versions.minsdk.get().toInt()
    targetSdk = libs.versions.targetsdk.get().toInt()
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  kotlinOptions {
    jvmTarget = libs.versions.java.get()
  }

  testOptions {
    if (System.getenv("orchestrated") == "true") {
      execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }
  }
}

dependencies {

  implementation(libs.androidx.core)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  implementation(libs.androidx.constraint.layout)
  testImplementation(libs.junit)
  androidTestUtil(libs.androidx.test.orchestrator)
  androidTestImplementation(libs.bundles.androidx.test)
}
