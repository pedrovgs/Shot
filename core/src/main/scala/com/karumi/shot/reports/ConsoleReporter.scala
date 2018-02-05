package com.karumi.shot.reports

import java.io.{ByteArrayOutputStream, File}
import java.util.Base64
import javax.imageio.ImageIO

import com.karumi.shot.domain.{
  DifferentImageDimensions,
  DifferentScreenshots,
  ScreenshotNotFound,
  ScreenshotsComparisionResult
}
import com.karumi.shot.ui.Console
import org.apache.commons.io.Charsets

class ConsoleReporter(console: Console) {

  def showBase64Error(comparision: ScreenshotsComparisionResult,
                      outputFolder: String) = {
    console.show(
      "\uD83E\uDD16  The option printBase64 is enabled. In order to see the generated diff images, run the following commands in your terminal:")
    console.lineBreak()
    comparision.screenshots.foreach(screenshot => {
      val diffScreenshotFile =
        new File(screenshot.getDiffScreenshotPath(outputFolder))
      val bufferedImage = ImageIO.read(diffScreenshotFile)
      val outputStream = new ByteArrayOutputStream()
      ImageIO.write(bufferedImage, "png", outputStream)
      val diffImageBase64Encoded =
        Base64.getEncoder.encode(outputStream.toByteArray)
      val diffBase64UTF8 = new String(diffImageBase64Encoded, Charsets.UTF_8)

      console.showError(s"Test ${screenshot.fileName}")
      console.lineBreak()
      console.show(
        s"\t> echo '$diffBase64UTF8' | base64 -D > ${screenshot.fileName}")
      console.lineBreak()
    })
  }

  def showErrors(comparision: ScreenshotsComparisionResult) = {
    console.showError(
      "❌  Hummmm...the following screenshot tests are broken:\n")
    comparision.errors.foreach { error =>
      error match {
        case ScreenshotNotFound(screenshot) =>
          console.showError(
            "   🔎  Recorded screenshot not found for test: " + screenshot.name)
        case DifferentScreenshots(screenshot) =>
          console.showError(
            "   🤔  The application UI has been modified for test: " + screenshot.name)
          console.showError(
            "            💾  You can find the original screenshot here: " + screenshot.recordedScreenshotPath)
          console.showError(
            "            🆕  You can find the new recorded screenshot here: " + screenshot.temporalScreenshotPath)
        case DifferentImageDimensions(screenshot,
                                      originalDimension,
                                      newDimension) => {
          console.showError(
            "   📱  The size of the screenshot taken has changed for test: " + screenshot.name)
          console.showError(
            "            💾  Original screenshot dimension: " + originalDimension + ". You can find the original screenshot here: " + screenshot.recordedScreenshotPath)
          console.showError(
            "            🆕  New recorded screenshot dimension: " + newDimension + ". You can find the new recorded screenshot here: " + screenshot.temporalScreenshotPath)
        }

        case _ =>
          console.showError(
            "   😞  Ups! Something went wrong while comparing your screenshots but we couldn't identify the cause. If you think you've found a bug, please open an issue at https://github.com/karumi/shot.")
      }
      console.lineBreak()
    }
  }

}
