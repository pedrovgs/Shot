package com.karumi.shot.domain

import com.karumi.shot.domain.model.{
  FilePath,
  ScreenshotComparisionErrors,
  ScreenshotsSuite
}

object model {
  type ScreenshotsSuite = Seq[Screenshot]
  type FilePath = String
  type Folder = String
  type AppId = String
  type ScreenshotComparisionErrors = Seq[ScreenshotComparisionError]
}

object Config {
  val androidDependencyMode: FilePath = "androidTestCompile"
  val androidDependency: FilePath =
    "com.facebook.testing.screenshot:core:0.4.2"
  val screenshotsFolderName: FilePath = "/screenshots/"
  val temporalScreenshotsFolder
    : FilePath = screenshotsFolderName + "screenshots-default/"
  val metadataFileName: FilePath = temporalScreenshotsFolder + "metadata.xml"
  val androidPluginName: FilePath = "com.android.application"
  val instrumentationTestTask: FilePath = "connectedAndroidTest"
  val packageTestApkTask: FilePath = "packageDebugAndroidTest"
}

case class Screenshot(name: String,
                      recordedScreenshotPath: String,
                      testClass: String,
                      testName: String,
                      tileWidth: Int,
                      tileHeight: Int,
                      viewHierarchy: FilePath,
                      absoluteFileNames: Seq[FilePath],
                      relativeFileNames: Seq[FilePath],
                      recordedPartsPaths: Seq[FilePath])

case class Dimension(width: Int, height: Int)

sealed trait ScreenshotComparisionError
case class ScreenshotNotFound(screenshot: Screenshot)
case class DifferentScreenshots(screenshot: Screenshot)
case class DifferentImageDimensions(screenshot: Screenshot,
                                    originalDimension: Dimension,
                                    newDimension: Dimension)

case class ScreenshotsComparisionResult(errors: ScreenshotComparisionErrors,
                                        screenshots: ScreenshotsSuite)
