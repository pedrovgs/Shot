package com.karumi.shot

import com.android.builder.model.{BuildType, ProductFlavor}
import com.android.build.gradle.AppExtension
import com.karumi.shot.android.Adb
import com.karumi.shot.base64.Base64Encoder
import com.karumi.shot.domain.Config
import com.karumi.shot.reports.{ConsoleReporter, ExecutionReporter}
import com.karumi.shot.screenshots.{ScreenshotsComparator, ScreenshotsDiffGenerator, ScreenshotsSaver}
import com.karumi.shot.tasks.{DownloadScreenshotsTask, ExecuteScreenshotTests, RemoveScreenshotsTask, ShotTask}
import com.karumi.shot.ui.Console
import org.gradle.api.artifacts.{Dependency, DependencyResolutionListener, ResolvableDependencies}
import org.gradle.api.{Plugin, Project}

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
    project.afterEvaluate { project => {
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
    val appExtension = project.getExtensions.getByType[AppExtension](classOf[AppExtension])
    appExtension.getApplicationVariants.all { variant =>
      val flavor = variant.getMergedFlavor
      val completeAppId = flavor.getApplicationId + flavor.getApplicationIdSuffix
      addTasksFor(project, variant.getFlavorName, variant.getBuildType, completeAppId)
    }
  }

  private def addExtensions(project: Project): Unit = {
    val name = ShotExtension.name
    project.getExtensions.add(name, new ShotExtension())
  }

  private def addTasksFor(project: Project, flavor: String, buildType: BuildType, appId: String): Unit = {
    val instrumentationTask = Config.defaultInstrumentationTestTask(flavor, buildType.getName)
    println("======= Adding tasks for")
    println(s"================ ${flavor}")
    println(s"================ ${buildType.getName}")
    println(s"================ ${appId}")
    println(s"instrumentation task selected = ${instrumentationTask}")
    println("================")
    val extension =
      project.getExtensions.getByType[ShotExtension](classOf[ShotExtension])
    val tasks = project.getTasks
    val removeScreenshots = tasks
      .create(RemoveScreenshotsTask.name(flavor, buildType), classOf[RemoveScreenshotsTask]).asInstanceOf[ShotTask]
    removeScreenshots.setDescription(RemoveScreenshotsTask.description(flavor, buildType))
    removeScreenshots.flavor = flavor
    removeScreenshots.buildType = buildType
    removeScreenshots.appId = appId
    val downloadScreenshots = tasks
      .create(DownloadScreenshotsTask.name(flavor, buildType), classOf[DownloadScreenshotsTask])
    downloadScreenshots.setDescription(DownloadScreenshotsTask.description(flavor, buildType))
    downloadScreenshots.flavor = flavor
    downloadScreenshots.buildType = buildType
    downloadScreenshots.appId = appId
    val executeScreenshot = tasks
      .create(ExecuteScreenshotTests.name(flavor, buildType), classOf[ExecuteScreenshotTests])
    executeScreenshot.setDescription(ExecuteScreenshotTests.description(flavor, buildType))
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
  }

  private def addAndroidTestDependency(project: Project): Unit = {

    project.getGradle.addListener(new DependencyResolutionListener() {

      override def beforeResolve(
                                  resolvableDependencies: ResolvableDependencies): Unit = {
        var facebookDependencyHasBeenAdded = false

        project.getConfigurations.forEach(config => {
          facebookDependencyHasBeenAdded |= config.getAllDependencies
            .toArray(new Array[Dependency](0))
            .exists(
              dependency =>
                Config.androidDependencyGroup == dependency.getGroup
                  && Config.androidDependencyName == dependency.getName
                  && Config.androidDependencyVersion == dependency.getVersion)
        })

        if (!facebookDependencyHasBeenAdded) {
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
