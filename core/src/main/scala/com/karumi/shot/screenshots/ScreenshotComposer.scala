package com.karumi.shot.screenshots

import java.io.File

import com.karumi.shot.domain.Screenshot
import com.sksamuel.scrimage.{AwtImage, Color, Image}

object ScreenshotComposer {

  private val tileSize = 512

  private[screenshots] def composeNewScreenshot(
      screenshot: Screenshot): Image = {
    val width = screenshot.screenshotDimension.width
    val height = screenshot.screenshotDimension.height
    var composedImage = Image.filled(width, height, Color.Transparent)
    var partIndex = 0
    for (x <- 0 until screenshot.tilesDimension.width;
         y <- 0 until screenshot.tilesDimension.height) {
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
}
