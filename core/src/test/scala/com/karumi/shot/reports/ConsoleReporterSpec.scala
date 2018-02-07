package com.karumi.shot.reports

import com.karumi.shot.base64.Base64Encoder
import com.karumi.shot.domain.{Screenshot, ScreenshotNotFound, ScreenshotsComparisionResult}
import com.karumi.shot.ui.Console
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

object ShotErrorMessages {
  val testBrokenMessage = "❌  Hummmm...the following screenshot tests are broken:\n"
  val screenShotNotFoundMessage = "   🔎  Recorded screenshot not found for test: name"
  val base64EnabledMessage = "🤖  The option printBase64 is enabled. In order to see the generated diff images, run the following commands in your terminal:"
  val base64GenerationErrorMessage = "\t❌ Base64 image generation error, image source not found."
  val base64MessageToCopy = "\t> echo 'base64' | base64 -D > name"
}

class ConsoleReporterSpec
  extends FlatSpec
    with Matchers
    with BeforeAndAfter
    with MockFactory {

  import ShotErrorMessages._

  private var consoleReporter: ConsoleReporter = _
  private val console: Console = mock[Console]
  private val base64Encoder : Base64Encoder = mock[Base64Encoder]
  private val screenshotName = "name"
  private val screenShot: Screenshot = new Screenshot(screenshotName,
    "", s"/$screenshotName", "", "", null, "", Seq(), Seq(), Seq(), null)
  private var screenShotNotFound: ScreenshotNotFound = _
  private var sreenShotsComparisionResultWithErrors: ScreenshotsComparisionResult = _

  before {
    consoleReporter = new ConsoleReporter(console, base64Encoder)
    screenShotNotFound = new ScreenshotNotFound(screenShot)
    sreenShotsComparisionResultWithErrors = new ScreenshotsComparisionResult(List(screenShotNotFound), Seq(screenShot))
  }

  it should "show screenshot not found error when comparision screenshot not found and base64 is not enabled" in {
    (console.showError _).expects(testBrokenMessage)
    (console.showError _).expects(screenShotNotFoundMessage)
    (console.lineBreak _).expects()

    consoleReporter.showErrors(comparision = sreenShotsComparisionResultWithErrors,
      shouldPrintBase64Error = false,
      outputFolder = "")
  }

  it should "show base64 image generation error when base64 is enabled and the base64 encoding fails" in {
    (base64Encoder.base64FromFile _).expects("diff_name").returns(None)

    (console.showError _).expects(testBrokenMessage)
    (console.showError _).expects(screenShotNotFoundMessage)
    (console.lineBreak _).expects()

    (console.show _).expects(base64EnabledMessage)
    (console.lineBreak _).expects()
    (console.showError _).expects(s"Test $screenshotName")
    (console.lineBreak _).expects()
    (console.showError _).expects(base64GenerationErrorMessage)
    (console.lineBreak _).expects()

    consoleReporter.showErrors(comparision = sreenShotsComparisionResultWithErrors,
      shouldPrintBase64Error = true,
      outputFolder = "")
  }

  it should "show base64 message when base64 is enabled" in {
    (base64Encoder.base64FromFile _).expects("diff_name").returns(Some("base64"))

    (console.showError _).expects(testBrokenMessage)
    (console.showError _).expects(screenShotNotFoundMessage)
    (console.lineBreak _).expects()

    (console.show _).expects(base64EnabledMessage)
    (console.lineBreak _).expects()
    (console.showError _).expects(s"Test $screenshotName")
    (console.lineBreak _).expects()
    (console.show _).expects(base64MessageToCopy)
    (console.lineBreak _).expects()

    consoleReporter.showErrors(comparision = sreenShotsComparisionResultWithErrors,
      shouldPrintBase64Error = true,
      outputFolder = "")
  }



}
