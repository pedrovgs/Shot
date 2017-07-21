package com.karumi.shot.xml

import com.karumi.shot.domain.Screenshot
import com.karumi.shot.domain.model.ScreenshotsSuite

import scala.xml._

class ScreenshotsSuiteXmlParser {

  def parseScreenshots(xml: String): ScreenshotsSuite = {
    val xmlScreenshots = XML.loadString(xml) \ "screenshot"
    xmlScreenshots.map(parseScreenshot)
  }

  private def parseScreenshot(xmlNode: Node): Screenshot = {
    val name = (xmlNode \ "name" head).text
    val testClass = (xmlNode \ "test_class" head).text
    val testName = (xmlNode \ "test_name" head).text
    val tileWidth = (xmlNode \ "tile_width" head).text.toInt
    val tileHeight = (xmlNode \ "tile_height" head).text.toInt
    val viewHierarchy = (xmlNode \ "view_hierarchy" head).text
    val absoluteFileNames = (xmlNode \ "absolute_file_name").map(_.text)
    val relativeFileNames = (xmlNode \ "relative_file_name").map(_.text)
    Screenshot(name,
               testClass,
               testName,
               tileWidth,
               tileHeight,
               viewHierarchy,
               absoluteFileNames,
               relativeFileNames)
  }

}
