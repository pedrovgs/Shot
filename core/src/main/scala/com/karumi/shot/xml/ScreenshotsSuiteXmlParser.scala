package com.karumi.shot.xml

import java.io.File

import com.karumi.shot.domain.{Dimension, Screenshot}
import com.karumi.shot.domain.model.{Folder, ScreenshotsSuite}

import scala.xml._

object ScreenshotsSuiteXmlParser {

  def parseScreenshots(xml: String,
                       screenshotsFolder: Folder,
                       temporalScreenshotsFolder: Folder): ScreenshotsSuite = {
    val xmlScreenshots = XML.loadString(xml) \ "screenshot"
    xmlScreenshots.map(
      parseScreenshot(_, screenshotsFolder, temporalScreenshotsFolder))
  }

  private def parseScreenshot(
      xmlNode: Node,
      screenshotsFolder: Folder,
      temporalScreenshotsFolder: Folder): Screenshot = {
    val name = (xmlNode \ "name" head).text
    val recordedScreenshotPath = screenshotsFolder + name + ".png"
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
      testClass,
      testName,
      tilesDimension,
      viewHierarchy,
      absoluteFileNames,
      relativeFileNames,
      recordedPartsPaths,
      0,
      0
    )
  }

  def parseScreenshotSize(screenshot: Screenshot,
                          viewHierarchyContent: String): Screenshot = {
    val xml = XML.loadString(viewHierarchyContent)
    val screenshotWidth = (xml \ "right" head).text.toInt
    val screenshotHeight = (xml \ "bottom" head).text.toInt
    screenshot.copy(screenshotWidth = screenshotWidth,
                    screenshotHeight = screenshotHeight)
  }

}
