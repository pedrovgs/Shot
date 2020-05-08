package com.karumi.shot

import com.android.builder.model.{BuildType, ProductFlavor}
import com.android.build.gradle.AppExtension
import com.karumi.shot.android.Adb
import com.karumi.shot.base64.Base64Encoder
import com.karumi.shot.domain.Config
import com.karumi.shot.reports.{ConsoleReporter, ExecutionReporter}
import com.karumi.shot.screenshots.{
  ScreenshotsComparator,
  ScreenshotsDiffGenerator,
  ScreenshotsSaver
}
import com.karumi.shot.tasks.{
  DownloadScreenshotsTask,
  ExecuteScreenshotTests,
  ExecuteScreenshotTestsForEveryFlavor,
  RemoveScreenshotsTask,
  ShotTask
}
import com.karumi.shot.ui.Console
import org.gradle.api.artifacts.{
  Dependency,
  DependencyResolutionListener,
  ResolvableDependencies
}
import org.gradle.api.{Plugin, Project, Task}

class ShotPlugin extends Plugin[Project] {

  private val console = new Console
  private lazy val shot: Shot =
    new Shot(
      new Adb,
      new Files,
      new ScreenshotsComparator,
      new ScreenshotsDiffGenerator(new Base64Encoder),
      new ScreenshotsSaver,
      console,
      new ExecutionReporter,
      new ConsoleReporter(console)
    )

  override def apply(project: Project): Unit = {
    addExtensions(project)
    addAndroidTestDependency(project)
    project.afterEvaluate { project =>
      {
        configureAdb(project)
        addTasks(project)
      }
    }
  }

  private def configureAdb(project: Project): Unit = {
    val adbPath = AdbPathExtractor.extractPath(project)
    shot.configureAdbPath(adbPath)
  }

  private def addTasks(project: Project): Unit = {
    val appExtension =
      project.getExtensions.getByType[AppExtension](classOf[AppExtension])
    val baseTask = project.getTasks.create(
      Config.defaultTaskName,
      classOf[ExecuteScreenshotTestsForEveryFlavor])
    appExtension.getApplicationVariants.all { variant =>
      val flavor = variant.getMergedFlavor
      val completeAppId = flavor.getApplicationId + Option(
        flavor.getApplicationIdSuffix).getOrElse("") +
        Option(variant.getBuildType.getApplicationIdSuffix).getOrElse("")
      if (variant.getBuildType.getName != "release") {
        addTasksFor(project,
                    variant.getFlavorName,
                    variant.getBuildType,
                    completeAppId,
                    baseTask)
      }
    }
  }

  private def addExtensions(project: Project): Unit = {
    val name = ShotExtension.name
    project.getExtensions.add(name, new ShotExtension())
  }

  private def addTasksFor(project: Project,
                          flavor: String,
                          buildType: BuildType,
                          appId: String,
                          baseTask: Task): Unit = {
    val extension =
      project.getExtensions.getByType[ShotExtension](classOf[ShotExtension])
    val instrumentationTask = if (extension.useComposer) {
      Config.composerInstrumentationTestTask(flavor, buildType.getName)
    } else {
      Config.defaultInstrumentationTestTask(flavor, buildType.getName)
    }
    val tasks = project.getTasks
    val removeScreenshots = tasks
      .create(RemoveScreenshotsTask.name(flavor, buildType),
              classOf[RemoveScreenshotsTask])
      .asInstanceOf[ShotTask]
    removeScreenshots.setDescription(
      RemoveScreenshotsTask.description(flavor, buildType))
    removeScreenshots.flavor = flavor
    removeScreenshots.buildType = buildType
    removeScreenshots.appId = appId
    val downloadScreenshots = tasks
      .create(DownloadScreenshotsTask.name(flavor, buildType),
              classOf[DownloadScreenshotsTask])
    downloadScreenshots.setDescription(
      DownloadScreenshotsTask.description(flavor, buildType))
    downloadScreenshots.flavor = flavor
    downloadScreenshots.buildType = buildType
    downloadScreenshots.appId = appId
    val executeScreenshot = tasks
      .create(ExecuteScreenshotTests.name(flavor, buildType),
              classOf[ExecuteScreenshotTests])
    executeScreenshot.setDescription(
      ExecuteScreenshotTests.description(flavor, buildType))
    executeScreenshot.flavor = flavor
    executeScreenshot.buildType = buildType
    executeScreenshot.appId = appId
    if (extension.runInstrumentation) {
      executeScreenshot.dependsOn(instrumentationTask)
      executeScreenshot.dependsOn(downloadScreenshots)
      executeScreenshot.dependsOn(removeScreenshots)
      downloadScreenshots.mustRunAfter(instrumentationTask)
      removeScreenshots.mustRunAfter(downloadScreenshots)
    }
    baseTask.dependsOn(executeScreenshot)
  }

  private def addAndroidTestDependency(project: Project): Unit = {

    project.getGradle.addListener(new DependencyResolutionListener() {

      override def beforeResolve(
          resolvableDependencies: ResolvableDependencies): Unit = {
        var shotAndroidDependencyHasBeenAdded = false

        project.getConfigurations.forEach(config => {
          shotAndroidDependencyHasBeenAdded |= config.getAllDependencies
            .toArray(new Array[Dependency](0))
            .exists(dependency =>
              Config.androidDependencyGroup == dependency.getGroup
                && Config.androidDependencyName == dependency.getName)
        })

        if (!shotAndroidDependencyHasBeenAdded) {
          val dependencyMode = Config.androidDependencyMode
          val dependencyName = Config.androidDependency
          val dependenciesHandler = project.getDependencies

          val dependencyToAdd = dependenciesHandler.create(dependencyName)
          Option(project.getPlugins.findPlugin(Config.androidPluginName))
            .map(_ =>
              project.getDependencies.add(dependencyMode, dependencyToAdd))
          project.getGradle.removeListener(this)
        }
      }

      override def afterResolve(
          resolvableDependencies: ResolvableDependencies): Unit = {}
    })
  }
}
