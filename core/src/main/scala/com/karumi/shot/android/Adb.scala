package com.karumi.shot.android

import com.karumi.shot.android.Adb.{baseStoragePath}
import com.karumi.shot.domain.model.{AppId, Folder}

import scala.sys.process._

object Adb {
  // To be able to support API 29+ with scoped storage we need to change
  // the base url where the app saves our screenshots inside the device.
  // This value is computed in runtime in shot-android AndroidStorageInfo.
  private val baseStoragePath = "/storage/emulated/0/Download"
}

class Adb(
    adbPath: String
) {

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

  def pullScreenshots(
      device: String,
      screenshotsFolder: Folder,
      appId: AppId,
      orchestrated: Boolean
  ): Unit = {
    pullFolder(
      s"screenshots-default${orchestratedSuffix(orchestrated)}",
      device,
      screenshotsFolder,
      appId
    )
    pullFolder("screenshots-compose-default", device, screenshotsFolder, appId)
    if (orchestrated) {
      pullFolder(
        s"screenshots-compose-default${orchestratedSuffix(orchestrated)}",
        device,
        screenshotsFolder,
        appId
      )
    }
  }

  def clearScreenshots(device: String, appId: AppId, orchestrated: Boolean): Unit = {
    clearScreenshotsFromFolder(device, appId, "screenshots-default")
    clearScreenshotsFromFolder(device, appId, "screenshots-compose-default")
    if (orchestrated) {
      clearScreenshotsFromFolder(
        device,
        appId,
        s"screenshots-default${orchestratedSuffix(orchestrated)}"
      )
      clearScreenshotsFromFolder(
        device,
        appId,
        s"screenshots-compose-default${orchestratedSuffix(orchestrated)}"
      )
    }
  }

  private def pullFolder(
      folderName: String,
      device: String,
      screenshotsFolder: Folder,
      appId: AppId
  ) = {
    val folderToPull = s"${baseStoragePath}/screenshots/$appId/$folderName/"
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
  }

  private def executeAdbCommand(command: String): Int =
    s"${adbPath} $command" ! logger

  private def executeAdbCommandWithResult(command: String): String =
    s"${adbPath} $command" !! logger

  private def isCarriageReturnASCII(device: String): Boolean =
    device.charAt(0) == CR_ASCII_DECIMAL

  private def orchestratedSuffix(orchestrated: Boolean) = if (orchestrated) "-orchestrated" else ""
}
