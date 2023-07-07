package com.karumi.shot.screenshots

import java.io.File

import com.karumi.shot.domain.Screenshot
import com.sksamuel.scrimage.color.Colors
import com.sksamuel.scrimage.{AwtImage, ImmutableImage}

object ScreenshotComposer {

  private val tileSize = 512

  private[screenshots] def composeNewScreenshot(screenshot: Screenshot): ImmutableImage = {
    val width  = screenshot.screenshotDimension.width
    val height = screenshot.screenshotDimension.height
    if (screenshot.recordedPartsPaths.size == 1) {
      ImmutableImage.loader().fromFile(new File(screenshot.recordedPartsPaths.head))
    } else {
      var composedImage = ImmutableImage.filled(width, height, Colors.Transparent.awt())
      var partIndex     = 0
      for (
        x <- 0 until screenshot.tilesDimension.width;
        y <- 0 until screenshot.tilesDimension.height
      ) {
        val partFile  = new File(screenshot.recordedPartsPaths(partIndex))
        val part      = ImmutableImage.loader().fromFile(partFile).awt
        val xPosition = x * tileSize
        val yPosition = y * tileSize
        composedImage = composedImage.overlay(new AwtImage(part), xPosition, yPosition)
        partIndex += 1
      }
      composedImage
    }
  }
}
