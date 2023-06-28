plugins {
  id("com.android.library")
  id("shot")
}

android {
  compileSdk = libs.versions.targetsdk.get().toInt()

  defaultConfig {
    minSdk = libs.versions.minsdk.get().toInt()
    targetSdk = libs.versions.targetsdk.get().toInt()

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
    testApplicationId = "com.bpawlowski.library.test"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  packagingOptions {
    exclude("META-INF/AL2.0")
    exclude("META-INF/LGPL2.1")
  }

  testOptions {
    if (System.getenv("orchestrated") == "true") {
      execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }
  }
}

shot {
  applicationId = "com.bpawlowski.library"
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
