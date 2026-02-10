package com.karumi.shot.screenshots

import java.io.File

import com.karumi.shot.domain._
import com.karumi.shot.domain.model.ScreenshotsSuite
import com.sksamuel.scrimage.ImmutableImage
import scala.collection.parallel.CollectionConverters._

class ScreenshotsComparator {

  def compare(
      screenshots: ScreenshotsSuite,
      tolerance: Double,
      colorTolerance: Double = 0.0
  ): ScreenshotsComparisionResult = {
    val errors =
      screenshots.par.flatMap(compareScreenshot(_, tolerance, colorTolerance)).toList
    ScreenshotsComparisionResult(errors, screenshots)
  }

  private def compareScreenshot(
      screenshot: Screenshot,
      tolerance: Double,
      colorTolerance: Double
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
      } else if (imagesAreDifferent(screenshot, oldScreenshot, newScreenshot, tolerance, colorTolerance)) {
        Some(DifferentScreenshots(screenshot))
      } else {
        None
      }
    }
  }

  /** Per-channel threshold (0-255). When colorTolerancePercent is 0, no fuzz; otherwise
    * two pixels are considered equal if each ARGB channel differs by at most this much.
    * Derived from colorTolerancePercent (0-100): threshold = (colorTolerancePercent/100 * 255).round
    */
  private def colorToleranceThreshold(colorTolerancePercent: Double): Int =
    if (colorTolerancePercent <= 0) 0
    else Math.min(255, Math.round(colorTolerancePercent / 100.0 * 255).toInt)

  /** True if two ARGB pixels differ by more than the per-channel threshold. */
  private def pixelsDiffer(argb1: Int, argb2: Int, threshold: Int): Boolean = {
    if (threshold <= 0) return argb1 != argb2
    def channel(c: Int, shift: Int): Int = (c >> shift) & 0xff
    Math.abs(channel(argb1, 24) - channel(argb2, 24)) > threshold ||
    Math.abs(channel(argb1, 16) - channel(argb2, 16)) > threshold ||
    Math.abs(channel(argb1, 8) - channel(argb2, 8)) > threshold ||
    Math.abs(channel(argb1, 0) - channel(argb2, 0)) > threshold
  }

  private def imagesAreDifferent(
      screenshot: Screenshot,
      oldScreenshot: ImmutableImage,
      newScreenshot: ImmutableImage,
      tolerance: Double,
      colorTolerance: Double
  ) = {
    if (oldScreenshot == newScreenshot) {
      false
    } else {
      val threshold = colorToleranceThreshold(colorTolerance)
      val oldArgb = oldScreenshot.pixels.map(_.argb)
      val newArgb = newScreenshot.pixels.map(_.argb)

      val differentPixels =
        if (threshold <= 0)
          oldArgb.zip(newArgb).filter { case (a, b) => a != b }
        else
          oldArgb.zip(newArgb).filter { case (a, b) => pixelsDiffer(a, b, threshold) }
      val percentageOfDifferentPixels =
        differentPixels.length.toDouble / oldArgb.length.toDouble
      val percentageOutOf100        = percentageOfDifferentPixels * 100.0
      val imagesAreDifferent        = percentageOutOf100 > tolerance
      val imagesAreConsideredEquals = !imagesAreDifferent
      if (imagesAreConsideredEquals && (tolerance != Config.defaultTolerance || colorTolerance > 0)) {
        val screenshotName = screenshot.name
        val msg = if (colorTolerance > 0)
          s"⚠️   Shot warning: There are some pixels changed in the screenshot named $screenshotName, but we consider the comparison correct because tolerance is configured to $tolerance % and colorTolerance to $colorTolerance %; the percentage of different pixels is $percentageOutOf100 %"
        else
          s"⚠️   Shot warning: There are some pixels changed in the screenshot named $screenshotName, but we consider the comparison correct because tolerance is configured to $tolerance % and the percentage of different pixels is $percentageOutOf100 %"
        println(Console.YELLOW + msg + Console.RESET)
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
