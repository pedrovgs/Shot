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

  val shotConfiguration: String = "shotDependencies"
  val androidDependencyMode: FilePath = "androidTestImplementation"
  val androidDependencyGroup: String = "com.karumi"
  val androidDependencyName: String = "shot-android"
  val androidDependencyVersion: String = "4.4.0"
  val androidDependency: FilePath =
    s"$androidDependencyGroup:$androidDependencyName:$androidDependencyVersion"
  def screenshotsFolderName(flavor: String, buildType: String): FilePath =
    if (flavor.isEmpty) {
      s"/screenshots/$buildType/"
    } else {
      s"/screenshots/$flavor/$buildType/"
    }
  def pulledScreenshotsFolder(flavor: String, buildType: String): FilePath =
    screenshotsFolderName(flavor, buildType) + "screenshots-default/"
  def metadataFileName(flavor: String, buildType: String): FilePath =
    pulledScreenshotsFolder(flavor, buildType) + "metadata.xml"
  val androidPluginName: FilePath = "com.android.application"
  val screenshotsTemporalRootPath: FilePath = "/tmp/shot/screenshot/"
  def defaultInstrumentationTestTask(flavor: String,
                                     buildType: String): String =
    s"connected${flavor.capitalize}${buildType.capitalize}AndroidTest"
  def composerInstrumentationTestTask(flavor: String, buildType: String) =
    s"test${flavor.capitalize}${buildType.capitalize}Composer"
  val defaultPackageTestApkTask: String = "packageDebugAndroidTest"
  def reportFolder(flavor: String, buildType: String): String = "/reports/shot"
  def verificationReportFolder(flavor: String, buildType: String): String =
    reportFolder(flavor, buildType) + "/verification"
  def recordingReportFolder(flavor: String, buildType: String): String =
    reportFolder(flavor, buildType) + "/record"
  val defaultTaskName: String = "executeScreenshotTests"
}

case class Screenshot(name: String,
                      recordedScreenshotPath: String,
                      temporalScreenshotPath: String,
                      testClass: String,
                      testName: String,
                      tilesDimension: Dimension,
                      viewHierarchy: FilePath,
                      absoluteFileNames: Seq[FilePath],
                      relativeFileNames: Seq[FilePath],
                      recordedPartsPaths: Seq[FilePath],
                      screenshotDimension: Dimension) {
  val fileName: String =
    temporalScreenshotPath.substring(
      temporalScreenshotPath.lastIndexOf("/") + 1,
      temporalScreenshotPath.length)

  def getDiffScreenshotPath(basePath: String): String =
    s"${basePath}diff_$fileName"

}

case class Dimension(width: Int, height: Int) {
  override def toString: FilePath = width + "x" + height
}

sealed trait ScreenshotComparisionError

case class ScreenshotNotFound(screenshot: Screenshot)
    extends ScreenshotComparisionError

case class DifferentScreenshots(screenshot: Screenshot,
                                base64Diff: Option[String] = None)
    extends ScreenshotComparisionError

case class DifferentImageDimensions(screenshot: Screenshot,
                                    originalDimension: Dimension,
                                    newDimension: Dimension)
    extends ScreenshotComparisionError

case class ScreenshotsComparisionResult(errors: ScreenshotComparisionErrors,
                                        screenshots: ScreenshotsSuite) {
  val hasErrors: Boolean = errors.nonEmpty
}
