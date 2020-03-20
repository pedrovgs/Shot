package com.karumi.shot

import java.io.File
import java.nio.file.Paths

import com.karumi.shot.android.Adb
import com.karumi.shot.domain._
import com.karumi.shot.domain.model.{AppId, Folder, ScreenshotsSuite}
import com.karumi.shot.reports.{ConsoleReporter, ExecutionReporter}
import com.karumi.shot.screenshots.{
  ScreenshotsComparator,
  ScreenshotsDiffGenerator,
  ScreenshotsSaver
}
import com.karumi.shot.ui.Console
import com.karumi.shot.xml.ScreenshotsSuiteXmlParser._
import org.apache.commons.io.FileUtils
import org.tinyzip.TinyZip

class Shot(adb: Adb,
           files: Files,
           screenshotsComparator: ScreenshotsComparator,
           screenshotsDiffGenerator: ScreenshotsDiffGenerator,
           screenshotsSaver: ScreenshotsSaver,
           console: Console,
           reporter: ExecutionReporter,
           consoleReporter: ConsoleReporter) {
  def configureAdbPath(adbPath: Folder): Unit = {
    Adb.adbBinaryPath = adbPath
  }

  def downloadScreenshots(projectFolder: Folder,
                          flavor: String,
                          buildType: String,
                          appId: AppId): Unit = {
    console.show("⬇️  Pulling screenshots from your connected devices!")
    pullScreenshots(projectFolder, appId, flavor, buildType)
  }

  def recordScreenshots(appId: AppId,
                        buildFolder: Folder,
                        projectFolder: Folder,
                        projectName: String,
                        flavor: String,
                        buildType: String): Unit = {
    console.show("💾  Saving screenshots.")
    val screenshots =
      readScreenshotsMetadata(projectFolder, flavor, buildType, projectName)
    screenshotsSaver.saveRecordedScreenshots(projectFolder,
                                             flavor,
                                             buildType,
                                             screenshots)
    screenshotsSaver.copyRecordedScreenshotsToTheReportFolder(
      projectFolder,
      flavor,
      buildType,
      buildFolder + Config
        .recordingReportFolder(flavor, buildType) + "/images/recorded/")
    console.show(
      "😃  Screenshots recorded and saved at: " + projectFolder + Config
        .screenshotsFolderName(flavor, buildType))
    reporter.generateRecordReport(appId,
                                  screenshots,
                                  buildFolder,
                                  flavor,
                                  buildType)
    console.show(
      "🤓  You can review the execution report here: " + buildFolder + Config
        .recordingReportFolder(flavor, buildType) + "/index.html")
    removeProjectTemporalScreenshotsFolder(projectFolder, flavor, buildType)
  }

  def verifyScreenshots(
      appId: AppId,
      buildFolder: Folder,
      projectFolder: Folder,
      flavor: String,
      buildType: String,
      projectName: String,
      shouldPrintBase64Error: Boolean): ScreenshotsComparisionResult = {
    console.show("🔎  Comparing screenshots with previous ones.")
    val screenshots =
      readScreenshotsMetadata(projectFolder, flavor, buildType, projectName)
    val newScreenshotsVerificationReportFolder = buildFolder + Config
      .verificationReportFolder(flavor, buildType) + "/images/"
    screenshotsSaver.saveTemporalScreenshots(
      screenshots,
      projectName,
      newScreenshotsVerificationReportFolder)
    val comparision = screenshotsComparator.compare(screenshots)
    val updatedComparision = screenshotsDiffGenerator.generateDiffs(
      comparision,
      newScreenshotsVerificationReportFolder,
      shouldPrintBase64Error)
    screenshotsSaver.copyRecordedScreenshotsToTheReportFolder(
      projectFolder,
      flavor,
      buildType,
      buildFolder + Config
        .verificationReportFolder(flavor, buildType) + "/images/recorded/")

    if (updatedComparision.hasErrors) {
      consoleReporter.showErrors(updatedComparision,
                                 newScreenshotsVerificationReportFolder)
    } else {
      console.showSuccess("✅  Yeah!!! Your tests are passing.")
    }
    removeProjectTemporalScreenshotsFolder(projectFolder, flavor, buildType)
    reporter.generateVerificationReport(appId,
                                        comparision,
                                        buildFolder,
                                        flavor,
                                        buildType)
    console.show(
      "🤓  You can review the execution report here: " + buildFolder + Config
        .verificationReportFolder(flavor, buildType) + "/index.html")
    comparision
  }

  def removeScreenshots(appId: AppId): Unit =
    clearScreenshots(appId)

  private def clearScreenshots(appId: AppId): Unit = adb.devices.foreach {
    device =>
      adb.clearScreenshots(device, appId)
  }

  private def createScreenshotsFolderIfDoesNotExist(screenshotsFolder: AppId) = {
    val folder = new File(screenshotsFolder)
    folder.mkdirs()
  }

  private def pullScreenshots(projectFolder: Folder,
                              appId: AppId,
                              flavor: String,
                              buildType: String): Unit = {
    adb.devices.foreach { device =>
      val screenshotsFolder = projectFolder + Config.screenshotsFolderName(
        flavor,
        buildType)
      createScreenshotsFolderIfDoesNotExist(screenshotsFolder)
      adb.pullScreenshots(device, screenshotsFolder, appId)
      extractPicturesFromBundle(
        projectFolder + Config.pulledScreenshotsFolder(flavor, buildType))
      renameMetadataFile(projectFolder, flavor, buildType, device)
    }
  }

  private def renameMetadataFile(projectFolder: Folder,
                                 flavor: String,
                                 buildType: String,
                                 device: String): Unit = {
    val metadataFilePath = projectFolder + Config.metadataFileName(flavor,
                                                                   buildType)
    val newMetadataFilePath = metadataFilePath + "_" + device
    files.rename(metadataFilePath, newMetadataFilePath)
  }

  private def readScreenshotsMetadata(
      projectFolder: Folder,
      flavor: String,
      buildType: String,
      projectName: String): ScreenshotsSuite = {
    val screenshotsFolder = projectFolder + Config.pulledScreenshotsFolder(
      flavor,
      buildType)
    val filesInScreenshotFolder = new java.io.File(screenshotsFolder).listFiles
    val metadataFiles = filesInScreenshotFolder.filter(
      file =>
        file.getAbsolutePath.contains(
          Config.metadataFileName(flavor, buildType)))
    val screenshotSuite = metadataFiles.flatMap { metadataFilePath =>
      val metadataFileContent =
        files.read(metadataFilePath.getAbsolutePath)
      parseScreenshots(
        metadataFileContent,
        projectName,
        projectFolder + Config.screenshotsFolderName(flavor, buildType),
        projectFolder + Config.pulledScreenshotsFolder(flavor, buildType)
      )
    }
    screenshotSuite.par.map { screenshot =>
      val viewHierarchyFileName = projectFolder + Config
        .pulledScreenshotsFolder(flavor, buildType) + screenshot.viewHierarchy
      val viewHierarchyContent = files.read(viewHierarchyFileName)
      parseScreenshotSize(screenshot, viewHierarchyContent)
    }.toList
  }

  private def removeProjectTemporalScreenshotsFolder(
      projectFolder: Folder,
      flavor: String,
      buildType: String): Unit = {
    val projectTemporalScreenshots = new File(
      projectFolder + Config.pulledScreenshotsFolder(flavor, buildType))

    if (projectTemporalScreenshots.exists()) {
      FileUtils.deleteDirectory(projectTemporalScreenshots)
    }
  }
  private def extractPicturesFromBundle(screenshotsFolder: String): Unit = {
    val bundleFile = s"$screenshotsFolder/screenshot_bundle.zip"
    if (java.nio.file.Files.exists(Paths.get(bundleFile))) {
      TinyZip.unzip(bundleFile, screenshotsFolder)
    }
  }
}
