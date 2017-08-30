package com.karumi.shot

import com.karumi.shot.tasks.{
  RemoveScreenshotsTask,
  ExecuteScreenshotTests,
  DownloadScreenshotsTask
}
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.scalatest.{FlatSpec, Matchers}

class ShotPluginSpec extends FlatSpec with Matchers {

  private val project: Project = {
    val project = ProjectBuilder.builder().build()
    project.getPluginManager.apply("shot")
    project
  }

  "ShotPlugin" should "have a task to clear the device screenshots" in {
    project.getTasks.findByName(RemoveScreenshotsTask.name) shouldBe a[
      RemoveScreenshotsTask]
  }

  it should "have a task to pull the screenshots" in {
    project.getTasks.findByName(DownloadScreenshotsTask.name) shouldBe a[
      DownloadScreenshotsTask]
  }

  it should "have a task to execute screenshot tests" in {
    project.getTasks.findByName(ExecuteScreenshotTests.name) shouldBe a[
      ExecuteScreenshotTests]
  }

  it should "use the documented task names" in {
    RemoveScreenshotsTask.name shouldBe "removeScreenshots"
    DownloadScreenshotsTask.name shouldBe "downloadScreenshots"
    ExecuteScreenshotTests.name shouldBe "executeScreenshotTests"
  }

  it should "configure executeScreenshotTests depending on connectedAndroidTest task" in {
    val task = project.getTasks.findByName(ExecuteScreenshotTests.name)
    task.getDependsOn.contains("connectedAndroidTest")
  }

  it should "configure executeScreenshotTests depending on clearScreenshots task" in {
    val task = project.getTasks.findByName(ExecuteScreenshotTests.name)
    task.getDependsOn.contains("clearScreenshots")
  }

  it should "configure executeScreenshotTests depending on pullScreenshots task" in {
    val task = project.getTasks.findByName(ExecuteScreenshotTests.name)
    task.getDependsOn.contains("pullScreenshots")
  }

  it should "configure pullScreenshots depending on packageDebugAndroidTest task" in {
    val task = project.getTasks.findByName(DownloadScreenshotsTask.name)
    task.getDependsOn.contains("packageDebugAndroidTest")
  }

  it should "configure an extension named ShotExtension" in {
    project.getExtensions.findByName(ShotExtension.name) shouldBe a[
      ShotExtension]
  }

}
