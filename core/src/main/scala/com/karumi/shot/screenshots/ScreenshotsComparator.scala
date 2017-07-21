package com.karumi.shot.screenshots

import java.io.File

import com.karumi.shot.domain._
import com.karumi.shot.domain.model.ScreenshotsSuite
import com.sksamuel.scrimage.{AwtImage, Color, Image}

object ScreenshotsComparator {
  private val tileSize = 512
}

class ScreenshotsComparator {

  import ScreenshotsComparator._

  def compare(screenshots: ScreenshotsSuite): ScreenshotsComparisionResult = {
    val errors = screenshots.flatMap(compareScreenshot)
    ScreenshotsComparisionResult(errors, screenshots)
  }

  private def compareScreenshot(screenshot: Screenshot): Option[ScreenshotComparisionError] = {
    val oldScreenshot = Image.fromFile(new File(screenshot.recordedScreenshotPath))
    val newScreenshot = composeNewScreenshot(screenshot)
    if (!haveSimilarDimensions(newScreenshot, oldScreenshot)) {
      val originalDimension = Dimension(oldScreenshot.width, oldScreenshot.height)
      val newDimension = Dimension(newScreenshot.width, newScreenshot.height)
      Some(DifferentImageDimensions(screenshot, originalDimension, newDimension))
    } else if (newScreenshot != oldScreenshot) {
      Some(DifferentScreenshots(screenshot))
    } else {
      None
    }
  }

  private def composeNewScreenshot(screenshot: Screenshot): Image = {
    val width = screenshot.screenshotWidth
    val height = screenshot.screenshotHeight
    var composedImage = Image.filled(width,
      height,
      Color.Transparent)
    var partIndex = 0
    for (x <- 0 until screenshot.tileWidth; y <- 0 until screenshot.tileHeight) {
      val partFile = new File(screenshot.recordedPartsPaths(partIndex))
      val part = Image.fromFile(partFile).awt
      val xPosition = x * tileSize
      val yPosition = y * tileSize
      composedImage =
        composedImage.overlay(new AwtImage(part), xPosition, yPosition)
      partIndex += 1
    }
    composedImage
  }

  private def haveSimilarDimensions(newScreenshot: Image, recordedScreenshot: Image): Boolean =
    newScreenshot.width == recordedScreenshot.width && newScreenshot.height == recordedScreenshot.height

}