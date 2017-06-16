package com.karumi.shot

import com.karumi.shot.domain.Config
import com.karumi.shot.domain.model.Folder

class Shot(val adb: Adb) {

  def configureAdbPath(adbPath: Folder) =
    Adb.adbBinaryPath = adbPath

  def pullScreenshots(projectFolder: Folder): Unit = {
    val screenshotsFolder = projectFolder + Config.screenshotsFolderName
    adb.pullScreenshots(screenshotsFolder)
  }

  def clearScreenshots(): Unit =
    adb.clearScreenshots()

}
