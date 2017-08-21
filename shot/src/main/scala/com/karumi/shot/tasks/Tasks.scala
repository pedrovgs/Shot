package com.karumi.shot.tasks

import com.karumi.shot.android.Adb
import com.karumi.shot.screenshots.{ScreenshotsComparator, ScreenshotsSaver}
import com.karumi.shot.ui.Console
import com.karumi.shot.{Files, Shot, ShotExtension}
import org.gradle.api.{DefaultTask, GradleException}
import org.gradle.api.tasks.TaskAction

abstract class ShotTask() extends DefaultTask {

  protected val shot: Shot =
    new Shot(new Adb,
             new Files,
             new ScreenshotsComparator,
             new ScreenshotsSaver,
             new Console)
  protected val shotExtension: ShotExtension =
    getProject.getExtensions.findByType(classOf[ShotExtension])

  setGroup("shot")

}

object ExecuteScreenshotTests {
  val name = "executeScreenshotTests"
}

class ExecuteScreenshotTests extends ShotTask {

  setDescription(
    "Records the user interface tests screenshots. If you execute this task using \"-Precord\" param the screenshot will be regenerated.")

  @TaskAction
  def executeScreenshotTests(): Unit = {
    val project = getProject
    val recordScreenshots = project.hasProperty("record")
    val projectFolder = project.getProjectDir.getAbsolutePath
    val projectName = project.getName
    if (recordScreenshots) {
      shot.recordScreenshots(projectFolder, projectName)
    } else {
      val result = shot.verifyScreenshots(projectFolder, project.getName)
      if (result.hasErrors) {
        throw new GradleException(
          "Screenshots comparision fail. Review the execution report to see what's broken your build.")
      }
    }
  }
}

object PullScreenshotsTask {
  val name = "pullScreenshots"
}

class PullScreenshotsTask extends ShotTask {

  setDescription(
    "Retrieves the screenshots stored into the Android device where the tests were executed.")

  @TaskAction
  def pullScreenshots(): Unit = {
    val projectFolder = getProject.getProjectDir.getAbsolutePath
    val appId = shotExtension.getOptionAppId
    shot.pullScreenshots(projectFolder, appId)
  }
}

object ClearScreenshotsTask {
  val name = "clearScreenshots"
}

class ClearScreenshotsTask extends ShotTask {

  setDescription(
    "Removes the screenshots recorded during the tests execution from the Android device where the tests were executed.")

  @TaskAction
  def clearScreenshots(): Unit = {
    val appId = shotExtension.getOptionAppId
    shot.clearScreenshots(appId)
  }
}
