package com.karumi.shot.domain

object model {
  type Folder = String
  type AppId = String
}

object Config {
  val androidDependencyMode = "androidTestCompile"
  val androidDependency = "com.facebook.testing.screenshot:core:0.4.2"
  val screenshotsFolderName = "/screenshots"
  val androidPluginName = "com.android.application"
  val instrumentationTestTask = "connectedAndroidTest"
  val packageTestApkTask = "packageDebugAndroidTest"
}
