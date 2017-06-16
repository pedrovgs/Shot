package com.karumi.shot.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import scala.sys.process._

abstract class ShotTask() extends DefaultTask {

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
    println("--------> PULL SCREENSHOTS")
    val result =
      "adb pull /sdcard/screenshots/com.karumi.screenshot.test/screenshots-default/ .".!!
    println("-------> " + result)
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
    val result =
      "adb shell rm -r /sdcard/screenshots/com.karumi.screenshot.test/screenshots-default/".!
  }
}
