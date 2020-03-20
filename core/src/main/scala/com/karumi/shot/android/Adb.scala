package com.karumi.shot.android

import com.karumi.shot.domain.model.{AppId, Folder}

import scala.sys.process._

object Adb {
  var adbBinaryPath: String = ""
}

class Adb {

  final val CR_ASCII_DECIMAL = 13

  def devices: List[String] = {
    listWithoutCarriageReturn(executeAdbCommandWithResult("devices").split('\n').toList.drop(1).map {
      line =>
        line.split('\t').toList.head
    })
  }

  def listWithoutCarriageReturn(strings: List[String]): List[String] = {
    var result = List[String]()
    strings.foreach{
      line =>
        if(!isCarriageReturnASCII(line))
          result = line :: result
    }
    result
  }

  def isCarriageReturnASCII(device: String): Boolean = device.charAt(0) == CR_ASCII_DECIMAL

  def pullScreenshots(device: String,
                      screenshotsFolder: Folder,
                      appId: AppId): Unit =
    executeAdbCommandWithResult(
      s"-s $device pull /sdcard/screenshots/$appId.test/screenshots-default/ $screenshotsFolder")

  def clearScreenshots(device: String, appId: AppId): Unit =
    executeAdbCommand(
      s"-s $device shell rm -r /sdcard/screenshots/$appId.test/screenshots-default/")

  private def executeAdbCommand(command: String): Int =
    s"${Adb.adbBinaryPath} $command".!

  private def executeAdbCommandWithResult(command: String): String =
    s"${Adb.adbBinaryPath} $command".!!

}
