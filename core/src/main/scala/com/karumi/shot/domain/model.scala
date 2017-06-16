package com.karumi.shot.domain

object model {
  type Folder = String
}

object Config {
  val androidDependencyMode = "androidTestCompile"
  val androidDependency = "com.facebook.testing.screenshot:core:0.4.2"
  val screenshotsFolderName = "/screenshots"
}
