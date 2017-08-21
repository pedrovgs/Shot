package com.karumi.shot.screenshots

import java.io.File

import com.karumi.shot.domain._
import com.karumi.shot.domain.model.ScreenshotsSuite
import com.sksamuel.scrimage.Image

class ScreenshotsComparator {

  def compare(screenshots: ScreenshotsSuite): ScreenshotsComparisionResult = {
    val errors = screenshots.par.flatMap(compareScreenshot).toList
    ScreenshotsComparisionResult(errors, screenshots)
  }

  private def compareScreenshot(
                                 screenshot: Screenshot): Option[ScreenshotComparisionError] = {
    val oldScreenshot =
      Image.fromFile(new File(screenshot.recordedScreenshotPath))
    val newScreenshot = ScreenshotComposer.composeNewScreenshot(screenshot)
    if (!haveSameDimensions(newScreenshot, oldScreenshot)) {
      val originalDimension =
        Dimension(oldScreenshot.width, oldScreenshot.height)
      val newDimension = Dimension(newScreenshot.width, newScreenshot.height)
      Some(
        DifferentImageDimensions(screenshot, originalDimension, newDimension))
    } else if (newScreenshot != oldScreenshot) {
      Some(DifferentScreenshots(screenshot))
    } else {
      None
    }
  }

  private def haveSameDimensions(newScreenshot: Image,
                                 recordedScreenshot: Image): Boolean =
    newScreenshot.width == recordedScreenshot.width && newScreenshot.height == recordedScreenshot.height

}
