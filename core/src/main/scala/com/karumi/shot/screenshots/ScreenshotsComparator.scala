package com.karumi.shot.screenshots

import java.io.File

import com.karumi.shot.domain.model.ScreenshotsSuite
import com.karumi.shot.domain.{Screenshot, ScreenshotComparisionError, ScreenshotsComparisionResult}
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
    val recordedScreenshot =
      Image.fromFile(new File(screenshot.recordedScreenshotPath))
    recordedScreenshot.output(
      new File("/Users/Pedro/Desktop/imageRecorded.png"))
    val newScreenshot = composeNewScreenshot(screenshot)
    newScreenshot.output(new File("/Users/Pedro/Desktop/screenshots/" + screenshot.name + ".png"))
    None //TODO: Fix this
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
    //composedImage.resizeTo(screenshot.screenshotWidth, screenshot.screenshotHeight)
    composedImage
  }

}
