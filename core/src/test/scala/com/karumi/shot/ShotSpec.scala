package com.karumi.shot

import com.karumi.shot.android.Adb
import com.karumi.shot.domain.model.AppId
import com.karumi.shot.mothers.{AppIdMother, ProjectFolderMother, ProjectNameMother}
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
    shot = new Shot(
      adb,
      files,
      screenshotsComparator,
      screenshotsDiffGenerator,
      screenshotsSaver,
      console,
      reporter,
      consoleReporter,
      envVars
    )
  }

  "Shot" should "should delegate screenshots cleaning to Adb" in {
    val appId: AppId          = AppIdMother.anyAppId
    val device: String        = "emulator-5554"
    val orchestrated: Boolean = false

    (adb.devices _).expects().returns(List(device))
    (envVars.androidSerial _).expects().returns(None)

    (adb.clearScreenshots _).expects(device, appId, orchestrated)

    shot.removeScreenshots(appId, orchestrated)
  }

  it should "pull the screenshots using the project metadata folder and the app id" in {
    val appId                 = AppIdMother.anyAppId
    val device                = "emulator-5554"
    val orchestrated: Boolean = false

    val listOfMetadataFiles = new util.LinkedList[File]()
    listOfMetadataFiles.add(
      new File(
        "/User/pedro/projects/KarumiApp/app/screenshots/green/debug/Api26/screenshots-default/metadata.json"
      )
    )

    val listOfMetadataComposeFiles = new util.LinkedList[File]()
    listOfMetadataComposeFiles.add(
      new File(
        "/User/pedro/projects/KarumiApp/app/screenshots/green/debug/Api26/screenshots-compose-default/metadata_compose.json"
      )
    )

    (adb.devices _).expects().returns(List(device))
    (envVars.androidSerial _).expects().returns(None)

    (console.show _).expects(*)
    (adb.pullScreenshots _)
      .expects(
        device,
        "/User/pedro/projects/KarumiApp/app/screenshots/green/debug/Api26/",
        appId,
        orchestrated
      )
    (files.rename _)
      .expects(
        "/User/pedro/projects/KarumiApp/app/screenshots/green/debug/Api26/screenshots-default/metadata.json",
        "/User/pedro/projects/KarumiApp/app/screenshots/green/debug/Api26/screenshots-default/metadata.json_emulator-5554"
      )
      .once()
    (files.rename _)
      .expects(
        "/User/pedro/projects/KarumiApp/app/screenshots/green/debug/Api26/screenshots-compose-default/metadata_compose.json",
        "/User/pedro/projects/KarumiApp/app/screenshots/green/debug/Api26/screenshots-compose-default/metadata_compose.json_emulator-5554"
      )
      .once()

    (files.listFilesInFolder _)
      .expects(
        "/User/pedro/projects/KarumiApp/app/screenshots/green/debug/Api26/screenshots-default/"
      )
      .returns(listOfMetadataFiles)
      .once()

    (files.listFilesInFolder _)
      .expects(
        "/User/pedro/projects/KarumiApp/app/screenshots/green/debug/Api26/screenshots-compose-default/"
      )
      .returns(listOfMetadataComposeFiles)
      .once()

    shot.downloadScreenshots(
      appId,
      ProjectFolderMother.anyShotFolder,
      orchestrated
    )
  }

  it should "should delegate screenshots cleaning to Adb using the specified ANDROID_SERIAL env var" in {
    val appId: AppId          = AppIdMother.anyAppId
    val device1: String       = "emulator-5554"
    val device2: String       = "emulator-5556"
    val orchestrated: Boolean = false

    (adb.devices _).expects().returns(List(device1, device2))
    (envVars.androidSerial _).expects().returns(Some(device2))

    (adb.clearScreenshots _).expects(device2, appId, orchestrated)

    shot.removeScreenshots(appId, orchestrated)
  }

  it should "pull the screenshots using the project metadata folder and the app id from the specified ANDROID_SERIAL env var" in {
    val appId                 = AppIdMother.anyAppId
    val device1               = "emulator-5554"
    val device2               = "emulator-5556"
    val orchestrated: Boolean = false

    val listOfMetadataFiles = new util.LinkedList[File]()
    listOfMetadataFiles.add(
      new File(
        "/User/pedro/projects/KarumiApp/app/screenshots/green/debug/Api26/screenshots-default/metadata.json"
      )
    )

    val listOfMetadataComposeFiles = new util.LinkedList[File]()
    listOfMetadataComposeFiles.add(
      new File(
        "/User/pedro/projects/KarumiApp/app/screenshots/green/debug/Api26/screenshots-compose-default/metadata_compose.json"
      )
    )

    (adb.devices _).expects().returns(List(device1, device2))
    (envVars.androidSerial _).expects().returns(Some(device2))

    (console.show _).expects(*)
    (adb.pullScreenshots _)
      .expects(
        device2,
        "/User/pedro/projects/KarumiApp/app/screenshots/green/debug/Api26/",
        appId,
        orchestrated
      )
    (files.rename _).expects(
      "/User/pedro/projects/KarumiApp/app/screenshots/green/debug/Api26/screenshots-default/metadata.json",
      "/User/pedro/projects/KarumiApp/app/screenshots/green/debug/Api26/screenshots-default/metadata.json_emulator-5556"
    )
    (files.rename _)
      .expects(
        "/User/pedro/projects/KarumiApp/app/screenshots/green/debug/Api26/screenshots-compose-default/metadata_compose.json",
        "/User/pedro/projects/KarumiApp/app/screenshots/green/debug/Api26/screenshots-compose-default/metadata_compose.json_emulator-5556"
      )

    (files.listFilesInFolder _)
      .expects(
        "/User/pedro/projects/KarumiApp/app/screenshots/green/debug/Api26/screenshots-default/"
      )
      .returns(listOfMetadataFiles)
      .once()

    (files.listFilesInFolder _)
      .expects(
        "/User/pedro/projects/KarumiApp/app/screenshots/green/debug/Api26/screenshots-compose-default/"
      )
      .returns(listOfMetadataComposeFiles)
      .once()

    shot.downloadScreenshots(
      appId,
      ProjectFolderMother.anyShotFolder,
      orchestrated
    )
  }

  it should "should delegate screenshots cleaning to Adb using the devices if ANDROID_SERIAL env var is not valid" in {
    val appId: AppId          = AppIdMother.anyAppId
    val device1: String       = "emulator-5554"
    val device2: String       = "emulator-5556"
    val orchestrated: Boolean = false

    (adb.devices _).expects().returns(List(device1, device2))
    (envVars.androidSerial _).expects().returns(Some("another emulator"))

    (adb.clearScreenshots _).expects(device1, appId, orchestrated)
    (adb.clearScreenshots _).expects(device2, appId, orchestrated)

    shot.removeScreenshots(appId, orchestrated)
  }

  it should "show a warning message if we couldn't find the compose screenshots' metadata during the verification proces" in {
    val appId = AppIdMother.anyAppId

    (files.listFilesInFolder _)
      .expects(*)
      .returns(new util.LinkedList[File]())
    (console.show _).expects(*)
    (console.showWarning _).expects(
      "🤔 We couldn't find any screenshot. Did you configure Shot properly and added your tests to your project? https://github.com/pedrovgs/Shot/#getting-started"
    )

    shot.verifyScreenshots(
      appId,
      ProjectFolderMother.anyShotFolder,
      ProjectNameMother.anyProjectName,
      shouldPrintBase64Error = false,
      0d,
      showOnlyFailingTestsInReports = false,
      orchestrated = false
    )
  }

  it should "show a warning message if we couldn't find the compose screenshots' metadata during the record process" in {
    val appId = AppIdMother.anyAppId

    (files.listFilesInFolder _)
      .expects(*)
      .returns(new util.LinkedList[File]())
    (console.show _).expects(*)
    (console.showWarning _).expects(
      "🤔 We couldn't find any screenshot. Did you configure Shot properly and added your tests to your project? https://github.com/pedrovgs/Shot/#getting-started"
    )

    shot.recordScreenshots(appId, ProjectFolderMother.anyShotFolder, orchestrated = false)
  }
}
