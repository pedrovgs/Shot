package com.karumi.shot

import com.karumi.shot.android.Adb
import com.karumi.shot.domain.Config
import com.karumi.shot.domain.model.AppId
import com.karumi.shot.mothers.{AppIdMother, BuildTypeMother, ProjectNameMother}
import com.karumi.shot.reports.{ConsoleReporter, ExecutionReporter}
import com.karumi.shot.screenshots.{
  ScreenshotsComparator,
  ScreenshotsDiffGenerator,
  ScreenshotsSaver
}
import com.karumi.shot.system.EnvVars
import com.karumi.shot.ui.Console
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec._
import org.scalatest.matchers._
import java.io.File
import java.util

class ShotSpec
    extends AnyFlatSpec
    with should.Matchers
    with BeforeAndAfter
    with MockFactory
    with Resources {

  private var shot: Shot               = _
  private val adb                      = mock[Adb]
  private val files                    = mock[Files]
  private val console                  = mock[Console]
  private val screenshotsComparator    = mock[ScreenshotsComparator]
  private val screenshotsDiffGenerator = mock[ScreenshotsDiffGenerator]
  private val screenshotsSaver         = mock[ScreenshotsSaver]
  private val reporter                 = mock[ExecutionReporter]
  private val consoleReporter          = mock[ConsoleReporter]
  private val envVars                  = mock[EnvVars]

  before {
    shot = new Shot(adb,
                    files,
                    screenshotsComparator,
                    screenshotsDiffGenerator,
                    screenshotsSaver,
                    console,
                    reporter,
                    consoleReporter,
                    envVars)
  }

  "Shot" should "should delegate screenshots cleaning to Adb" in {
    val appId: AppId   = AppIdMother.anyAppId
    val device: String = "emulator-5554"
    (adb.devices _).expects().returns(List(device))
    (envVars.androidSerial _).expects().returns(None)

    (adb.clearScreenshots _).expects(device, appId)

    shot.removeScreenshots(appId)
  }

  it should "pull the screenshots using the project metadata folder and the app id" in {
    val appId         = AppIdMother.anyAppId
    val device        = "emulator-5554"
    val projectFolder = ProjectFolderMother.anyProjectFolder
    val expectedScreenshotsFolder = projectFolder + Config
      .screenshotsFolderName(BuildTypeMother.anyFlavor, BuildTypeMother.anyBuildType)
    val expectedOriginalMetadataFile = projectFolder + Config.metadataFileName(
      BuildTypeMother.anyFlavor,
      BuildTypeMother.anyBuildType)
    val expectedRenamedMetadataFile = projectFolder + Config.metadataFileName(
      BuildTypeMother.anyFlavor,
      BuildTypeMother.anyBuildType) + "_" + device
    val expectedComposeOriginalMetadataFile = projectFolder + Config
      .composeMetadataFileName(BuildTypeMother.anyFlavor, BuildTypeMother.anyBuildType)
    val expectedComposeRenamedMetadataFile = projectFolder + Config
      .composeMetadataFileName(BuildTypeMother.anyFlavor, BuildTypeMother.anyBuildType) + "_" + device
    (adb.devices _).expects().returns(List(device))
    (envVars.androidSerial _).expects().returns(None)

    (console.show _).expects(*)
    (adb.pullScreenshots _)
      .expects(device, expectedScreenshotsFolder, appId)
    (files.rename _)
      .expects(expectedOriginalMetadataFile, expectedRenamedMetadataFile)
      .once()
    (files.rename _)
      .expects(expectedComposeOriginalMetadataFile, expectedComposeRenamedMetadataFile)
      .once()

    shot.downloadScreenshots(projectFolder,
                             BuildTypeMother.anyFlavor,
                             BuildTypeMother.anyBuildType,
                             appId)
  }

  it should "configure adb path" in {
    val anyAdbPath = "/Library/androidsdk/bin/adb"

    shot.configureAdbPath(anyAdbPath)

    Adb.adbBinaryPath shouldBe anyAdbPath
  }

  it should "should delegate screenshots cleaning to Adb using the specified ANDROID_SERIAL env var" in {
    val appId: AppId    = AppIdMother.anyAppId
    val device1: String = "emulator-5554"
    val device2: String = "emulator-5556"
    (adb.devices _).expects().returns(List(device1, device2))
    (envVars.androidSerial _).expects().returns(Some(device2))

    (adb.clearScreenshots _).expects(device2, appId)

    shot.removeScreenshots(appId)
  }

  it should "pull the screenshots using the project metadata folder and the app id from the specified ANDROID_SERIAL env var" in {
    val appId         = AppIdMother.anyAppId
    val device1       = "emulator-5554"
    val device2       = "emulator-5556"
    val projectFolder = ProjectFolderMother.anyProjectFolder
    val expectedScreenshotsFolder = projectFolder + Config
      .screenshotsFolderName(BuildTypeMother.anyFlavor, BuildTypeMother.anyBuildType)
    val expectedOriginalMetadataFile = projectFolder + Config.metadataFileName(
      BuildTypeMother.anyFlavor,
      BuildTypeMother.anyBuildType)
    val expectedRenamedFile = projectFolder + Config.metadataFileName(
      BuildTypeMother.anyFlavor,
      BuildTypeMother.anyBuildType) + "_" + device2
    val expectedComposeOriginalMetadataFile = projectFolder + Config
      .composeMetadataFileName(BuildTypeMother.anyFlavor, BuildTypeMother.anyBuildType)
    val expectedComposeRenamedFile = projectFolder + Config
      .composeMetadataFileName(BuildTypeMother.anyFlavor, BuildTypeMother.anyBuildType) + "_" + device2
    (adb.devices _).expects().returns(List(device1, device2))
    (envVars.androidSerial _).expects().returns(Some(device2))

    (console.show _).expects(*)
    (adb.pullScreenshots _)
      .expects(device2, expectedScreenshotsFolder, appId)
    (files.rename _).expects(expectedOriginalMetadataFile, expectedRenamedFile)
    (files.rename _)
      .expects(expectedComposeOriginalMetadataFile, expectedComposeRenamedFile)

    shot.downloadScreenshots(projectFolder,
                             BuildTypeMother.anyFlavor,
                             BuildTypeMother.anyBuildType,
                             appId)
  }

  it should "should delegate screenshots cleaning to Adb using the devices if ANDROID_SERIAL env var is not valid" in {
    val appId: AppId    = AppIdMother.anyAppId
    val device1: String = "emulator-5554"
    val device2: String = "emulator-5556"
    (adb.devices _).expects().returns(List(device1, device2))
    (envVars.androidSerial _).expects().returns(Some("another emulator"))

    (adb.clearScreenshots _).expects(device1, appId)
    (adb.clearScreenshots _).expects(device2, appId)

    shot.removeScreenshots(appId)
  }

  it should "show a warning message if we couldn't find the compose screenshots' metadata during the verification proces" in {
    val appId         = AppIdMother.anyAppId
    val projectName   = ProjectNameMother.anyProjectName
    val buildFolder   = ProjectFolderMother.anyBuildFolder
    val projectFolder = ProjectFolderMother.anyProjectFolder
    val flavor        = BuildTypeMother.anyFlavor
    val buildType     = BuildTypeMother.anyBuildType
    (files.listFilesInFolder _)
      .expects(*)
      .returns(new util.LinkedList[File]())
    (console.show _).expects(*)
    (console.showWarning _).expects(
      "🤔 We couldn't find any screenshot. Did you configure Shot properly and added your tests to your project? https://github.com/Karumi/Shot/#getting-started")

    shot.verifyScreenshots(appId,
                           buildFolder,
                           projectFolder,
                           flavor,
                           buildType,
                           projectName,
                           shouldPrintBase64Error = false,
                           0d,
                           showOnlyFailingTestsInReports = false)
  }

  it should "show a warning message if we couldn't find the compose screenshots' metadata during the record process" in {
    val appId         = AppIdMother.anyAppId
    val projectName   = ProjectNameMother.anyProjectName
    val buildFolder   = ProjectFolderMother.anyBuildFolder
    val projectFolder = ProjectFolderMother.anyProjectFolder
    val flavor        = BuildTypeMother.anyFlavor
    val buildType     = BuildTypeMother.anyBuildType
    (files.listFilesInFolder _)
      .expects(*)
      .returns(new util.LinkedList[File]())
    (console.show _).expects(*)
    (console.showWarning _).expects(
      "🤔 We couldn't find any screenshot. Did you configure Shot properly and added your tests to your project? https://github.com/Karumi/Shot/#getting-started")

    shot.recordScreenshots(appId, buildFolder, projectFolder, projectName, flavor, buildType)
  }
}
