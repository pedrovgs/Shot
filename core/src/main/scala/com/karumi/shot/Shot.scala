package com.karumi.shot

import com.karumi.shot.android.Adb
import com.karumi.shot.domain.Config
import com.karumi.shot.domain.model.{AppId, Folder, ScreenshotsSuite}
import com.karumi.shot.ui.Console
import com.karumi.shot.xml.ScreenshotsSuiteXmlParser._

object Shot {
  private val appIdErrorMessage =
    "Error found executing screenshot tests. The appId param is not configured properly. You should configure the appId following the plugin instructions you can find at https://github.com/karumi/shot"
}

class Shot(val adb: Adb, val fileReader: Files, val console: Console) {
  import Shot._

  def configureAdbPath(adbPath: Folder): Unit = {
    Adb.adbBinaryPath = adbPath
  }

  def pullScreenshots(projectFolder: Folder, appId: Option[AppId]): Unit =
    executeIfAppIdIsValid(appId) { applicationId =>
      pullScreenshots(projectFolder, applicationId)
      val screenshots = readScreenshotsMetadata(projectFolder)
      print("-------->")
      print(screenshots)
      print("-------->")
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
    parseScreenshots(metadataFileContent)
  }

}
