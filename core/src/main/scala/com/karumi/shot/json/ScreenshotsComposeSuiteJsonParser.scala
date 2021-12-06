package com.karumi.shot.json

import com.karumi.shot.domain.{Dimension, Screenshot}
import com.karumi.shot.domain.model.{Folder, ScreenshotsSuite}
import org.json4s._
import org.json4s.jackson.JsonMethods._

object ScreenshotsComposeSuiteJsonParser {
  def parseScreenshots(
      json: String,
      screenshotsFolder: Folder,
      temporalScreenshotsFolder: Folder,
      screenshotsTemporalBuildPath: Folder
  ): ScreenshotsSuite = {
    implicit val formats: DefaultFormats.type = DefaultFormats
    val composeSuite                          = parse(json).extract[ComposeScreenshotSuite]
    composeSuite.screenshots.map { screenshot =>
      val name = screenshot.name
      Screenshot(
        name = name,
        recordedScreenshotPath = screenshotsFolder + name + ".png",
        temporalScreenshotPath =
          screenshotsTemporalBuildPath + "/" + name + ".png",
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
}

case class ComposeScreenshot(name: String, testClassName: String, testName: String)
case class ComposeScreenshotSuite(screenshots: List[ComposeScreenshot])
