plugins {
  id("com.android.application")
  id("kotlin-android")
}

android {
  compileSdk = 30

  defaultConfig {
    applicationId = "com.bpawlowski.shotagpbug"
    minSdk = 26
    targetSdk = 30
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  kotlinOptions {
    jvmTarget = "1.8"
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
