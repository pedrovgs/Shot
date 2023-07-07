package com.karumi.shot.json

import com.karumi.shot.domain.{Dimension, Screenshot}
import com.karumi.shot.domain.model.{Folder, ScreenshotsSuite}
import org.json4s._
import org.json4s.native.JsonMethods._

object ScreenshotsComposeSuiteJsonParser {

  def parseScreenshotSuite(
      metadataJson: String,
      screenshotsFolder: Folder,
      temporalScreenshotsFolder: Folder,
      screenshotsTemporalBuildPath: Folder
  ): ScreenshotsSuite = {
    implicit val formats: DefaultFormats.type = DefaultFormats

    val json = parse(metadataJson)

    parseScreenshots(
      json,
      screenshotsFolder,
      temporalScreenshotsFolder,
      screenshotsTemporalBuildPath
    )
  }

  private def parseScreenshots(
      json: JValue,
      screenshotsFolder: Folder,
      temporalScreenshotsFolder: Folder,
      screenshotsTemporalBuildPath: Folder
  ): ScreenshotsSuite = {
    val JArray(composeScreenshots) = json \ "screenshots"

    composeScreenshots
      .map(parseScreenshot)
      .map(
        mapComposeScreenshot(
          _,
          screenshotsFolder,
          temporalScreenshotsFolder,
          screenshotsTemporalBuildPath
        )
      )
  }

  private def parseScreenshot(jsonNode: JsonAST.JValue): ComposeScreenshot = {
    val JString(name)          = jsonNode \ "name"
    val JString(testClassName) = jsonNode \ "testClassName"
    val JString(testName)      = jsonNode \ "testName"

    ComposeScreenshot(name, testClassName, testName)
  }

  private def mapComposeScreenshot(
      screenshot: ComposeScreenshot,
      screenshotsFolder: Folder,
      temporalScreenshotsFolder: Folder,
      screenshotsTemporalBuildPath: Folder
  ): Screenshot = {
    val name = screenshot.name
    Screenshot(
      name = name,
      recordedScreenshotPath = screenshotsFolder + name + ".png",
      temporalScreenshotPath = screenshotsTemporalBuildPath + "/" + name + ".png",
      testClass = screenshot.testClassName,
      testName = screenshot.testName,
      tilesDimension = Dimension(0, 0),
      viewHierarchy = "",
      absoluteFileNames = Seq(),
      relativeFileNames = Seq(),
      recordedPartsPaths = Seq(temporalScreenshotsFolder + "/" + name + ".png"),
      screenshotDimension = Dimension(0, 0)
    )
  }
}

case class ComposeScreenshot(name: String, testClassName: String, testName: String)
case class ComposeScreenshotSuite(screenshots: List[ComposeScreenshot])
