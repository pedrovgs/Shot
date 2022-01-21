plugins {
  id("com.android.library")
  id("shot")
}

android {
  compileSdk = 30

  defaultConfig {
    minSdk = 26
    targetSdk = 30

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

  implementation("androidx.appcompat:appcompat:1.3.0")
  implementation("com.google.android.material:material:1.3.0")
  testImplementation("junit:junit:4.+")
  androidTestUtil("androidx.test:orchestrator:1.4.1")
  androidTestImplementation("androidx.test.ext:junit:1.1.2")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}
