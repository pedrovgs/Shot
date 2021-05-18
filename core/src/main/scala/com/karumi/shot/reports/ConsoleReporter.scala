package com.karumi.shot.reports

import com.karumi.shot.domain._
import com.karumi.shot.ui.Console

class ConsoleReporter(console: Console) {

  def showErrors(comparision: ScreenshotsComparisionResult, outputFolder: String): Unit = {
    console.showError("❌  Hummmm...the following screenshot tests are broken:\n")
    comparision.errors.foreach { error =>
      error match {
        case ScreenshotNotFound(screenshot) =>
          console.showError("   🔎  Recorded screenshot not found for test: " + screenshot.name)
        case DifferentScreenshots(screenshot, base64Diff) =>
          console.showError(
            "   🤔  The application UI has been modified for test: " + screenshot.name
          )
          console.showError(
            "            💾  You can find the original screenshot here: " + screenshot.recordedScreenshotPath
          )
          console.showError(
            "            🆕  You can find the new recorded screenshot here: " + screenshot.temporalScreenshotPath
          )
          showBase64Diff(screenshot, base64Diff)
        case DifferentImageDimensions(screenshot, originalDimension, newDimension) => {
          console.showError(
            "   📱  The size of the screenshot taken has changed for test: " + screenshot.name
          )
          console.showError(
            "            💾  Original screenshot dimension: " + originalDimension + ". You can find the original screenshot here: " + screenshot.recordedScreenshotPath
          )
          console.showError(
            "            🆕  New recorded screenshot dimension: " + newDimension + ". You can find the new recorded screenshot here: " + screenshot.temporalScreenshotPath
          )
        }

        case _ =>
          console.showError(
            "   😞  Ups! Something went wrong while comparing your screenshots but we couldn't identify the cause. If you think you've found a bug, please open an issue at https://github.com/karumi/shot."
          )
      }
      console.lineBreak()
    }
  }

  private def showBase64Diff(screenshot: Screenshot, base64Diff: Option[String]) =
    base64Diff match {
      case Some(diff) =>
        console.showError(
          "            🤖  The option printBase64 is enabled. In order to see the generated diff image for this failing test, run the following command in your terminal:"
        )
        console.showError(s"            > echo '$diff' | base64 -D > ${screenshot.fileName}")
      case _ =>
    }

}
