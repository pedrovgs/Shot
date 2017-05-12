package com.karumi.shot

import com.karumi.shot.free.domain.Config
import com.karumi.shot.tasks.{ClearScreenshotsTask, PullScreenshotsTask, ExecuteScreenshotTests}
import org.gradle.api.{Plugin, Project}

class ShotPlugin extends Plugin[Project] {

  override def apply(project: Project): Unit = {
    addAndroidTestDependency(project)
    addTasks(project)
  }

  private def addAndroidTestDependency(project: Project): Unit = {
    val dependencyMode = Config.androidDependencyMode
    val dependencyName = Config.androidDependency
    val dependency = project.getDependencies.create(dependencyName)
    project.getDependencies.add(dependencyMode, dependency)
  }

  private def addTasks(project: Project): Unit = {
    project.getTasks.create(ClearScreenshotsTask.name, classOf[ClearScreenshotsTask])
    val pullScreenshots = project.getTasks.create(PullScreenshotsTask.name, classOf[PullScreenshotsTask])
    val executeScreenshot = project.getTasks.create(ExecuteScreenshotTests.name, classOf[ExecuteScreenshotTests])
    executeScreenshot.dependsOn(ClearScreenshotsTask.name)
    executeScreenshot.dependsOn("connectedAndroidTest")
    executeScreenshot.dependsOn(PullScreenshotsTask.name)
    pullScreenshots.dependsOn("packageDebugAndroidTest")
  }

}
