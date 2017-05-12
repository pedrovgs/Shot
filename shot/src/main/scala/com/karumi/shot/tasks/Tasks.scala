package com.karumi.shot.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.internal.tasks.options.Option
import org.gradle.api.tasks.{Optional, TaskAction}

abstract class ShotTask() extends DefaultTask {

  setGroup("shot")

}

object ExecuteScreenshotTests {
  val name = "executeScreenshotTests"
}
class ExecuteScreenshotTests extends ShotTask {

  setDescription("Records the user interface tests screenshots.")

  @TaskAction
  def executeScreenshotTests(): Unit = {
    val recordScreenshots = getProject.hasProperty("record")
    println("---------> EXECUTE. RECORD = " + recordScreenshots)
  }
}

object PullScreenshotsTask {
  val name = "pullScreenshots"
}
class PullScreenshotsTask extends ShotTask {

  setDescription("Retrieves the screenshots stored into the Android device where the tests were executed.")

  @TaskAction
  def pullScreenshots(): Unit = {
    println("---------> PULL")
  }
}


object ClearScreenshotsTask {
  val name = "clearScreenshots"
}
class ClearScreenshotsTask extends ShotTask {

  setDescription("Removes the screenshots recorded during the tests execution from the Android device where the tests were executed.")

  @TaskAction
  def clearScreenshots(): Unit = {
    println("---------> CLEAR")
  }
}