package com.karumi.shot.screenshots

import java.io.File

import com.karumi.shot.domain._
import com.karumi.shot.domain.model.ScreenshotsSuite
import com.sksamuel.scrimage.ImmutableImage

class ScreenshotsComparator {

  def compare(screenshots: ScreenshotsSuite, tolerance: Double): ScreenshotsComparisionResult = {
    val errors =
      screenshots.par.flatMap(compareScreenshot(_, tolerance)).toList
    ScreenshotsComparisionResult(errors, screenshots)
  }

  private def compareScreenshot(
      screenshot: Screenshot,
      tolerance: Double
  ): Option[ScreenshotComparisonError] = {
    val recordedScreenshotFile = new File(screenshot.recordedScreenshotPath)
    if (!recordedScreenshotFile.exists()) {
      Some(ScreenshotNotFound(screenshot))
    } else {
      val oldScreenshot =
        ImmutableImage.loader().fromFile(recordedScreenshotFile)
      val newScreenshot = ScreenshotComposer.composeNewScreenshot(screenshot)
      if (!haveSameDimensions(newScreenshot, oldScreenshot)) {
        val originalDimension =
          Dimension(oldScreenshot.width, oldScreenshot.height)
        val newDimension = Dimension(newScreenshot.width, newScreenshot.height)
        Some(DifferentImageDimensions(screenshot, originalDimension, newDimension))
      } else if (imagesAreDifferent(screenshot, oldScreenshot, newScreenshot, tolerance)) {
        Some(DifferentScreenshots(screenshot))
      } else {
        None
      }
    }
  }

  private def imagesAreDifferent(
      screenshot: Screenshot,
      oldScreenshot: ImmutableImage,
      newScreenshot: ImmutableImage,
      tolerance: Double
  ) = {
    if (oldScreenshot == newScreenshot) {
      false
    } else {
      val oldScreenshotPixels = oldScreenshot.pixels
      val newScreenshotPixels = newScreenshot.pixels

      val differentPixels =
        oldScreenshotPixels.zip(newScreenshotPixels).filter { case (a, b) => a != b }
      val percentageOfDifferentPixels =
        differentPixels.length.toDouble / oldScreenshotPixels.length.toDouble
      val percentageOutOf100        = percentageOfDifferentPixels * 100.0
      val imagesAreDifferent        = percentageOutOf100 > tolerance
      val imagesAreConsideredEquals = !imagesAreDifferent
      if (imagesAreConsideredEquals && tolerance != Config.defaultTolerance) {
        val screenshotName = screenshot.name
        println(
          Console.YELLOW + s"⚠️   Shot warning: There are some pixels changed in the screenshot named $screenshotName, but we consider the comparison correct because tolerance is configured to $tolerance % and the percentage of different pixels is $percentageOutOf100 %" + Console.RESET
        )
      }
      imagesAreDifferent
    }
  }

  private def haveSameDimensions(
      newScreenshot: ImmutableImage,
      recordedScreenshot: ImmutableImage
  ): Boolean =
    newScreenshot.width == recordedScreenshot.width && newScreenshot.height == recordedScreenshot.height

}
