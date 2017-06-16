package com.karumi.shot

import com.karumi.shot.domain.model.{AppId, Folder}

import scala.sys.process._

object Adb {
  var adbBinaryPath: String = ""
}

class Adb {

  def pullScreenshots(screenshotsFolder: Folder, appId: AppId): Unit =
    executeAdbCommand(
      "pull /sdcard/screenshots/" + appId + ".test/screenshots-default/ " + screenshotsFolder)

  def clearScreenshots(appId: AppId): Unit =
    executeAdbCommand(
      "shell rm -r /sdcard/screenshots/" + appId + ".test/screenshots-default/")

  private def executeAdbCommand(command: String): Int =
    (Adb.adbBinaryPath + " " + command).!

}
