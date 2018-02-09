package com.karumi.shot.xml

import com.karumi.shot.domain.model.{Folder, ScreenshotsSuite}
import com.karumi.shot.domain.{Config, Dimension, Screenshot}
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.xml._

object ScreenshotsSuiteXmlParser {

  def parseScreenshots(xml: String,
                       projectName: String,
                       screenshotsFolder: Folder,
                       temporalScreenshotsFolder: Folder): ScreenshotsSuite = {
    val xmlScreenshots = XML.loadString(xml) \ "screenshot"
    xmlScreenshots.map(
      parseScreenshot(_,
                      projectName,
                      screenshotsFolder,
                      temporalScreenshotsFolder))
  }

  private def parseScreenshot(
      xmlNode: Node,
      projectName: String,
      screenshotsFolder: Folder,
      temporalScreenshotsFolder: Folder): Screenshot = {
    val name = (xmlNode \ "name" head).text
    val recordedScreenshotPath = screenshotsFolder + name + ".png"
    val temporalScreenshotPath = Config.screenshotsTemporalRootPath + projectName + "/" + name + ".png"
    val testClass = (xmlNode \ "test_class" head).text
    val testName = (xmlNode \ "test_name" head).text
    val tileWidth = (xmlNode \ "tile_width" head).text.toInt
    val tileHeight = (xmlNode \ "tile_height" head).text.toInt
    val tilesDimension = Dimension(tileWidth, tileHeight)
    val viewHierarchy = (xmlNode \ "view_hierarchy" head).text
    val absoluteFileNames = (xmlNode \ "absolute_file_name").map(_.text)
    val relativeFileNames = (xmlNode \ "relative_file_name").map(_.text)
    val recordedPartsPaths =
      relativeFileNames.map(temporalScreenshotsFolder + _)
    Screenshot(
      name,
      recordedScreenshotPath,
      temporalScreenshotPath,
      testClass,
      testName,
      tilesDimension,
      viewHierarchy,
      absoluteFileNames,
      relativeFileNames,
      recordedPartsPaths,
      Dimension(0, 0)
    )
  }

  def parseScreenshotSize(screenshot: Screenshot,
                          viewHierarchyContent: String): Screenshot = {
    val json = parse(viewHierarchyContent)
    val JInt(screenshotLeft) = json \ "left"
    val JInt(screenshotWidth) = json \ "width"
    val JInt(screenshotTop) = json \ "top"
    val JInt(screenshotHeight) = json \ "height"
    screenshot.copy(
      screenshotDimension =
        Dimension(screenshotLeft.toInt + screenshotWidth.toInt,
                  screenshotTop.toInt + screenshotHeight.toInt))
  }

}
