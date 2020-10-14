package com.karumi.shot.screenshots

import java.io.File

import com.karumi.shot.domain._
import com.karumi.shot.domain.model.ScreenshotsSuite
import com.sksamuel.scrimage.Image

class ScreenshotsComparator {

  def compare(screenshots: ScreenshotsSuite,
              tolerance: Int): ScreenshotsComparisionResult = {
    val errors =
      screenshots.par.flatMap(compareScreenshot(_, tolerance)).toList
    ScreenshotsComparisionResult(errors, screenshots)
  }

  private def compareScreenshot(
      screenshot: Screenshot,
      tolerance: Int): Option[ScreenshotComparisionError] = {
    val recordedScreenshotFile = new File(screenshot.recordedScreenshotPath)
    if (!recordedScreenshotFile.exists()) {
      Some(ScreenshotNotFound(screenshot))
    } else {
      val oldScreenshot =
        Image.fromFile(recordedScreenshotFile)
      val newScreenshot = ScreenshotComposer.composeNewScreenshot(screenshot)
      if (!haveSameDimensions(newScreenshot, oldScreenshot)) {
        val originalDimension =
          Dimension(oldScreenshot.width, oldScreenshot.height)
        val newDimension = Dimension(newScreenshot.width, newScreenshot.height)
        Some(
          DifferentImageDimensions(screenshot,
                                   originalDimension,
                                   newDimension))
      } else if (imagesAreDifferent(screenshot,
                                    oldScreenshot,
                                    newScreenshot,
                                    tolerance)) {
        Some(DifferentScreenshots(screenshot))
      } else {
        None
      }
    }
  }

  private def imagesAreDifferent(screenshot: Screenshot,
                                 oldScreenshot: Image,
                                 newScreenshot: Image,
                                 tolerance: Int) = {
    val oldScreenshotPixels = oldScreenshot.pixels
    val newScreenshotPixels = newScreenshot.pixels
    val differentPixels = oldScreenshotPixels.diff(newScreenshotPixels).length
    val percentageOfDifferentPixels = differentPixels.toFloat / oldScreenshotPixels.length.toFloat
    val imagesAreDifferent = percentageOfDifferentPixels * 100 > tolerance
    if (tolerance != 100 && imagesAreDifferent) {
      val screenshotName = screenshot.name
      println(
        Console.YELLOW + s"Shot warning: There are some pixels changed in the screenshot named $screenshotName, but we consider the comparison correct because tolerance is configured to $tolerance and the percentage of different pixels is $percentageOfDifferentPixels" + Console.RESET)
    }
    imagesAreDifferent
  }

  private def haveSameDimensions(newScreenshot: Image,
                                 recordedScreenshot: Image): Boolean =
    newScreenshot.width == recordedScreenshot.width && newScreenshot.height == recordedScreenshot.height

}
