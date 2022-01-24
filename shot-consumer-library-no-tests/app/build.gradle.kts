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

  implementation("androidx.core:core-ktx:1.5.0")
  implementation("androidx.appcompat:appcompat:1.3.0")
  implementation("com.google.android.material:material:1.3.0")
  implementation("androidx.constraintlayout:constraintlayout:2.0.4")
  testImplementation("junit:junit:4.+")
  androidTestUtil("androidx.test:orchestrator:1.4.1")
  androidTestImplementation("androidx.test.ext:junit:1.1.2")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}
