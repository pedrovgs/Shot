package com.karumi.shot.screenshots

import java.io.File

import com.karumi.shot.domain.{
  Config,
  DifferentImageDimensions,
  DifferentScreenshots,
  Dimension,
  Screenshot,
  ScreenshotNotFound,
  ScreenshotsComparisionResult
}
import com.karumi.shot.domain.model.{Folder, ScreenshotsSuite}
import com.sksamuel.scrimage.Image
import org.apache.commons.io.FileUtils

class ScreenshotsSaver {

  def saveRecordedScreenshots(projectFolder: Folder,
                              flavor: String,
                              buildType: String,
                              screenshots: ScreenshotsSuite) = {
    deleteOldScreenshots(projectFolder, flavor, buildType)
    saveScreenshots(
      screenshots,
      projectFolder + Config.screenshotsFolderName(flavor, buildType))
  }

  def saveTemporalScreenshots(screenshots: ScreenshotsSuite,
                              projectName: String,
                              reportFolder: String) = {
    deleteOldTemporalScreenshots(projectName)
    saveScreenshots(screenshots,
                    Config.screenshotsTemporalRootPath + projectName + "/")
    deleteFolder(reportFolder)
    saveScreenshots(screenshots, reportFolder)
  }

  def copyRecordedScreenshotsToTheReportFolder(projectFolder: Folder,
                                               flavor: String,
                                               buildType: String,
                                               destinyFolder: Folder) = {
    val recordedScreenshotsFolder = projectFolder + Config
      .screenshotsFolderName(flavor, buildType)
    FileUtils.copyDirectory(new File(recordedScreenshotsFolder),
                            new File(destinyFolder))
    deleteFolder(destinyFolder)
  }

  def copyOnlyFailingRecordedScreenshotsToTheReportFolder(
      projectFolder: Folder,
      flavor: String,
      buildType: String,
      destinyFolder: Folder,
      updatedComparison: ScreenshotsComparisionResult): Unit = {

    updatedComparison.errors.foreach {
      case ScreenshotNotFound(screenshot) =>
        copyFile(screenshot, destinyFolder)

      case DifferentScreenshots(screenshot, base64Diff) =>
        copyFile(screenshot, destinyFolder)

      case DifferentImageDimensions(screenshot,
                                    originalDimension,
                                    newDimension) =>
        copyFile(screenshot, destinyFolder)

      case _ => // Nothing to do
    }
  }

  private def copyFile(screenshot: Screenshot, destinyFolder: Folder): Unit = {
    val existingScreenshot = new File(screenshot.recordedScreenshotPath)
    FileUtils.copyFile(existingScreenshot,
                       new File(destinyFolder + existingScreenshot.getName))
  }

  def getScreenshotDimension(projectFolder: String,
                             flavor: String,
                             buildType: String,
                             screenshot: Screenshot): Dimension = {
    val screenshotPath = projectFolder + Config.pulledScreenshotsFolder(
      flavor,
      buildType) + screenshot.name + ".png"
    val image = Image.fromFile(new File(screenshotPath))
    Dimension(image.width, image.height)
  }

  private def deleteOldScreenshots(projectFolder: Folder,
                                   flavor: String,
                                   buildType: String) = {
    deleteFolder(
      projectFolder + Config.screenshotsFolderName(flavor, buildType))
  }

  private def deleteOldTemporalScreenshots(projectName: String): Unit = {
    deleteFolder(Config.screenshotsTemporalRootPath + projectName + "/")
  }

  private def deleteFolder(path: String): Unit = {
    val folder = new File(path)
    if (folder.exists()) {
      folder.delete()
    }
  }

  private def saveScreenshots(screenshots: ScreenshotsSuite, folder: Folder) = {
    val screenshotsFolder = new File(folder)
    if (!screenshotsFolder.exists()) {
      screenshotsFolder.mkdirs()
    }
    screenshots.par.foreach { screenshot =>
      val outputFile = new File(folder + screenshot.fileName)
      if (!outputFile.exists()) {
        outputFile.createNewFile()
      }
      val image = ScreenshotComposer.composeNewScreenshot(screenshot)
      image.output(outputFile)
    }
  }

}
