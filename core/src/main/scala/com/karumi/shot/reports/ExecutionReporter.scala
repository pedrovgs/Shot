package com.karumi.shot.reports

import java.io.{File, FileWriter}

import com.karumi.shot.domain._
import com.karumi.shot.domain.model.{AppId, Folder, ScreenshotComparisionErrors, ScreenshotsSuite}
import com.karumi.shot.templates.RecordIndexTemplate.recordIndexTemplate
import com.karumi.shot.templates.VerificationIndexTemplate.verificationIndexTemplate

class ExecutionReporter {

  def generateRecordReport(
      appId: AppId,
      screenshots: ScreenshotsSuite,
      shotFolder: ShotFolder
  ) = {
    val reportFileContents = populateRecordTemplate(appId, screenshots)
    resetVerificationReport(shotFolder)
    val reportFolder = shotFolder.recordingReportFolder()
    writeReport(reportFileContents, reportFolder)
  }

  def generateVerificationReport(
      appId: AppId,
      comparision: ScreenshotsComparisionResult,
      shotFolder: ShotFolder,
      showOnlyFailingTestsInReports: Boolean = false
  ) = {
    val reportFileContents =
      populateVerificationTemplate(appId, comparision, showOnlyFailingTestsInReports)
    resetVerificationReport(shotFolder)
    val reportFolder = shotFolder.verificationReportFolder()
    writeReport(reportFileContents, reportFolder)
  }

  private def writeReport(
      fileContents: String,
      reportFolder: String
  ) = {
    val indexFile = new File(reportFolder + "index.html")
    new File(reportFolder).mkdirs()
    val writer = new FileWriter(indexFile)
    writer.write(fileContents)
    writer.close()
  }

  private def resetVerificationReport(shotFolder: ShotFolder) = {
    val file = new File(shotFolder.reportFolder() + "index.html")
    if (file.exists()) {
      file.delete()
    }
  }

  private def populateRecordTemplate(
      appId: AppId,
      screenshots: ScreenshotsSuite
  ): String = {
    val title         = s"Record results: $appId"
    val numberOfTests = screenshots.size
    val summaryResults =
      s"$numberOfTests screenshot tests recorded."
    val summaryTableBody = generateRecordSummaryTableBody(screenshots)
    recordIndexTemplate(
      title = title,
      summaryResult = summaryResults,
      summaryTableBody = summaryTableBody
    )
  }

  private def generateRecordSummaryTableBody(screenshots: ScreenshotsSuite): String = {
    screenshots
      .map { screenshot: Screenshot =>
        val testClass          = screenshot.testClass
        val testName           = screenshot.testName
        val originalScreenshot = "./images/recorded/" + screenshot.name + ".png"
        val width              = (screenshot.screenshotDimension.width * 0.2).toInt
        val screenshotName     = screenshot.name
        "<tr>" +
          s"<th> <p>Test class: $testClass</p>" +
          s"<p>Test name: $testName</p>" +
          s"<p>Screenshot name: $screenshotName</p></th>" +
          s"<th> <a href='$originalScreenshot'><img width='$width' src='$originalScreenshot'/></a></th>" +
          "</tr>"
      }
      .mkString("\n")
  }

  private def populateVerificationTemplate(
      appId: AppId,
      comparision: ScreenshotsComparisionResult,
      showOnlyFailingTestsInReports: Boolean
  ): String = {
    val title         = s"Verification results: $appId"
    val screenshots   = comparision.screenshots
    val numberOfTests = screenshots.size
    val failedNumber  = comparision.errors.size
    val successNumber = numberOfTests - failedNumber
    val summaryResults =
      s"$numberOfTests screenshot tests executed. $successNumber passed and $failedNumber failed."
    val summaryTableBody =
      generateVerificationSummaryTableBody(comparision, showOnlyFailingTestsInReports)
    val screenshotsTableBody =
      generateScreenshotsTableBody(comparision, showOnlyFailingTestsInReports)
    verificationIndexTemplate(
      title = title,
      summaryResult = summaryResults,
      summaryTableBody = summaryTableBody,
      screenshotsTableBody = screenshotsTableBody
    )
  }

  private def getSortedByResultScreenshots(comparison: ScreenshotsComparisionResult) =
    comparison.screenshots
      .map { screenshot: Screenshot =>
        val error = findError(screenshot, comparison.errors)
        (screenshot, error)
      }
      .sortBy(_._2.isEmpty)

  private def generateVerificationSummaryTableBody(
      comparision: ScreenshotsComparisionResult,
      showOnlyFailingTestsInReports: Boolean
  ): String = {
    getSortedByResultScreenshots(comparision)
      .map { case (screenshot, error) =>
        val isFailedTest   = error.isDefined
        val testClass      = screenshot.testClass
        val testName       = screenshot.testName
        val result         = if (isFailedTest) "❌" else "✅"
        val reason         = generateReasonMessage(error)
        val color          = if (isFailedTest) "red-text" else "green-text"
        val id             = screenshot.name.replace(".", "")
        val screenshotName = screenshot.name

        if (showOnlyFailingTestsInReports && isFailedTest || !showOnlyFailingTestsInReports) {
          "<tr>" +
            s"<th><a href='#$id'>$result</a></th>" +
            s"<th><a href='#$id'><p class='$color'>Test class: $testClass</p>" +
            s"<p class='$color'>Test name: $testName</p></a></th>" +
            s"<p class='$color'>Screenshot name: $screenshotName</p></th>" +
            s"<th>$reason</th>" +
            "</tr>"
        } else {
          ""
        }
      }
      .mkString("\n")
  }

  private def generateScreenshotsTableBody(
      comparision: ScreenshotsComparisionResult,
      showOnlyFailingTestsInReports: Boolean
  ): String = {
    getSortedByResultScreenshots(comparision)
      .map { case (screenshot, error) =>
        val isFailedTest       = error.isDefined
        val testClass          = screenshot.testClass
        val testName           = screenshot.testName
        val originalScreenshot = "./images/recorded/" + screenshot.name + ".png"
        val newScreenshot      = "./images/" + screenshot.name + ".png"
        val diff = if (error.exists(_.isInstanceOf[DifferentScreenshots])) {
          screenshot.getDiffScreenshotPath("./images/")
        } else {
          ""
        }
        val color          = if (isFailedTest) "red-text" else "green-text"
        val width          = (screenshot.screenshotDimension.width * 0.2).toInt
        val id             = screenshot.name.replace(".", "")
        val screenshotName = screenshot.name

        if (showOnlyFailingTestsInReports && isFailedTest || !showOnlyFailingTestsInReports) {
          "<tr>" +
            s"<th id='$id'> <p class='$color'>Test class: $testClass</p>" +
            s"<p class='$color'>Test name: $testName</p>" +
            s"<p class='$color'>Screenshot name: $screenshotName</p></th>" +
            s"<th> <a href='$originalScreenshot'><img width='$width' src='$originalScreenshot'/></a></th>" +
            s"<th> <a href='$newScreenshot'><img width='$width' src='$newScreenshot'/></a></th>" +
            s"<th> <a href='$diff'><img width='$width' src='$diff'/></a></th>" +
            "</tr>"
        } else {
          ""
        }
      }
      .mkString("\n")
  }

  private def findError(
      screenshot: Screenshot,
      errors: ScreenshotComparisionErrors
  ): Option[ScreenshotComparisonError] =
    errors.find {
      case ScreenshotNotFound(error)             => screenshot == error
      case DifferentImageDimensions(error, _, _) => screenshot == error
      case DifferentScreenshots(error, _)        => screenshot == error
      case _                                     => false
    }

  private def generateReasonMessage(error: Option[ScreenshotComparisonError]): String =
    error
      .map {
        case ScreenshotNotFound(_) =>
          "<p class='red-text'>🔎  Recorded screenshot not found.</p>"
        case DifferentScreenshots(_, _) =>
          "<p class='red-text'>🤔  The application UI has been modified.</p>"
        case DifferentImageDimensions(_, _, _) =>
          "<p class='red-text'>📱  The size of the screenshot taken has changed.</p>"
        case _ =>
          "<p class='red-text'>😞  Ups! Something went wrong while comparing your screenshots but we couldn't identify the cause. If you think you've found a bug, please open an issue at https://github.com/karumi/shot.</p>"
      }
      .getOrElse("")
}
