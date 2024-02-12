package com.karumi.shot.reports

import com.karumi.shot.domain._
import com.karumi.shot.domain.model.{AppId, ScreenshotComparisionErrors, ScreenshotsSuite}

import java.io.{File, FileWriter}
import scala.language.postfixOps

class JunitExecutionReporter extends ExecutionReporter {

  def generateRecordReport(
      appId: AppId,
      screenshots: ScreenshotsSuite,
      shotFolder: ShotFolder
  ): Unit = ()

  def generateVerificationReport(
      appId: AppId,
      comparision: ScreenshotsComparisionResult,
      shotFolder: ShotFolder,
      showOnlyFailingTestsInReports: Boolean = false
  ): Unit = {
    val reportFileContents =
      populateVerificationTemplate(appId, comparision)
    resetVerificationReport(shotFolder)
    val reportFolder = shotFolder.verificationReportFolder()
    writeReport(reportFileContents, reportFolder)
  }

  private def writeReport(
      fileContents: String,
      reportFolder: String
  ): Unit = {
    val indexFile = new File(reportFolder + "TEST-Shot.xml")
    new File(reportFolder).mkdirs()
    val writer = new FileWriter(indexFile)
    writer.write(fileContents)
    writer.close()
  }

  private def resetVerificationReport(shotFolder: ShotFolder) = {
    val file = new File(shotFolder.reportFolder() + "TEST-Shot.xml")
    if (file.exists()) {
      file.delete()
    }
  }

  private def populateVerificationTemplate(
      appId: AppId,
      comparision: ScreenshotsComparisionResult
  ): String = {
    val title = s"Screenshot results: $appId"
    val summaryTableBody =
      generateVerificationSummaryTableBody(comparision)
    report(
      title,
      summaryTableBody
    )
  }

  private def report(title: String, testResults: String): String = {
    s"""|<?xml version="1.0" encoding="utf-8"?>
        |<testsuites name="$title">
        |  $testResults
        |</testsuites>""".stripMargin
  }

  private def generateVerificationSummaryTableBody(
      comparisionResult: ScreenshotsComparisionResult
  ): String = {
    val groupedScreenshots =
      comparisionResult.screenshots
        .groupBy { (screenshot: Screenshot) =>
          screenshot.testClass
        }
    groupedScreenshots
      .map { case (testSuite: String, screenshots: Seq[Screenshot]) =>
        val tests: String = screenshots
          .map(f = screenshot => {
            val error =
              findError(screenshot = screenshot, comparisionResult.errors)
            val isFailedTest = error.isDefined
            val testClass    = screenshot.testClass
            val testName     = screenshot.fileName
            val reason       = generateReasonMessage(error)

            val failureString = if (isFailedTest) {
              s"""<failure message="$reason" />"""
            } else {
              ""
            }

            s"""<testcase name="$testName" classname="$testClass">
                 |  $failureString
                 |</testcase>""".stripMargin
          })
          .mkString("\n")

        s"""<testsuite name="$testSuite">
           |  $tests
           |</testsuite>
           |""".stripMargin
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
          "Recorded screenshot not found."
        case DifferentScreenshots(_, _) =>
          "The application UI has been modified."
        case DifferentImageDimensions(_, _, _) =>
          "The size of the screenshot taken has changed."
        case _ =>
          "Ups! Something went wrong while comparing your screenshots but we couldn't identify the cause. If you think you've found a bug, please open an issue at https://github.com/karumi/shot."
      }
      .getOrElse("")
}
