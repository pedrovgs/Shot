package com.karumi.shot

import com.karumi.shot.android.Adb
import com.karumi.shot.domain.Config
import com.karumi.shot.tasks.{
  ClearScreenshotsTask,
  ExecuteScreenshotTests,
  PullScreenshotsTask
}
import org.gradle.api.{Plugin, Project}

class ShotPlugin extends Plugin[Project] {

  private lazy val shot: Shot = new Shot(new Adb)

  override def apply(project: Project): Unit = {
    configureAdb(project)
    addAndroidTestDependency(project)
    addExtensions(project)
    addTasks(project)
  }

  private def configureAdb(project: Project): Unit = {
    val adbPath = AdbPathExtractor.extractPath(project)
    shot.configureAdbPath(adbPath)
  }

  private def addAndroidTestDependency(project: Project): Unit = {
    val dependencyMode = Config.androidDependencyMode
    val dependencyName = Config.androidDependency
    val dependenciesHandler = project.getDependencies
    val dependency = dependenciesHandler.create(dependencyName)
    Option(project.getPlugins.findPlugin("com.android.application"))
      .map(_ => dependenciesHandler.add(dependencyMode, dependency))
  }

  private def addTasks(project: Project): Unit = {
    project.getTasks
      .create(ClearScreenshotsTask.name, classOf[ClearScreenshotsTask])
    val pullScreenshots = project.getTasks
      .create(PullScreenshotsTask.name, classOf[PullScreenshotsTask])
    val executeScreenshot = project.getTasks
      .create(ExecuteScreenshotTests.name, classOf[ExecuteScreenshotTests])
    executeScreenshot.dependsOn(ClearScreenshotsTask.name)
    executeScreenshot.dependsOn("connectedAndroidTest")
    executeScreenshot.dependsOn(PullScreenshotsTask.name)
    pullScreenshots.dependsOn("packageDebugAndroidTest")
  }

  private def addExtensions(project: Project): Unit = {
    val name = ShotExtension.name
    project.getExtensions.add(name, new ShotExtension())
  }

}
