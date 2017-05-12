package com.karumi.shot.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class ShotTask extends DefaultTask {

  setGroup("shot")

}

class RecordScreenshotsTask extends ShotTask {

  setDescription("Records the user interface tests screenshots.")

  @TaskAction
  def recordScreenshots(): Unit = {
    println("---------> RECORD")
  }
}

class CompareScreenshotsTask extends ShotTask {

  setDescription("Compares the screenshots obtained from the tests execution and fails the tests execution if the screenshots don't match.")

  @TaskAction
  def compareScreenshots(): Unit = {
    println("---------> COMPARE")
  }
}

class PullScreenshotsTask extends ShotTask {

  setDescription("Retrieves the screenshots stored into the Android device where the tests were executed.")

  @TaskAction
  def pullScreenshots(): Unit = {
    println("---------> PULL")
  }
}

class ClearScreenshotsTask extends ShotTask {

  setDescription("Removes the screenshots recorded during the tests execution from the Android device where the tests were executed.")

  @TaskAction
  def clearScreenshots(): Unit = {
    println("---------> CLEAR")
  }
}