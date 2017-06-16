package com.karumi.shot

import com.karumi.shot.android.Adb
import com.karumi.shot.domain.Config
import com.karumi.shot.mothers.AppIdMother
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class ShotSpec
    extends FlatSpec
    with Matchers
    with BeforeAndAfter
    with MockFactory {

  private var shot: Shot = _
  private val adb = mock[Adb]

  before {
    shot = new Shot(adb)
  }

  "Shot" should "should delegate screenshots cleaning to Adb" in {
    val appId = AppIdMother.anyAppId

    (adb.clearScreenshots _).expects(appId.get)

    shot.clearScreenshots(appId)
  }

  it should "not clear screenshots if the appId is None" in {
    val appId = AppIdMother.anyInvalidAppId

    (adb.clearScreenshots _).expects(*).never

    shot.clearScreenshots(appId)
  }

  it should "pull the screenshots using the project folder and the app id if" in {
    val appId = AppIdMother.anyAppId
    val projectFolder = ProjectFolderMother.anyProjectFolder
    val expectedScreenshotsFolder = projectFolder + Config
      .screenshotsFolderName

    (adb.pullScreenshots _).expects(expectedScreenshotsFolder, appId.get)

    shot.pullScreenshots(projectFolder, appId)
  }

  it should "not pull the screenshots using if the appId is not defined" in {
    val appId = AppIdMother.anyInvalidAppId
    val projectFolder = ProjectFolderMother.anyProjectFolder

    (adb.pullScreenshots _).expects(*, *).never

    shot.pullScreenshots(projectFolder, appId)
  }

  it should "configure adb path" in {
    val anyAdbPath = "/Library/androidsdk/bin/adb"

    shot.configureAdbPath(anyAdbPath)

    Adb.adbBinaryPath shouldBe anyAdbPath
  }
}
