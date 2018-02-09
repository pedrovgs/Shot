package com.karumi.shot.screenshots

import java.io.File

import com.karumi.shot.base64.Base64Encoder
import com.karumi.shot.domain.model.ScreenshotComparisionErrors
import com.karumi.shot.domain.{
  DifferentScreenshots,
  ScreenshotsComparisionResult
}
import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.composite.RedComposite

class ScreenshotsDiffGenerator(base64Encoder: Base64Encoder) {

  def generateDiffs(
      comparision: ScreenshotsComparisionResult,
      outputFolder: String,
      generateBase64Diff: Boolean): ScreenshotsComparisionResult = {
    val updatedErrors: ScreenshotComparisionErrors =
      comparision.errors.par.map {
        case error: DifferentScreenshots =>
          generateDiff(error, outputFolder, generateBase64Diff)
        case anyOtherError => anyOtherError
      }.seq
    comparision.copy(errors = updatedErrors)
  }

  private def generateDiff(
      error: DifferentScreenshots,
      outputFolder: String,
      generateBase64Diff: Boolean): DifferentScreenshots = {
    val screenshot = error.screenshot
    val originalImagePath = screenshot.recordedScreenshotPath
    val newImagePath = screenshot.temporalScreenshotPath
    val originalImage = Image.fromFile(new File(originalImagePath))
    val newImage = Image.fromFile(new File(newImagePath))
    val diff = newImage.composite(new RedComposite(1d), originalImage)
    val outputFilePath = screenshot.getDiffScreenshotPath(outputFolder)
    diff.output(outputFilePath)
    if (generateBase64Diff) {
      error.copy(base64Diff = base64Encoder.base64FromFile(outputFilePath))
    } else {
      error
    }
  }

}
