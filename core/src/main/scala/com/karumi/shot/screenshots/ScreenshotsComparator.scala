package com.karumi.shot.screenshots

import java.io.File

import com.karumi.shot.domain.model.ScreenshotsSuite
import com.karumi.shot.domain.{
  Screenshot,
  ScreenshotComparisionError,
  ScreenshotsComparisionResult
}
import com.sksamuel.scrimage.Image

class ScreenshotsComparator {

  def compare(screenshots: ScreenshotsSuite): ScreenshotsComparisionResult = {
    val errors = screenshots.flatMap(compareScreenshot)
    ScreenshotsComparisionResult(errors, screenshots)
  }

  private def compareScreenshot(
      screenshot: Screenshot): Option[ScreenshotComparisionError] = {
    val recordedImage =
      Image.fromFile(new File(screenshot.recordedScreenshotPath))
    recordedImage.output(new File("/Users/Pedro/Desktop/imageRecorded.png"))
    None //TODO: Fix this
  }

}
