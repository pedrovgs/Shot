package com.karumi.shot.json

import com.karumi.shot.domain.{Dimension, Screenshot}
import com.karumi.shot.domain.model.{Folder, ScreenshotsSuite}
import org.json4s._
import org.json4s.native.JsonMethods._

object ScreenshotsComposeSuiteJsonParser {

  def parseScreenshot(jsonNode: JsonAST.JValue): ComposeScreenshot = {
    val JString(name)          = jsonNode \ "name"
    val JString(testClassName) = jsonNode \ "testClassName"
    val JString(testName)      = jsonNode \ "testName"

    ComposeScreenshot(name, testClassName, testName)
  }

  def parseScreenshots(
      metadataJson: String,
      screenshotsFolder: Folder,
      temporalScreenshotsFolder: Folder,
      screenshotsTemporalBuildPath: Folder
  ): ScreenshotsSuite = {
    implicit val formats: DefaultFormats.type = DefaultFormats

    val json                            = parse(metadataJson)
    val JObject(composeScreenshotSuite) = json
    val JArray(composeScreenshots)      = json \ "screenshots"

    composeScreenshots.map(parseScreenshot).map { screenshot =>
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
}

case class ComposeScreenshot(name: String, testClassName: String, testName: String)
case class ComposeScreenshotSuite(screenshots: List[ComposeScreenshot])
