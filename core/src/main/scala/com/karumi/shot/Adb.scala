package com.karumi.shot

import com.karumi.shot.domain.model.Folder

import scala.sys.process._

object Adb {
  var adbBinaryPath: String = ""
}

class Adb {

  def pullScreenshots(screenshotsFolder: Folder): Unit =
    executeAdbCommand(
      "pull /sdcard/screenshots/com.karumi.screenshot.test/screenshots-default/ .")

  def clearScreenshots(): Unit =
    executeAdbCommand(
      "shell rm -r /sdcard/screenshots/com.karumi.screenshot.test/screenshots-default/")

  private def executeAdbCommand(command: String): Int =
    (Adb.adbBinaryPath + " " + command).!

}
