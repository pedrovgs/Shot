package com.karumi.shot.domain

import com.karumi.shot.domain.model.FilePath

object model {
  type ScreenshotsSuite = Seq[Screenshot]
  type FilePath = String
  type Folder = String
  type AppId = String
}

object Config {
  val androidDependencyMode = "androidTestCompile"
  val androidDependency = "com.facebook.testing.screenshot:core:0.4.2"
  val screenshotsFolderName = "/screenshots"
  val metadataFileName = screenshotsFolderName + "/screenshots-default/metadata.xml"
  val androidPluginName = "com.android.application"
  val instrumentationTestTask = "connectedAndroidTest"
  val packageTestApkTask = "packageDebugAndroidTest"
}

case class Screenshot(name: String,
                      testClass: String,
                      testName: String,
                      tileWidth: Int,
                      tileHeight: Int,
                      viewHierarchy: FilePath,
                      absoluteFileNames: Seq[FilePath],
                      relativeFileNames: Seq[FilePath])
