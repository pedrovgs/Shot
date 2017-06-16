package com.karumi.shot

import com.karumi.shot.android.Adb
import com.karumi.shot.domain.Config
import com.karumi.shot.domain.model.{AppId, Folder}
import com.karumi.shot.ui.Console

object Shot {
  private val appIdErrorMessage =
    "Error found executing screenshot tests. The appId param is not configured properly. You should configure the appId following the plugin instructions you can find at https://github.com/karumi/shot"
}

class Shot(val adb: Adb, val console: Console) {
  import Shot._

  def configureAdbPath(adbPath: Folder): Unit = {
    Adb.adbBinaryPath = adbPath
  }

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
      case None => console.showError(appIdErrorMessage)
    }

  private def clearScreenshots(appId: AppId): Unit =
    adb.clearScreenshots(appId)

  private def pullScreenshots(projectFolder: Folder, appId: AppId): Unit = {
    val screenshotsFolder = projectFolder + Config.screenshotsFolderName
    adb.pullScreenshots(screenshotsFolder, appId)
  }

}
