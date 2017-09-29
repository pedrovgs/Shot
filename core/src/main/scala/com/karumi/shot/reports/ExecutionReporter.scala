package com.karumi.shot.reports

import java.io.{File, FileWriter}
import java.util
import scala.collection.JavaConverters._
import org.apache.commons.io.FileUtils
import com.karumi.shot.domain._
import com.karumi.shot.domain.model.{
  AppId,
  Folder,
  ScreenshotComparisionErrors
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

  def generateVerificationReport(appId: AppId,
                                 comparision: ScreenshotsComparisionResult,
                                 buildFolder: Folder) = {
    val input = generateTemplateValues(appId, comparision)
    val template = freeMarkerConfig.getTemplate("verification/index.ftl")
    resetVerificationReport()
    writeReport(buildFolder, input, template)
  }

  private def writeReport(buildFolder: Folder,
                          input: util.Map[String, String],
                          template: Template) = {
    val reportFolder = buildFolder + Config.verificationReportFolder + "/"
    val indexFile = new File(reportFolder + "index.html")
    new File(reportFolder).mkdirs()
    val writer = new FileWriter(indexFile)
    template.process(input, writer)
    writer.close()
  }

  private def resetVerificationReport() = {
    val file = new File(Config.verificationReportFolder + "/index.html")
    if (file.exists()) {
      file.delete()
    }
  }

  private def generateTemplateValues(
      appId: AppId,
      comparision: ScreenshotsComparisionResult): util.Map[String, String] = {
    val title = s"Verification results: $appId"
    val screenshots = comparision.screenshots
    val numberOfTests = screenshots.size
    val failedNumber = comparision.errors.size
    val successNumber = numberOfTests - failedNumber
    val summaryResults =
      s"$numberOfTests screenshot tests executed. $successNumber passed and $failedNumber failed."
    val summaryTableBody = generateSummaryTableBody(comparision)
    val screenshotsTableBody = generateScreenshotsTableBody(comparision)
    Map("title" -> title,
        "summaryResult" -> summaryResults,
        "summaryTableBody" -> summaryTableBody,
        "screenshotsTableBody" -> screenshotsTableBody).asJava
  }

  private def generateSummaryTableBody(
      comparision: ScreenshotsComparisionResult): String = {
    val errors = comparision.errors
    comparision.screenshots
      .map { screenshot: Screenshot =>
        val error = findError(screenshot, errors)
        val isFailedTest = error.isDefined
        val testClass = screenshot.testClass
        val testName = screenshot.testName
        val result = if (isFailedTest) "❌" else "✅"
        val reason = generateReasonMessage(error)
        val color = if (isFailedTest) "red-text" else "green-text"
        "<tr>" +
          s"<th>$result</th>" +
          s"<th> <p class='$color'>Test class: $testClass</p>" +
          s"<p class='$color'>Test name: $testName</p></th>" +
          s"<th>$reason</th>" +
          "</tr>"
      }
      .mkString("\n")
  }

  private def generateScreenshotsTableBody(
      comparision: ScreenshotsComparisionResult): String = {
    val errors = comparision.errors
    comparision.screenshots
      .map { screenshot: Screenshot =>
        val error = findError(screenshot, errors)
        val isFailedTest = error.isDefined
        val testClass = screenshot.testClass
        val testName = screenshot.testName
        val originalScreenshot = "./images/recorded/" + screenshot.name + ".png"
        val newScreenshot = "./images/" + screenshot.name + ".png"
        val diff = ""
        val reason = generateReasonMessage(error)
        val color = if (isFailedTest) "red-text" else "green-text"
        val width = (screenshot.screenshotDimension.width * 0.2).toInt
        "<tr>" +
          s"<th> <p class='$color'>Test class: $testClass</p>" +
          s"<p class='$color'>Test name: $testName</p></th>" +
          s"<th> <img width='$width' src='$originalScreenshot'/></th>" +
          s"<th> <img width='$width' src='$newScreenshot'/></th>" +
          s"<th> <img width='$width' src='$diff'/></th>" +
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
      case DifferentScreenshots(error) => screenshot == error
      case _ => false
    }

  private def generateReasonMessage(
      error: Option[ScreenshotComparisionError]): String =
    error
      .map {
        case ScreenshotNotFound(screenshot) =>
          "<p class='red-text'>🔎  Recorded screenshot not found.</p>"
        case DifferentScreenshots(screenshot) =>
          "<p class='red-text'>🤔  The application UI has been modified.</p>"
        case DifferentImageDimensions(screenshot, _, _) =>
          "<p class='red-text'>📱  The size of the screenshot taken has changed.</p>"
        case _ =>
          "<p class='red-text'>😞  Ups! Something went wrong while comparing your screenshots but we couldn't identify the cause. If you think you've found a bug, please open an issue at https://github.com/karumi/shot.</p>"
      }
      .getOrElse("")
}
