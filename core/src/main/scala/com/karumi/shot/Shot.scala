package com.karumi.shot

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

import java.io.File
import java.nio.file.Paths

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

  def downloadScreenshots(appId: AppId, shotFolder: ShotFolder): Unit = {
    console.show("⬇️  Pulling screenshots from your connected devices!")
    pullScreenshots(appId, shotFolder)
  }

  def recordScreenshots(
      appId: AppId,
      shotFolder: ShotFolder,
      projectName: String
  ): Unit = {
    console.show("💾  Saving screenshots.")
    moveComposeScreenshotsToRegularScreenshotsFolder(shotFolder)
    val composeScreenshotSuite = recordComposeScreenshots(shotFolder)
    val regularScreenshotSuite = recordRegularScreenshots(shotFolder, projectName)
    if (regularScreenshotSuite.isEmpty && composeScreenshotSuite.isEmpty) {
      console.showWarning(
        "🤔 We couldn't find any screenshot. Did you configure Shot properly and added your tests to your project? https://github.com/Karumi/Shot/#getting-started"
      )
    } else {
      val screenshots = regularScreenshotSuite.get ++ composeScreenshotSuite.get
      console.show("😃  Screenshots recorded and saved at: " + shotFolder.screenshotsFolder())
      reporter.generateRecordReport(appId, screenshots, shotFolder)
      console.show(
        "🤓  You can review the execution report here: " + shotFolder.reportFolder() + "index.html"
      )
      removeProjectTemporalScreenshotsFolder(shotFolder)
    }
  }

  def verifyScreenshots(
      appId: AppId,
      shotFolder: ShotFolder,
      projectName: String,
      shouldPrintBase64Error: Boolean,
      tolerance: Double,
      showOnlyFailingTestsInReports: Boolean
  ): ScreenshotsComparisionResult = {
    console.show("🔎  Comparing screenshots with previous ones.")
    moveComposeScreenshotsToRegularScreenshotsFolder(shotFolder)
    val regularScreenshots = readScreenshotsMetadata(shotFolder)
    val composeScreenshots = readComposeScreenshotsMetadata(shotFolder)
    if (regularScreenshots.isEmpty && composeScreenshots.isEmpty) {
      console.showWarning(
        "🤔 We couldn't find any screenshot. Did you configure Shot properly and added your tests to your project? https://github.com/Karumi/Shot/#getting-started"
      )
      ScreenshotsComparisionResult()
    } else {
      val screenshots                            = regularScreenshots.get ++ composeScreenshots.get
      val newScreenshotsVerificationReportFolder = shotFolder.verificationReportFolder() + "images/"
      screenshotsSaver.saveTemporalScreenshots(
        screenshots,
        projectName,
        newScreenshotsVerificationReportFolder,
        shotFolder
      )
      val comparison = screenshotsComparator.compare(screenshots, tolerance)
      val updatedComparison = screenshotsDiffGenerator.generateDiffs(
        comparison,
        newScreenshotsVerificationReportFolder,
        shouldPrintBase64Error
      )

      if (showOnlyFailingTestsInReports) {
        val verificationReferenceImagesFolder = shotFolder.verificationReportFolder() + "images/"
        screenshotsSaver.removeNonFailingReferenceImages(
          verificationReferenceImagesFolder,
          comparison
        )
        screenshotsSaver.copyOnlyFailingRecordedScreenshotsToTheReportFolder(
          shotFolder.verificationReportFolder() + "images/recorded/",
          updatedComparison
        )
      } else {
        screenshotsSaver.copyRecordedScreenshotsToTheReportFolder(
          shotFolder.screenshotsFolder(),
          shotFolder.verificationReportFolder() + "images/recorded/"
        )
      }

      if (updatedComparison.hasErrors) {
        consoleReporter.showErrors(updatedComparison, newScreenshotsVerificationReportFolder)

        console.showError("🤔 Do you a need a hand with your automated tests or your Android app?")
        console.showError("   We'll be happy to help! Send us an email to hello@karumi.com\n")
      } else {
        console.showSuccess("✅  Yeah!!! Your tests are passing.")
      }
      removeProjectTemporalScreenshotsFolder(shotFolder)
      reporter.generateVerificationReport(
        appId,
        comparison,
        shotFolder,
        showOnlyFailingTestsInReports
      )
      console.show(
        "🤓  You can review the execution report here: " + shotFolder
          .verificationReportFolder() + "index.html"
      )
      comparison
    }
  }

  def removeScreenshots(appId: AppId): Unit =
    clearScreenshots(appId)

  private def moveComposeScreenshotsToRegularScreenshotsFolder(
      shotFolder: ShotFolder
  ): Unit = {
    val composeFolder = shotFolder.pulledComposeScreenshotsFolder()
    files.listFilesInFolder(composeFolder).forEach { file: File =>
      val rawFilePath = file.getAbsolutePath
      val newFilePath =
        rawFilePath.replace(
          shotFolder.pulledComposeScreenshotsFolder(),
          shotFolder.pulledScreenshotsFolder()
        )
      files.rename(rawFilePath, newFilePath)
    }
  }

  private def recordRegularScreenshots(shotFolder: ShotFolder, projectName: String) = {
    readScreenshotsMetadata(shotFolder)
      .map { screenshots =>
        screenshotsSaver.saveRecordedScreenshots(shotFolder.screenshotsFolder(), screenshots)
        screenshotsSaver.copyRecordedScreenshotsToTheReportFolder(
          shotFolder.screenshotsFolder(),
          shotFolder.recordingReportFolder() + "images/recorded/"
        )
        screenshots
      }
  }

  private def recordComposeScreenshots(
      shotFolder: ShotFolder
  ) = {
    readComposeScreenshotsMetadata(shotFolder).map { screenshots =>
      screenshotsSaver.saveRecordedScreenshots(shotFolder.screenshotsFolder(), screenshots)
      screenshotsSaver.copyRecordedScreenshotsToTheReportFolder(
        shotFolder.screenshotsFolder(),
        shotFolder.recordingReportFolder() + "images/recorded/"
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
      appId: AppId,
      shotFolder: ShotFolder
  ): Unit =
    forEachDevice { device =>
      val screenshotsFolder = shotFolder.screenshotsFolder()
      createScreenshotsFolderIfDoesNotExist(screenshotsFolder)
      adb.pullScreenshots(device, screenshotsFolder, appId)

      extractPicturesFromBundle(shotFolder.pulledScreenshotsFolder())
      files.rename(shotFolder.metadataFile(), s"${shotFolder.metadataFile()}_$device")
      files.rename(shotFolder.composeMetadataFile(), s"${shotFolder.composeMetadataFile()}_$device")
    }

  private def readScreenshotsMetadata(
      shotFolder: ShotFolder
   ): Option[ScreenshotsSuite] = {
    val screenshotsFolder = shotFolder.pulledScreenshotsFolder()
    val folder            = new File(screenshotsFolder)
    if (folder.exists()) {
      val filesInScreenshotFolder = folder.listFiles
      val metadataFiles =
        filesInScreenshotFolder.filter(file => file.getAbsolutePath.contains("metadata.xml"))
      val screenshotSuite = metadataFiles.flatMap { metadataFilePath =>
        val metadataFileContent = files.read(metadataFilePath.getAbsolutePath)
        parseScreenshots(
          metadataFileContent,
          shotFolder.screenshotsFolder(),
          shotFolder.pulledScreenshotsFolder(),
          shotFolder.screenshotsTemporalBuildPath()
        )
      }
      val suite = screenshotSuite.par.map { screenshot =>
        val viewHierarchyFileName = shotFolder.pulledScreenshotsFolder() + screenshot.viewHierarchy
        val viewHierarchyContent  = files.read(viewHierarchyFileName)
        parseScreenshotSize(screenshot, viewHierarchyContent)
      }.toList
      Some(suite)
    } else {
      None
    }
  }

  private def readComposeScreenshotsMetadata(
      shotFolder: ShotFolder
  ): Option[ScreenshotsSuite] = {
    val screenshotsFolder = shotFolder.pulledScreenshotsFolder()
    val folder            = new File(screenshotsFolder)
    if (folder.exists()) {
      val filesInScreenshotFolder = folder.listFiles
      val metadataFiles =
        filesInScreenshotFolder.filter(file => file.getAbsolutePath.contains("metadata.json"))
      val screenshotSuite = metadataFiles.flatMap { metadataFilePath =>
        val metadataFileContent = files.read(metadataFilePath.getAbsolutePath)
        ScreenshotsComposeSuiteJsonParser.parseScreenshots(
          metadataFileContent,
          shotFolder.screenshotsFolder(),
          shotFolder.pulledScreenshotsFolder(),
          shotFolder.screenshotsTemporalBuildPath()
        )
      }
      val suite = screenshotSuite.map { screenshot =>
        val dimension =
          screenshotsSaver.getScreenshotDimension(shotFolder, screenshot)
        screenshot.copy(screenshotDimension = dimension)
      }.toList
      Some(suite)
    } else {
      None
    }
  }

  private def removeProjectTemporalScreenshotsFolder(shotFolder: ShotFolder): Unit = {
    val projectTemporalScreenshots = new File(shotFolder.pulledScreenshotsFolder())

    if (projectTemporalScreenshots.exists()) {
      FileUtils.deleteDirectory(projectTemporalScreenshots)
    }
  }

  private def extractPicturesFromBundle(screenshotsFolder: String): Unit = {
    val bundleFile = s"${screenshotsFolder}screenshot_bundle.zip"
    if (java.nio.file.Files.exists(Paths.get(bundleFile))) {
      TinyZip.unzip(bundleFile, screenshotsFolder)
    }
  }
}
