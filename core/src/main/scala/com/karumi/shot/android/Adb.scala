package com.karumi.shot.android

import com.karumi.shot.android.Adb.{baseStoragePath, orchestrated}
import com.karumi.shot.domain.model.{AppId, Folder}

import scala.sys.process._

object Adb {
  var adbBinaryPath: String = ""
  // To be able to support API 29+ with scoped storage we need to change
  // the base url where the app saves our screenshots inside the device.
  // This value is computed in runtime in shot-android AndroidStorageInfo.
  private val baseStoragePath = "/storage/emulated/0/Download"

  private val orchestrated: Boolean = true
}

class Adb {

  private final val CR_ASCII_DECIMAL = 13
  private val logger = ProcessLogger(
    outputMessage => println("Shot ADB output: " + outputMessage),
    errorMessage => println(Console.YELLOW + "Shot ADB warning: " + errorMessage + Console.RESET)
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

  def clearScreenshots(device: String, appId: AppId): Unit = {
    clearScreenshotsFromFolder(device, appId, "screenshots-default")
    clearScreenshotsFromFolder(device, appId, "screenshots-compose-default")
  }

  private def pullFolder(
      folderName: String,
      device: String,
      screenshotsFolder: Folder,
      appId: AppId
  ) = {
    val folderToPull = s"${baseStoragePath}/screenshots${orchestratedSuffix(Adb.orchestrated)}/$appId/$folderName/"
    try {
      executeAdbCommandWithResult(s"-s $device pull $folderToPull $screenshotsFolder")
    } catch {
      case _: Throwable =>
        println(
          Console.YELLOW + s"Shot ADB warning: We could not pull screenshots from folder: ${folderToPull}"
        )
    }
  }

  private def clearScreenshotsFromFolder(device: String, appId: AppId, folder: AppId): Unit = {
    executeAdbCommand(s"-s $device shell rm -r $baseStoragePath/screenshots/$appId/$folder/")
    executeAdbCommand(s"-s $device shell rm -r $baseStoragePath/screenshots${orchestratedSuffix(Adb.orchestrated)}/$appId/$folder/")
  }

  private def executeAdbCommand(command: String): Int =
    s"${Adb.adbBinaryPath} $command" ! logger

  private def executeAdbCommandWithResult(command: String): String =
    s"${Adb.adbBinaryPath} $command" !! logger

  private def isCarriageReturnASCII(device: String): Boolean =
    device.charAt(0) == CR_ASCII_DECIMAL

  private def orchestratedSuffix(orchestrated: Boolean) = if (orchestrated) "-orchestrated" else ""
}
