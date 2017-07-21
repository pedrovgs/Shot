package com.karumi.shot

import com.karumi.shot.android.Adb
import com.karumi.shot.domain.model.{AppId, Folder, ScreenshotsSuite}
import com.karumi.shot.domain.{
  Config,
  DifferentImageDimensions,
  DifferentScreenshots,
  ScreenshotNotFound
}
import com.karumi.shot.screenshots.ScreenshotsComparator
import com.karumi.shot.ui.Console
import com.karumi.shot.xml.ScreenshotsSuiteXmlParser._

object Shot {
  private val appIdErrorMessage =
    "Error found executing screenshot tests. The appId param is not configured properly. You should configure the appId following the plugin instructions you can find at https://github.com/karumi/shot"
}

class Shot(val adb: Adb,
           val fileReader: Files,
           val screenshotsComparator: ScreenshotsComparator,
           console: Console) {

  import Shot._

  def configureAdbPath(adbPath: Folder): Unit = {
    Adb.adbBinaryPath = adbPath
  }

  def pullScreenshots(projectFolder: Folder, appId: Option[AppId]): Unit =
    executeIfAppIdIsValid(appId) { applicationId =>
      console.show("Pulling screenshots from your connected device!")
      pullScreenshots(projectFolder, applicationId)
      console.show(
        "Let's compare the pulled screenshots with the already recorded ones!")
      val screenshots = readScreenshotsMetadata(projectFolder)
      val compare = screenshotsComparator.compare(screenshots)
      if (compare.errors.isEmpty) {
        console.showSuccess("Yeah!!! You didn't break your tests")
      } else {
        console.showError(
          "Hummmm...you've broken the following screenshot tests:")
        compare.errors.foreach {
          case ScreenshotNotFound(screenshot) =>
            console.showError(
              "Original screenshot not shown: " + screenshot.name)
          case DifferentScreenshots(screenshot) =>
            console.showError(
              "The application UI has been modified and we've noticed that thanks to this test: " + screenshot.name)
          case DifferentImageDimensions(screenshot,
                                        originalDimension,
                                        newDimension) => {
            console.showError(
              "The size of the screenshot taken has changed: " + screenshot.name)
            console.showError(
              "Original dimension: " + originalDimension + ". New dimension: " + newDimension)
          }

          case _ => console.showError("Ups! Something went wrong :(")
        }
      }
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
      projectFolder: Folder): ScreenshotsSuite = {
    val metadataFilePath = projectFolder + Config.metadataFileName
    val metadataFileContent = fileReader.read(metadataFilePath)
    val screenshotSuite = parseScreenshots(
      metadataFileContent,
      projectFolder + Config.screenshotsFolderName,
      projectFolder + Config.temporalScreenshotsFolder)
    screenshotSuite.par.map { screenshot =>
      val viewHierarchyContent = fileReader.read(
        projectFolder + Config.temporalScreenshotsFolder + screenshot.viewHierarchy)
      parseScreenshotSize(screenshot, viewHierarchyContent)
    }.toList
  }

}
