package com.karumi.shot.xml

import com.karumi.shot.domain.model.{FilePath, Folder, ScreenshotsSuite}
import com.karumi.shot.domain.{Dimension, Screenshot}
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object ScreenshotsSuiteJsonParser {

  def parseScreenshots(
      metadataJson: String,
      screenshotsFolder: Folder,
      temporalScreenshotsFolder: Folder,
      screenshotsTemporalBuildPath: Folder
  ): ScreenshotsSuite = {
    val json                    = parse(metadataJson)
    val JArray(jsonScreenshots) = json
    jsonScreenshots.map(
      parseScreenshot(_, screenshotsFolder, temporalScreenshotsFolder, screenshotsTemporalBuildPath)
    )
  }

  private def parseScreenshot(
      jsonNode: JValue,
      screenshotsFolder: Folder,
      temporalScreenshotsFolder: Folder,
      screenshotsTemporalBuildPath: Folder
  ): Screenshot = {
    val JString(name)          = jsonNode \ "name"
    val recordedScreenshotPath = screenshotsFolder + name + ".png"
    val temporalScreenshotPath =
      screenshotsTemporalBuildPath + "/" + name + ".png"
    val JString(testClass)                = (jsonNode \ "testClass")
    val JString(testName)                 = (jsonNode \ "testName")
    val JInt(tileWidth)                   = (jsonNode \ "tileWidth")
    val JInt(tileHeight)                  = (jsonNode \ "tileHeight")
    val tilesDimension                    = Dimension(tileWidth.toInt, tileHeight.toInt)
    val JString(viewHierarchy)            = (jsonNode \ "viewHierarchy")
    val JArray(absoluteFileNamesFromJson) = (jsonNode \ "absoluteFilesNames")
    val absoluteFileNames                 = ListBuffer[String]()
    absoluteFileNamesFromJson.foreach(value => {
      val JString(fileName) = value
      absoluteFileNames += (fileName + ".png")
    })
    val JArray(relativeFileNamesFromJson) =
      (jsonNode \ "relativeFileNames")

    val relativeFileNames = ListBuffer[String]()
    relativeFileNamesFromJson.foreach(value => {
      val JString(fileName) = value
      relativeFileNames += (fileName + ".png")
    })

    implicit val formats = DefaultFormats

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
      relativeFileNames.map(temporalScreenshotsFolder + _),
      Dimension(0, 0)
    )
  }

  def parseScreenshotSize(screenshot: Screenshot, viewHierarchyContent: String): Screenshot = {
    val json                   = parse(viewHierarchyContent)
    val viewHierarchyNode      = json \ "viewHierarchy"
    val JInt(screenshotLeft)   = viewHierarchyNode \ "left"
    val JInt(screenshotWidth)  = viewHierarchyNode \ "width"
    val JInt(screenshotTop)    = viewHierarchyNode \ "top"
    val JInt(screenshotHeight) = viewHierarchyNode \ "height"
    screenshot.copy(
      screenshotDimension = Dimension(
        screenshotLeft.toInt + screenshotWidth.toInt,
        screenshotTop.toInt + screenshotHeight.toInt
      )
    )
  }

}
