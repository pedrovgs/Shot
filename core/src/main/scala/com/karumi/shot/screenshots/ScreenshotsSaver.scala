package com.karumi.shot.screenshots

import com.karumi.shot.domain.model.{FilePath, Folder, ScreenshotsSuite}
import com.karumi.shot.domain.{Dimension, Screenshot, ScreenshotsComparisionResult, ShotFolder}
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
import org.apache.commons.io.FileUtils

import java.io.File

class ScreenshotsSaver {

  def saveRecordedScreenshots(
      to: FilePath,
      screenshots: ScreenshotsSuite
  ): Unit = {
    deleteFile(to)
    saveScreenshots(screenshots, to)
  }

  private def saveScreenshots(screenshots: ScreenshotsSuite, folder: Folder) = {
    val screenshotsFolder = new File(folder)
    if (!screenshotsFolder.exists()) {
      screenshotsFolder.mkdirs()
    }
    screenshots.foreach { screenshot =>
      val outputFile = new File(folder + screenshot.fileName)
      if (!outputFile.exists()) {
        outputFile.createNewFile()
      }
      val image = ScreenshotComposer.composeNewScreenshot(screenshot)
      image.output(PngWriter.MaxCompression, outputFile)
    }
  }

  private def deleteFile(path: String): Unit = {
    val folder = new File(path)
    if (folder.exists()) {
      folder.delete()
    }
  }

  def saveTemporalScreenshots(
      screenshots: ScreenshotsSuite,
      projectName: String,
      reportFolder: String,
      shotFolder: ShotFolder
  ) = {
    deleteOldTemporalScreenshots(projectName, shotFolder)
    saveScreenshots(screenshots, shotFolder.screenshotsTemporalBuildPath() + "/")
    deleteFile(reportFolder)
    saveScreenshots(screenshots, reportFolder)
  }

  private def deleteOldTemporalScreenshots(projectName: String, shotFolder: ShotFolder): Unit = {
    deleteFile(shotFolder.screenshotsTemporalBuildPath() + "/")
  }

  def copyRecordedScreenshotsToTheReportFolder(
      from: FilePath,
      to: FilePath
  ) = {
    FileUtils.copyDirectory(new File(from), new File(to))
    deleteFile(to)
  }

  def copyOnlyFailingRecordedScreenshotsToTheReportFolder(
      destinyFolder: Folder,
      screenshotsResult: ScreenshotsComparisionResult
  ): Unit = {
    screenshotsResult.errorScreenshots.foreach(copyFile(_, destinyFolder))
    deleteFile(destinyFolder)
  }

  private def copyFile(screenshot: Screenshot, destinyFolder: Folder): Unit = {
    val existingScreenshot = new File(screenshot.recordedScreenshotPath)
    FileUtils.copyFile(existingScreenshot, new File(destinyFolder + existingScreenshot.getName))
  }

  def removeNonFailingReferenceImages(
      verificationReferenceImagesFolder: Folder,
      screenshotsResult: ScreenshotsComparisionResult
  ): Unit =
    screenshotsResult.correctScreenshots.foreach(screenshot =>
      deleteFile(verificationReferenceImagesFolder + screenshot.fileName)
    )

  def getScreenshotDimension(
      shotFolder: ShotFolder,
      screenshot: Screenshot
  ): Dimension = {
    val screenshotPath = shotFolder
      .pulledScreenshotsFolder() + screenshot.name + ".png"
    val image = ImmutableImage.loader().fromFile(new File(screenshotPath))
    Dimension(image.width, image.height)
  }

}
