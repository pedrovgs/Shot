package com.karumi.shot

import com.karumi.shot.tasks.{ClearScreenshotsTask, ExecuteScreenshotTests, PullScreenshotsTask}
import org.gradle.api.{Project, Task}
import org.gradle.testfixtures.ProjectBuilder
import org.scalatest.{FlatSpec, Matchers}

class ShotPluginSpec extends FlatSpec with Matchers {

  private val project: Project = {
    val project = ProjectBuilder.builder().build()
    project.getPluginManager.apply("shot")
    project
  }

  "ShotPlugin" should "have a task to clear the device screenshots" in {
    project.getTasks.findByName(ClearScreenshotsTask.name) shouldBe a[ClearScreenshotsTask]
  }

  it should "have a task to pull the screenshots" in {
    project.getTasks.findByName(PullScreenshotsTask.name) shouldBe a[PullScreenshotsTask]
  }

  it should "have a task to execute screenshot tests" in {
    project.getTasks.findByName(ExecuteScreenshotTests.name) shouldBe a[ExecuteScreenshotTests]
  }

  it should "use the documented task names" in {
    ClearScreenshotsTask.name shouldBe "clearScreenshots"
    PullScreenshotsTask.name shouldBe "pullScreenshots"
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

}
