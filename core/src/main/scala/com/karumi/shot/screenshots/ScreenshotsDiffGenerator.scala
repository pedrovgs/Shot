package com.karumi.shot.screenshots

import java.io.File

import com.karumi.shot.domain.{
  DifferentScreenshots,
  ScreenshotsComparisionResult
}
import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.composite.RedComposite

class ScreenshotsDiffGenerator {

  def generateDiffs(comparision: ScreenshotsComparisionResult,
                    outputFolder: String): Unit =
    comparision.errors.par.foreach {
      case error: DifferentScreenshots =>
        generateDiff(error, outputFolder)
      case _ => ()
    }

  private def generateDiff(error: DifferentScreenshots,
                           outputFolder: String): Unit = {
    val screenshot = error.screenshot
    val originalImagePath = screenshot.recordedScreenshotPath
    val newImagePath = screenshot.temporalScreenshotPath
    val originalImage = Image.fromFile(new File(originalImagePath))
    val newImage = Image.fromFile(new File(newImagePath))
    val diff = newImage.composite(new RedComposite(1d), originalImage)
    val outputFilePath = outputFolder + s"diff_${screenshot.fileName}"
    diff.output(outputFilePath)
  }

}
