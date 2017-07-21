package com.karumi.shot.screenshots

import java.io.File

import com.karumi.shot.domain.model.ScreenshotsSuite
import com.karumi.shot.domain.{Screenshot, ScreenshotComparisionError, ScreenshotsComparisionResult}
import com.sksamuel.scrimage.{AwtImage, Color, Image}

class ScreenshotsComparator {

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
    newScreenshot.output(new File("/Users/Pedro/Desktop/newScreenshot.png"))
    None //TODO: Fix this
  }

  private def composeNewScreenshot(screenshot: Screenshot): Image = {
    var composedImage = Image.filled(512 * screenshot.tileWidth, 512 * screenshot.tileHeight, Color.Transparent)
    var imageIndex = 0
    for (x <- 0 until screenshot.tileWidth; y <- 0 until screenshot.tileHeight) {
      val partFile = new File(screenshot.recordedPartsPaths(imageIndex))
      val part = Image.fromFile(partFile).awt
      composedImage = composedImage.overlay(new AwtImage(part), x * 512, y * 512)
      imageIndex += 1
    }
    composedImage
  }

}
