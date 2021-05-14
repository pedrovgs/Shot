package com.karumi.shot

import java.io.File
import java.nio.file.Paths

import com.karumi.shot.android.Adb
import com.karumi.shot.domain._
import com.karumi.shot.domain.model.{AppId, Folder, ScreenshotsSuite}
import com.karumi.shot.json.ScreenshotsComposeSuiteJsonParser
import com.karumi.shot.reports.{ConsoleReporter, ExecutionReporter}
import com.karumi.shot.screenshots.{
  ScreenshotsComparator,
  ScreenshotsDiffGenerator,
  ScreenshotsSaver
}
import com.karumi.shot.system.EnvVars
import com.karumi.shot.ui.Console
import com.karumi.shot.xml.ScreenshotsSuiteXmlParser._
import org.apache.commons.io.FileUtils
import org.tinyzip.TinyZip

class Shot(
    adb: Adb,
    files: Files,
    screenshotsComparator: ScreenshotsComparator,
    screenshotsDiffGenerator: ScreenshotsDiffGenerator,
    screenshotsSaver: ScreenshotsSaver,
    console: Console,
    reporter: ExecutionReporter,
    consoleReporter: ConsoleReporter,
    envVars: EnvVars
) {
  def configureAdbPath(adbPath: Folder): Unit = {
    Adb.adbBinaryPath = adbPath
  }

  def downloadScreenshots(
      projectFolder: Folder,
      flavor: String,
      buildType: String,
      appId: AppId
  ): Unit = {
    console.show("⬇️  Pulling screenshots from your connected devices!")
    pullScreenshots(projectFolder, appId, flavor, buildType)
  }

  def recordScreenshots(
      appId: AppId,
      buildFolder: Folder,
      projectFolder: Folder,
      projectName: String,
      flavor: String,
      buildType: String
  ): Unit = {
    console.show("💾  Saving screenshots.")
    moveComposeScreenshotsToRegularScreenshotsFolder(projectFolder, flavor, buildType)
    val composeScreenshotSuite =
      recordComposeScreenshots(buildFolder, projectFolder, projectName, flavor, buildType)
    val regularScreenshotSuite =
      recordRegularScreenshots(buildFolder, projectFolder, projectName, flavor, buildType)
    if (regularScreenshotSuite.isEmpty && composeScreenshotSuite.isEmpty) {
      console.showWarning(
        "🤔 We couldn't find any screenshot. Did you configure Shot properly and added your tests to your project? https://github.com/Karumi/Shot/#getting-started"
      )
    } else {
      val screenshots = regularScreenshotSuite.get ++ composeScreenshotSuite.get
      console.show(
        "😃  Screenshots recorded and saved at: " + projectFolder + Config
          .screenshotsFolderName(flavor, buildType)
      )
      reporter.generateRecordReport(appId, screenshots, buildFolder, flavor, buildType)
      console.show(
        "🤓  You can review the execution report here: " + buildFolder + Config
          .recordingReportFolder(flavor, buildType) + "/index.html"
      )
      removeProjectTemporalScreenshotsFolder(projectFolder, flavor, buildType)
    }
  }

  def verifyScreenshots(
      appId: AppId,
      buildFolder: Folder,
      projectFolder: Folder,
      flavor: String,
      buildType: String,
      projectName: String,
      shouldPrintBase64Error: Boolean,
      tolerance: Double,
      showOnlyFailingTestsInReports: Boolean
  ): ScreenshotsComparisionResult = {
    console.show("🔎  Comparing screenshots with previous ones.")
    moveComposeScreenshotsToRegularScreenshotsFolder(projectFolder, flavor, buildType)
    val regularScreenshots =
      readScreenshotsMetadata(projectFolder, flavor, buildType, projectName)
    val composeScreenshots =
      readComposeScreenshotsMetadata(projectFolder, flavor, buildType, projectName)
    if (regularScreenshots.isEmpty && composeScreenshots.isEmpty) {
      console.showWarning(
        "🤔 We couldn't find any screenshot. Did you configure Shot properly and added your tests to your project? https://github.com/Karumi/Shot/#getting-started"
      )
      ScreenshotsComparisionResult()
    } else {
      val screenshots = regularScreenshots.get ++ composeScreenshots.get
      val newScreenshotsVerificationReportFolder = buildFolder + Config
        .verificationReportFolder(flavor, buildType) + "/images/"
      screenshotsSaver.saveTemporalScreenshots(
        screenshots,
        projectName,
        newScreenshotsVerificationReportFolder
      )
      val comparison = screenshotsComparator.compare(screenshots, tolerance)
      val updatedComparison = screenshotsDiffGenerator.generateDiffs(
        comparison,
        newScreenshotsVerificationReportFolder,
        shouldPrintBase64Error
      )

      if (showOnlyFailingTestsInReports) {
        val verificationReferenceImagesFolder = buildFolder + Config
          .verificationReportFolder(flavor, buildType) + "/images/"
        screenshotsSaver.removeNonFailingReferenceImages(
          verificationReferenceImagesFolder,
          comparison
        )
        screenshotsSaver.copyOnlyFailingRecordedScreenshotsToTheReportFolder(
          buildFolder + Config
            .verificationReportFolder(flavor, buildType) + "/images/recorded/",
          updatedComparison
        )
      } else {
        screenshotsSaver.copyRecordedScreenshotsToTheReportFolder(
          projectFolder,
          flavor,
          buildType,
          buildFolder + Config
            .verificationReportFolder(flavor, buildType) + "/images/recorded/"
        )
      }

      if (updatedComparison.hasErrors) {
        consoleReporter.showErrors(updatedComparison, newScreenshotsVerificationReportFolder)

        console.showError("🤔 Do you a need a hand with your automated tests or your Android app?")
        console.showError("   We'll be happy to help! Send us an email to hello@karumi.com\n")
      } else {
        console.showSuccess("✅  Yeah!!! Your tests are passing.")
      }
      removeProjectTemporalScreenshotsFolder(projectFolder, flavor, buildType)
      reporter.generateVerificationReport(
        appId,
        comparison,
        buildFolder,
        flavor,
        buildType,
        showOnlyFailingTestsInReports
      )
      console.show(
        "🤓  You can review the execution report here: " + buildFolder + Config
          .verificationReportFolder(flavor, buildType) + "/index.html"
      )
      comparison
    }
  }

  def removeScreenshots(appId: AppId): Unit =
    clearScreenshots(appId)

  private def moveComposeScreenshotsToRegularScreenshotsFolder(
      projectFolder: Folder,
      flavor: String,
      buildType: String
  ): Unit = {
    val composeFolder = projectFolder + Config.pulledComposeScreenshotsFolder(flavor, buildType)
    files.listFilesInFolder(composeFolder).forEach { file: File =>
      val rawFilePath = file.getAbsolutePath
      val newFilePath =
        rawFilePath.replace(
          Config.pulledComposeScreenshotsFolder(flavor, buildType),
          Config.pulledScreenshotsFolder(flavor, buildType)
        )
      files.rename(rawFilePath, newFilePath)
    }
  }

  private def recordRegularScreenshots(
      buildFolder: Folder,
      projectFolder: Folder,
      projectName: String,
      flavor: String,
      buildType: String
  ) = {
    readScreenshotsMetadata(projectFolder, flavor, buildType, projectName)
      .map { screenshots =>
        screenshotsSaver.saveRecordedScreenshots(projectFolder, flavor, buildType, screenshots)
        screenshotsSaver.copyRecordedScreenshotsToTheReportFolder(
          projectFolder,
          flavor,
          buildType,
          buildFolder + Config
            .recordingReportFolder(flavor, buildType) + "/images/recorded/"
        )
        screenshots
      }
  }

  private def recordComposeScreenshots(
      buildFolder: Folder,
      projectFolder: Folder,
      projectName: String,
      flavor: String,
      buildType: String
  ) = {
    readComposeScreenshotsMetadata(projectFolder, flavor, buildType, projectName).map {
      screenshots =>
        screenshotsSaver.saveRecordedScreenshots(projectFolder, flavor, buildType, screenshots)
        screenshotsSaver.copyRecordedScreenshotsToTheReportFolder(
          projectFolder,
          flavor,
          buildType,
          buildFolder + Config
            .recordingReportFolder(flavor, buildType) + "/images/recorded/"
        )
        screenshots
    }
  }

  private def clearScreenshots(appId: AppId): Unit = forEachDevice { device =>
    adb.clearScreenshots(device, appId)
  }

  private def forEachDevice[T](f: String => T): Unit = devices().foreach(f)

  private def devices(): List[String] = {
    val allDevices      = adb.devices
    val specifiedDevice = envVars.androidSerial
    specifiedDevice match {
      case Some(device) if allDevices.contains(device) => List(device)
      case _                                           => allDevices
    }
  }

  private def createScreenshotsFolderIfDoesNotExist(screenshotsFolder: AppId) = {
    val folder = new File(screenshotsFolder)
    folder.mkdirs()
  }

  private def pullScreenshots(
      projectFolder: Folder,
      appId: AppId,
      flavor: String,
      buildType: String
  ): Unit =
    forEachDevice { device =>
      val screenshotsFolder = projectFolder + Config.screenshotsFolderName(flavor, buildType)
      createScreenshotsFolderIfDoesNotExist(screenshotsFolder)
      adb.pullScreenshots(device, screenshotsFolder, appId)

      extractPicturesFromBundle(projectFolder + Config.pulledScreenshotsFolder(flavor, buildType))
      renameMetadataFile(projectFolder, device, Config.metadataFileName(flavor, buildType))
      renameMetadataFile(projectFolder, device, Config.composeMetadataFileName(flavor, buildType))
    }

  private def renameMetadataFile(
      projectFolder: Folder,
      device: String,
      metadataFileName: String
  ): Unit = {
    val metadataFilePath    = projectFolder + metadataFileName
    val newMetadataFilePath = metadataFilePath + "_" + device
    files.rename(metadataFilePath, newMetadataFilePath)
  }

  private def readScreenshotsMetadata(
      projectFolder: Folder,
      flavor: String,
      buildType: String,
      projectName: String
  ): Option[ScreenshotsSuite] = {
    val screenshotsFolder = projectFolder + Config.pulledScreenshotsFolder(flavor, buildType)
    val folder            = new File(screenshotsFolder)
    if (folder.exists()) {
      val filesInScreenshotFolder = folder.listFiles
      val metadataFiles = filesInScreenshotFolder.filter(file =>
        file.getAbsolutePath.contains(Config.metadataFileName(flavor, buildType))
      )
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
      val suite = screenshotSuite.par.map { screenshot =>
        val viewHierarchyFileName = projectFolder + Config
          .pulledScreenshotsFolder(flavor, buildType) + screenshot.viewHierarchy
        val viewHierarchyContent = files.read(viewHierarchyFileName)
        parseScreenshotSize(screenshot, viewHierarchyContent)
      }.toList
      Some(suite)
    } else {
      None
    }
  }

  private def readComposeScreenshotsMetadata(
      projectFolder: Folder,
      flavor: String,
      buildType: String,
      projectName: String
  ): Option[ScreenshotsSuite] = {
    val screenshotsFolder = projectFolder + Config.pulledScreenshotsFolder(flavor, buildType)
    val folder            = new File(screenshotsFolder)
    if (folder.exists()) {
      val filesInScreenshotFolder = folder.listFiles
      val metadataFiles =
        filesInScreenshotFolder.filter(file => file.getAbsolutePath.contains("metadata.json"))
      val screenshotSuite = metadataFiles.flatMap { metadataFilePath =>
        val metadataFileContent =
          files.read(metadataFilePath.getAbsolutePath)
        ScreenshotsComposeSuiteJsonParser.parseScreenshots(
          metadataFileContent,
          projectName,
          projectFolder + Config.screenshotsFolderName(flavor, buildType),
          projectFolder + Config.pulledScreenshotsFolder(flavor, buildType)
        )
      }
      val suite = screenshotSuite.par.map { screenshot =>
        val dimension =
          screenshotsSaver.getScreenshotDimension(projectFolder, flavor, buildType, screenshot)
        screenshot.copy(screenshotDimension = dimension)
      }.toList
      Some(suite)
    } else {
      None
    }
  }

  private def removeProjectTemporalScreenshotsFolder(
      projectFolder: Folder,
      flavor: String,
      buildType: String
  ): Unit = {
    val projectTemporalScreenshots = new File(
      projectFolder + Config.pulledScreenshotsFolder(flavor, buildType)
    )

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
