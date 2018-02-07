package com.karumi.shot

import java.io.{ByteArrayOutputStream, File, FileInputStream, InputStream}
import java.util.Base64
import javax.imageio.ImageIO

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
import org.apache.commons.io.{Charsets, FileUtils}

object Shot {
  private val appIdErrorMessage =
    "🤔  Error found executing screenshot tests. The appId param is not configured properly. You should configure the appId following the plugin instructions you can find at https://github.com/karumi/shot"
}

class Shot(adb: Adb,
           fileReader: Files,
           screenshotsComparator: ScreenshotsComparator,
           screenshotsDiffGenerator: ScreenshotsDiffGenerator,
           screenshotsSaver: ScreenshotsSaver,
           console: Console,
           reporter: ExecutionReporter,
           consoleReporter: ConsoleReporter) {

  import Shot._

  def configureAdbPath(adbPath: Folder): Unit = {
    Adb.adbBinaryPath = adbPath
  }

  def downloadScreenshots(projectFolder: Folder, appId: Option[AppId]): Unit =
    executeIfAppIdIsValid(appId) { applicationId =>
      console.show("⬇️  Pulling screenshots from your connected device!")
      pullScreenshots(projectFolder, applicationId)
    }

  def recordScreenshots(appId: AppId,
                        buildFolder: Folder,
                        projectFolder: Folder,
                        projectName: String): Unit = {
    console.show("💾  Saving screenshots.")
    val screenshots = readScreenshotsMetadata(projectFolder, projectName)
    screenshotsSaver.saveRecordedScreenshots(projectFolder, screenshots)
    screenshotsSaver.copyRecordedScreenshotsToTheReportFolder(
      projectFolder,
      buildFolder + Config.recordingReportFolder + "/images/recorded/")
    console.show(
      "😃  Screenshots recorded and saved at: " + projectFolder + Config.screenshotsFolderName)
    reporter.generateRecordReport(appId, screenshots, buildFolder)
    console.show(
      "🤓  You can review the execution report here: " + buildFolder + Config.recordingReportFolder + "/index.html")
    removeProjectTemporalScreenshotsFolder(projectFolder)
  }

  def verifyScreenshots(
      appId: AppId,
      buildFolder: Folder,
      projectFolder: Folder,
      projectName: String,
      shouldPrintBase64Error: Boolean): ScreenshotsComparisionResult = {
    console.show("🔎  Comparing screenshots with previous ones.")
    val screenshots = readScreenshotsMetadata(projectFolder, projectName)
    val newScreenshotsVerificationReportFolder = buildFolder + Config.verificationReportFolder + "/images/"
    screenshotsSaver.saveTemporalScreenshots(
      screenshots,
      projectName,
      newScreenshotsVerificationReportFolder)
    val comparision = screenshotsComparator.compare(screenshots)
    screenshotsDiffGenerator.generateDiffs(
      comparision,
      newScreenshotsVerificationReportFolder)
    screenshotsSaver.copyRecordedScreenshotsToTheReportFolder(
      projectFolder,
      buildFolder + Config.verificationReportFolder + "/images/recorded/")

    if (comparision.hasErrors) {
      consoleReporter.showErrors(comparision,
                                 shouldPrintBase64Error,
                                 newScreenshotsVerificationReportFolder)
    } else {
      console.showSuccess("✅  Yeah!!! Your tests are passing.")
    }
    removeProjectTemporalScreenshotsFolder(projectFolder)
    reporter.generateVerificationReport(appId, comparision, buildFolder)
    console.show(
      "🤓  You can review the execution report here: " + buildFolder + Config.verificationReportFolder + "/index.html")
    comparision
  }

  def removeScreenshots(appId: Option[AppId]): Unit =
    executeIfAppIdIsValid(appId) { applicationId =>
      clearScreenshots(applicationId)
    }

  private def executeIfAppIdIsValid(appId: Option[AppId])(f: AppId => Unit) =
    appId match {
      case Some(applicationId) => f(applicationId)
      case None => console.showError(appIdErrorMessage)
    }

  private def clearScreenshots(appId: AppId): Unit =
    adb.clearScreenshots(appId)

  private def createScreenshotsFolderIfDoesNotExist(screenshotsFolder: AppId) = {
    val folder = new File(screenshotsFolder)
    folder.mkdirs()
  }

  private def pullScreenshots(projectFolder: Folder, appId: AppId): Unit = {
    val screenshotsFolder = projectFolder + Config.pulledScreenshotsFolder
    createScreenshotsFolderIfDoesNotExist(
      projectFolder + Config.screenshotsFolderName)
    adb.pullScreenshots(screenshotsFolder, appId)
  }

  private def readScreenshotsMetadata(
      projectFolder: Folder,
      projectName: String): ScreenshotsSuite = {
    val metadataFilePath = projectFolder + Config.metadataFileName
    val metadataFileContent = fileReader.read(metadataFilePath)
    val screenshotSuite = parseScreenshots(
      metadataFileContent,
      projectName,
      projectFolder + Config.screenshotsFolderName,
      projectFolder + Config.pulledScreenshotsFolder)
    screenshotSuite.par.map { screenshot =>
      val viewHierarchyContent = fileReader.read(
        projectFolder + Config.pulledScreenshotsFolder + screenshot.viewHierarchy)
      parseScreenshotSize(screenshot, viewHierarchyContent)
    }.toList
  }

  private def removeProjectTemporalScreenshotsFolder(projectFolder: Folder) = {
    val projectTemporalScreenshots = new File(
      projectFolder + Config.pulledScreenshotsFolder)

    if (projectTemporalScreenshots.exists()) {
      FileUtils.deleteDirectory(projectTemporalScreenshots)
    }
  }
}
