package com.karumi.shot

import com.karumi.shot.android.Adb
import com.karumi.shot.domain.Config
import com.karumi.shot.screenshots.{ScreenshotsComparator, ScreenshotsSaver}
import com.karumi.shot.tasks.{
  RemoveScreenshotsTask,
  ExecuteScreenshotTests,
  DownloadScreenshotsTask
}
import com.karumi.shot.ui.Console
import org.gradle.api.{Plugin, Project}

class ShotPlugin extends Plugin[Project] {

  private lazy val shot: Shot =
    new Shot(new Adb,
             new Files,
             new ScreenshotsComparator,
             new ScreenshotsSaver,
             new Console)

  override def apply(project: Project): Unit = {
    configureAdb(project)
    addAndroidTestDependency(project)
    addExtensions(project)
    project.afterEvaluate { project =>
      addTasks(project)
    }
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
    Option(project.getPlugins.findPlugin(Config.androidPluginName))
      .map(_ => dependenciesHandler.add(dependencyMode, dependency))
  }

  private def addTasks(project: Project): Unit = {
    project.getTasks
      .create(RemoveScreenshotsTask.name, classOf[RemoveScreenshotsTask])
    val pullScreenshots = project.getTasks
      .create(DownloadScreenshotsTask.name, classOf[DownloadScreenshotsTask])
    val executeScreenshot = project.getTasks
      .create(ExecuteScreenshotTests.name, classOf[ExecuteScreenshotTests])
    executeScreenshot.dependsOn(RemoveScreenshotsTask.name)
    val extension = project.getExtensions.getByType[ShotExtension](classOf[ShotExtension])
    val instrumentationTask = extension.getOptionInstrumentationTestTask
    val packageTask = extension.getOptionPackageTestApkTask
    (instrumentationTask, packageTask) match {
      case (Some(instTask), Some(packTask)) =>
        executeScreenshot.dependsOn(instTask)
        pullScreenshots.dependsOn(packTask)
      case _ =>
        executeScreenshot.dependsOn(Config.defaultInstrumentationTestTask)
        pullScreenshots.dependsOn(Config.defaultPackageTestApkTask)
    }

    executeScreenshot.dependsOn(DownloadScreenshotsTask.name)
  }

  private def addExtensions(project: Project): Unit = {
    val name = ShotExtension.name
    project.getExtensions.add(name, new ShotExtension())
  }

}
