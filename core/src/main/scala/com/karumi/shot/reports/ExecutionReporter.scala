package com.karumi.shot.reports

import java.io.{File, FileWriter}
import java.util

import scala.collection.JavaConverters._
import org.apache.commons.io.FileUtils
import com.karumi.shot.domain._
import com.karumi.shot.domain.model.{
  AppId,
  Folder,
  ScreenshotComparisionErrors,
  ScreenshotsSuite
}
import freemarker.template.{Configuration, Template, TemplateExceptionHandler}

class ExecutionReporter {

  private val freeMarkerConfig: Configuration = {
    val config = new Configuration(Configuration.VERSION_2_3_23)
    config.setClassForTemplateLoading(getClass, "/templates/")
    config.setDefaultEncoding("UTF-8")
    config.setTemplateExceptionHandler(
      TemplateExceptionHandler.RETHROW_HANDLER)
    config
  }

  def generateRecordReport(appId: AppId,
                           screenshots: ScreenshotsSuite,
                           buildFolder: Folder,
                           flavor: String,
                           buildType: String) = {
    val input = generateRecordTemplateValues(appId, screenshots)
    val template = freeMarkerConfig.getTemplate("recordIndex.ftl")
    resetVerificationReport(flavor, buildType)
    val reportFolder = buildFolder + Config.recordingReportFolder(
      flavor,
      buildType) + "/"
    writeReport(buildFolder, input, template, reportFolder)
  }

  def generateVerificationReport(appId: AppId,
                                 comparision: ScreenshotsComparisionResult,
                                 buildFolder: Folder,
                                 flavor: String,
                                 buildType: String) = {
    val input = generateVerificationTemplateValues(appId, comparision)
    val template = freeMarkerConfig.getTemplate("verificationIndex.ftl")
    resetVerificationReport(flavor, buildType)
    val reportFolder = buildFolder + Config.verificationReportFolder(
      flavor,
      buildType) + "/"
    writeReport(buildFolder, input, template, reportFolder)
  }

  private def writeReport(buildFolder: Folder,
                          input: util.Map[String, String],
                          template: Template,
                          reportFolder: String) = {
    val indexFile = new File(reportFolder + "index.html")
    new File(reportFolder).mkdirs()
    val writer = new FileWriter(indexFile)
    template.process(input, writer)
    writer.close()
  }

  private def resetVerificationReport(flavor: String, buildType: String) = {
    val file = new File(
      Config.verificationReportFolder(flavor, buildType) + "/index.html")
    if (file.exists()) {
      file.delete()
    }
  }

  private def generateRecordTemplateValues(
      appId: AppId,
      screenshots: ScreenshotsSuite): util.Map[String, String] = {
    val title = s"Record results: $appId"
    val numberOfTests = screenshots.size
    val summaryResults =
      s"$numberOfTests screenshot tests recorded."
    val summaryTableBody = generateRecordSummaryTableBody(screenshots)
    Map("title" -> title,
        "summaryResult" -> summaryResults,
        "summaryTableBody" -> summaryTableBody).asJava
  }

  private def generateRecordSummaryTableBody(
      screenshots: ScreenshotsSuite): String = {
    screenshots
      .map { screenshot: Screenshot =>
        val testClass = screenshot.testClass
        val testName = screenshot.testName
        val originalScreenshot = "./images/recorded/" + screenshot.name + ".png"
        val width = (screenshot.screenshotDimension.width * 0.2).toInt
        "<tr>" +
          s"<th> <p>Test class: $testClass</p>" +
          s"<p>Test name: $testName</p></th>" +
          s"<th> <a href='$originalScreenshot'><img width='$width' src='$originalScreenshot'/></a></th>" +
          "</tr>"
      }
      .mkString("\n")
  }

  private def generateVerificationTemplateValues(
      appId: AppId,
      comparision: ScreenshotsComparisionResult): util.Map[String, String] = {
    val title = s"Verification results: $appId"
    val screenshots = comparision.screenshots
    val numberOfTests = screenshots.size
    val failedNumber = comparision.errors.size
    val successNumber = numberOfTests - failedNumber
    val summaryResults =
      s"$numberOfTests screenshot tests executed. $successNumber passed and $failedNumber failed."
    val summaryTableBody = generateVerificationSummaryTableBody(comparision)
    val screenshotsTableBody = generateScreenshotsTableBody(comparision)
    Map("title" -> title,
        "summaryResult" -> summaryResults,
        "summaryTableBody" -> summaryTableBody,
        "screenshotsTableBody" -> screenshotsTableBody).asJava
  }

  private def getSortedByResultScreenshots(
      comparison: ScreenshotsComparisionResult) =
    comparison.screenshots
      .map { screenshot: Screenshot =>
        val error = findError(screenshot, comparison.errors)
        (screenshot, error)
      }
      .sortBy(_._2.isEmpty)

  private def generateVerificationSummaryTableBody(
      comparision: ScreenshotsComparisionResult): String = {
    getSortedByResultScreenshots(comparision)
      .map {
        case (screenshot, error) =>
          val isFailedTest = error.isDefined
          val testClass = screenshot.testClass
          val testName = screenshot.testName
          val result = if (isFailedTest) "❌" else "✅"
          val reason = generateReasonMessage(error)
          val color = if (isFailedTest) "red-text" else "green-text"
          val id = screenshot.name.replace(".", "")
          "<tr>" +
            s"<th><a href='#$id'>$result</></th>" +
            s"<th><a href='#$id'><p class='$color'>Test class: $testClass</p>" +
            s"<p class='$color'>Test name: $testName</p></a></th>" +
            s"<th>$reason</th>" +
            "</tr>"
      }
      .mkString("\n")
  }

  private def generateScreenshotsTableBody(
      comparision: ScreenshotsComparisionResult): String = {
    getSortedByResultScreenshots(comparision)
      .map {
        case (screenshot, error) =>
          val isFailedTest = error.isDefined
          val testClass = screenshot.testClass
          val testName = screenshot.testName
          val originalScreenshot = "./images/recorded/" + screenshot.name + ".png"
          val newScreenshot = "./images/" + screenshot.name + ".png"
          val diff = if (error.exists(_.isInstanceOf[DifferentScreenshots])) {
            screenshot.getDiffScreenshotPath("./images/")
          } else {
            ""
          }
          val color = if (isFailedTest) "red-text" else "green-text"
          val width = (screenshot.screenshotDimension.width * 0.2).toInt
          val id = screenshot.name.replace(".", "")
          "<tr>" +
            s"<th id='$id'> <p class='$color'>Test class: $testClass</p>" +
            s"<p class='$color'>Test name: $testName</p></th>" +
            s"<th> <a href='$originalScreenshot'><img width='$width' src='$originalScreenshot'/></a></th>" +
            s"<th> <a href='$newScreenshot'><img width='$width' src='$newScreenshot'/></a></th>" +
            s"<th> <a href='$diff'><img width='$width' src='$diff'/></a></th>" +
            "</tr>"
      }
      .mkString("\n")
  }

  private def findError(screenshot: Screenshot,
                        errors: ScreenshotComparisionErrors)
    : Option[ScreenshotComparisionError] =
    errors.find {
      case ScreenshotNotFound(error) => screenshot == error
      case DifferentImageDimensions(error, _, _) => screenshot == error
      case DifferentScreenshots(error, _) => screenshot == error
      case _ => false
    }

  private def generateReasonMessage(
      error: Option[ScreenshotComparisionError]): String =
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
