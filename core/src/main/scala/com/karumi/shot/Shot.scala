package com.karumi.shot

import com.karumi.shot.domain.Config
import com.karumi.shot.domain.model.{AppId, Folder}

class Shot(val adb: Adb) {

  def configureAdbPath(adbPath: Folder) =
    Adb.adbBinaryPath = adbPath

  def pullScreenshots(projectFolder: Folder, appId: Option[AppId]): Unit =
    executeIfAppIdIsValid(appId) { applicationId =>
      pullScreenshots(projectFolder, applicationId)
    }

  def clearScreenshots(appId: Option[AppId]): Unit =
    executeIfAppIdIsValid(appId) { applicationId =>
      clearScreenshots(applicationId)
    }

  private def executeIfAppIdIsValid(appId: Option[AppId])(f: AppId => Unit) =
    appId match {
      case Some(applicationId) => f(applicationId)
      case None =>
        println("Configuration error") //TODO: Replace this with the view usage when ready.
    }

  private def clearScreenshots(appId: AppId): Unit =
    adb.clearScreenshots(appId)

  private def pullScreenshots(projectFolder: Folder, appId: AppId): Unit = {
    val screenshotsFolder = projectFolder + Config.screenshotsFolderName
    adb.pullScreenshots(screenshotsFolder, appId)
  }

}
