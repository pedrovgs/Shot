package com.karumi.shot

import com.karumi.shot.android.Adb
import com.karumi.shot.domain.Config
import com.karumi.shot.mothers.AppIdMother
import com.karumi.shot.ui.View
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

object ShotSpec {
  private val appIdConfigError =
    "Error found executing screenshot tests. The appId param is not configured properly. You should configure the appId following the plugin instructions you can find at https://github.com/karumi/shot"
}
class ShotSpec
    extends FlatSpec
    with Matchers
    with BeforeAndAfter
    with MockFactory {

  import ShotSpec._

  private var shot: Shot = _
  private val adb = mock[Adb]
  private val view = mock[View]

  before {
    shot = new Shot(adb, view)
  }

  "Shot" should "should delegate screenshots cleaning to Adb" in {
    val appId = AppIdMother.anyAppId

    (adb.clearScreenshots _).expects(appId.get)

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

  it should "configure adb path" in {
    val anyAdbPath = "/Library/androidsdk/bin/adb"

    shot.configureAdbPath(anyAdbPath)

    Adb.adbBinaryPath shouldBe anyAdbPath
  }

  it should "show an error if the app ID is not properly configured when cleaning screenshots" in {
    val appId = AppIdMother.anyInvalidAppId

    (view.showError _).expects(appIdConfigError)

    shot.clearScreenshots(appId)
  }

  it should "show an error if the app ID is not properly configured when pulling screenshots" in {
    val appId = AppIdMother.anyInvalidAppId
    val projectFolder = ProjectFolderMother.anyProjectFolder

    (view.showError _).expects(appIdConfigError)

    shot.pullScreenshots(projectFolder, appId)
  }
}
