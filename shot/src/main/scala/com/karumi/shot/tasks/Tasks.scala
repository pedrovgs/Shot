package com.karumi.shot.tasks

import com.karumi.shot.{Adb, Shot}
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class ShotTask() extends DefaultTask {

  protected val shot = new Shot(new Adb)

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
    val recordScreenshots = getProject.hasProperty("record")
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
    shot.pullScreenshots()
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
    shot.clearScreenshots()
  }
}
