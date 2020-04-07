package com.karumi.shot.screenshots

import java.io.File

import com.karumi.shot.domain.Config
import com.karumi.shot.domain.model.{Folder, ScreenshotsSuite}
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
