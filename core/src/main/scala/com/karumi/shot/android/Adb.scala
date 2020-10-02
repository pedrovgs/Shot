package com.karumi.shot.android

import com.karumi.shot.domain.model.{AppId, Folder}

import scala.sys.process._

object Adb {
  var adbBinaryPath: String = ""
}

class Adb {

  private final val CR_ASCII_DECIMAL = 13
  private val logger = ProcessLogger(
    outputMessage => println("Shot ADB output: " + outputMessage),
    errorMessage => println(Console.RED + "Shot ADB error: " + errorMessage + Console.RESET)
  )

  def devices: List[String] = {
    executeAdbCommandWithResult("devices")
      .split('\n')
      .toList
      .drop(1)
      .map { line =>
        line.split('\t').toList.head
      }
      .filter(device => !isCarriageReturnASCII(device))
  }

  def pullScreenshots(device: String, screenshotsFolder: Folder, appId: AppId): Unit = {
    pullFolder("screenshots-default", device, screenshotsFolder, appId)
    pullFolder("screenshots-compose-default", device, screenshotsFolder, appId)
  }

  def clearScreenshots(device: String, appId: AppId): Unit =
    executeAdbCommand(s"-s $device shell rm -r /sdcard/screenshots/$appId/screenshots-default/")

  private def pullFolder(folderName: String,
                         device: String,
                         screenshotsFolder: Folder,
                         appId: AppId) =
    executeAdbCommandWithResult(
      s"-s $device pull /sdcard/screenshots/$appId/$folderName/ $screenshotsFolder")

  private def executeAdbCommand(command: String): Int =
    s"${Adb.adbBinaryPath} $command" ! logger

  private def executeAdbCommandWithResult(command: String): String =
    s"${Adb.adbBinaryPath} $command" !! logger

  private def isCarriageReturnASCII(device: String): Boolean =
    device.charAt(0) == CR_ASCII_DECIMAL
}
