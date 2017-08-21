package com.karumi.shot

import com.karumi.shot.android.Adb
import com.karumi.shot.domain.model.{AppId, Folder, ScreenshotsSuite}
import com.karumi.shot.domain._
import com.karumi.shot.screenshots.{ScreenshotsComparator, ScreenshotsSaver}
import com.karumi.shot.ui.Console
import com.karumi.shot.xml.ScreenshotsSuiteXmlParser._

object Shot {
  private val appIdErrorMessage =
    "🤔  Error found executing screenshot tests. The appId param is not configured properly. You should configure the appId following the plugin instructions you can find at https://github.com/karumi/shot"
}

class Shot(val adb: Adb,
           val fileReader: Files,
           val screenshotsComparator: ScreenshotsComparator,
           val screenshotsSaver: ScreenshotsSaver,
           console: Console) {

  import Shot._

  def configureAdbPath(adbPath: Folder): Unit = {
    Adb.adbBinaryPath = adbPath
  }

  def pullScreenshots(projectFolder: Folder, appId: Option[AppId]): Unit =
    executeIfAppIdIsValid(appId) { applicationId =>
      console.show("⬇️  Pulling screenshots from your connected device!")
      pullScreenshots(projectFolder, applicationId)
    }

  def recordScreenshots(projectFolder: Folder, projectName: String): Unit = {
    console.show("💾 Saving screenshots")
    val screenshots = readScreenshotsMetadata(projectFolder, projectName)
    screenshotsSaver.saveRecordedScreenshots(projectFolder, screenshots)
    console.showSuccess(
      "😃  Screenshots recorded and saved at: " + projectFolder + Config.screenshotsFolderName)
  }

  def verifyScreenshots(projectFolder: Folder,
                        projectName: String): ScreenshotsComparisionResult = {
    console.show(
      "🔎  Let's verify the pulled screenshots with the already recorded ones!")
    val screenshots = readScreenshotsMetadata(projectFolder, projectName)
    screenshotsSaver.saveTemporalScreenshots(screenshots, projectName)
    val comparision = screenshotsComparator.compare(screenshots)
    if (!comparision.hasErrors) {
      console.showSuccess("✅  Yeah!!! You didn't break your tests")
    } else {
      console.showError(
        "❌  Hummmm...you've broken the following screenshot tests:\n")
      comparision.errors.foreach {
        case ScreenshotNotFound(screenshot) =>
          console.showError(
            "   🔎  Original screenshot not found: " + screenshot.name)
        case DifferentScreenshots(screenshot) =>
          console.showError(
            "   🤔  The application UI has been modified and we've noticed that thanks to this test: " + screenshot.name + ". Check the images and review the differences.")
          console.showError(
            "            💾  You can find the original screenshot here: " + screenshot.recordedScreenshotPath)
          console.showError(
            "            🆕  You can find the new recorded screenshot here: " + screenshot.temporalScreenshotPath)
        case DifferentImageDimensions(screenshot,
                                      originalDimension,
                                      newDimension) => {
          console.showError(
            "   📱  The size of the screenshot taken has changed: " + screenshot.name)
          console.showError("              💾  Original screenshot dimension: " + originalDimension)
          console.showError("              🆕  New recorded screenshot dimension: " + newDimension)
        }

        case _ =>
          console.showError(
            "   😞  Ups! Something went wrong with your test but we couldn't identify the cause.")
      }
    }
    comparision
  }

  def clearScreenshots(appId: Option[AppId]): Unit =
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

  private def pullScreenshots(projectFolder: Folder, appId: AppId): Unit = {
    val screenshotsFolder = projectFolder + Config.screenshotsFolderName
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
      projectFolder + Config.deviceScreenshotsFolder)
    screenshotSuite.par.map { screenshot =>
      val viewHierarchyContent = fileReader.read(
        projectFolder + Config.deviceScreenshotsFolder + screenshot.viewHierarchy)
      parseScreenshotSize(screenshot, viewHierarchyContent)
    }.toList
  }

}
