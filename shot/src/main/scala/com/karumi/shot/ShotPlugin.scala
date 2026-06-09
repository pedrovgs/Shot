package com.karumi.shot

import com.android.build.api.dsl.{ApplicationExtension, LibraryExtension}
import com.android.build.api.variant.{
  AndroidTest,
  ApplicationAndroidComponentsExtension,
  ApplicationVariant,
  HasAndroidTest,
  LibraryAndroidComponentsExtension,
  LibraryVariant,
  Variant
}
import com.karumi.shot.domain.Config
import com.karumi.shot.tasks.{
  DownloadScreenshotsTask,
  ExecuteScreenshotTests,
  ExecuteScreenshotTestsForEveryFlavor,
  RemoveScreenshotsTask,
  ShotTask
}
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.plugins.AppliedPlugin
import org.gradle.api.provider.Provider
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.{Action, Plugin, Project, Task}

import scala.util.Try

class ShotPlugin extends Plugin[Project] {

  override def apply(project: Project): Unit = {
    addExtensions(project)
    addAndroidTestDependency(project)
    // AGP 9 removed the legacy `AppExtension`/`applicationVariants` API that Shot used to enumerate
    // variants. Wire the screenshot tasks through the new AndroidComponents Variant API instead.
    // `onVariants` must be registered while the plugin is applied (not inside `afterEvaluate`), so we
    // react via `withId` to support the Android plugin being applied either before or after Shot.
    project.getPluginManager.withPlugin(
      "com.android.application",
      asAction[AppliedPlugin](_ => configureAppModule(project))
    )
    project.getPluginManager.withPlugin(
      "com.android.library",
      asAction[AppliedPlugin](_ => configureLibraryModule(project))
    )
  }

  private def configureAppModule(project: Project): Unit = {
    val androidComponents =
      project.getExtensions.getByType(classOf[ApplicationAndroidComponentsExtension])
    val baseTask = registerBaseTask(project)
    androidComponents.onVariants(
      androidComponents.selector().all(),
      asAction[ApplicationVariant](variant => addTaskToVariant(project, baseTask, variant))
    )
  }

  private def configureLibraryModule(project: Project): Unit = {
    val androidComponents =
      project.getExtensions.getByType(classOf[LibraryAndroidComponentsExtension])
    val baseTask = registerBaseTask(project)
    androidComponents.onVariants(
      androidComponents.selector().all(),
      asAction[LibraryVariant](variant => addTaskToVariant(project, baseTask, variant))
    )
  }

  private def registerBaseTask(
      project: Project
  ): TaskProvider[ExecuteScreenshotTestsForEveryFlavor] =
    project.getTasks.register(Config.defaultTaskName, classOf[ExecuteScreenshotTestsForEveryFlavor])

  private def addTaskToVariant(
      project: Project,
      baseTask: TaskProvider[ExecuteScreenshotTestsForEveryFlavor],
      variant: Variant
  ): Unit = {
    // Only wire screenshot tasks for variants that actually produce an instrumentation (androidTest)
    // APK. This mirrors the previous behaviour, which skipped variants whose `connected…AndroidTest`
    // task did not exist.
    androidTestOf(variant).foreach { androidTest =>
      val flavorName    = Option(variant.getFlavorName).filter(_.nonEmpty)
      val buildTypeName = Option(variant.getBuildType).getOrElse("")
      // `AndroidTest.applicationId` is already the test application id (defaults to
      // "<applicationId>.test" for app modules, and the configured testApplicationId for libraries).
      val appId        = androidTest.getApplicationId
      val orchestrated = isOrchestratorConnected(project)
      addTasksFor(project, flavorName, buildTypeName, appId, orchestrated, baseTask)
    }
  }

  private def androidTestOf(variant: Variant): Option[AndroidTest] =
    variant match {
      case hasAndroidTest: HasAndroidTest => Option(hasAndroidTest.getAndroidTest)
      case _                              => None
    }

  private def addExtensions(project: Project): Unit = {
    val name = ShotExtension.name
    project.getExtensions.add(name, new ShotExtension())
  }

  private def addTasksFor(
      project: Project,
      flavor: Option[String],
      buildTypeName: String,
      appId: Provider[String],
      orchestrated: Boolean,
      baseTask: TaskProvider[ExecuteScreenshotTestsForEveryFlavor]
  ): Unit = {
    val extension = project.getExtensions.getByType(classOf[ShotExtension])
    val instrumentationTaskName =
      if (extension.useComposer) Config.composerInstrumentationTestTask(flavor, buildTypeName)
      else Config.defaultInstrumentationTestTask(flavor, buildTypeName)
    val tasks   = project.getTasks
    val adbPath = findAdbPath(project)

    val removeScreenshotsAfterExecution = tasks.register(
      RemoveScreenshotsTask.name(flavor, buildTypeName, beforeExecution = false),
      classOf[RemoveScreenshotsTask]
    )
    val removeScreenshotsBeforeExecution = tasks.register(
      RemoveScreenshotsTask.name(flavor, buildTypeName, beforeExecution = true),
      classOf[RemoveScreenshotsTask]
    )
    val downloadScreenshots = tasks.register(
      DownloadScreenshotsTask.name(flavor, buildTypeName),
      classOf[DownloadScreenshotsTask]
    )
    val executeScreenshot = tasks.register(
      ExecuteScreenshotTests.name(flavor, buildTypeName),
      classOf[ExecuteScreenshotTests]
    )

    configureShotTask(
      removeScreenshotsAfterExecution,
      RemoveScreenshotsTask.description(flavor, buildTypeName),
      project,
      flavor,
      buildTypeName,
      appId,
      orchestrated,
      adbPath
    )
    configureShotTask(
      removeScreenshotsBeforeExecution,
      RemoveScreenshotsTask.description(flavor, buildTypeName),
      project,
      flavor,
      buildTypeName,
      appId,
      orchestrated,
      adbPath
    )
    configureShotTask(
      downloadScreenshots,
      DownloadScreenshotsTask.description(flavor, buildTypeName),
      project,
      flavor,
      buildTypeName,
      appId,
      orchestrated,
      adbPath
    )
    configureShotTask(
      executeScreenshot,
      ExecuteScreenshotTests.description(flavor, buildTypeName),
      project,
      flavor,
      buildTypeName,
      appId,
      orchestrated,
      adbPath
    )

    if (runInstrumentation(project, extension)) {
      executeScreenshot.configure(asAction[ExecuteScreenshotTests] { task =>
        task.dependsOn(downloadScreenshots)
        task.dependsOn(removeScreenshotsAfterExecution)
        // Depend on the instrumentation task by name: under the new Variant API it is registered
        // after `onVariants` runs, so it cannot be resolved eagerly here.
        task.dependsOn(instrumentationTaskName)
      })
      downloadScreenshots.configure(asAction[DownloadScreenshotsTask] { task =>
        task.mustRunAfter(instrumentationTaskName)
      })
      // Wire the instrumentation task lazily, only if/when it gets registered. Some build types do
      // not register a `connected…AndroidTest` task, in which case this simply never fires.
      tasks
        .matching(nameEquals(instrumentationTaskName))
        .configureEach(asAction[Task](task => task.dependsOn(removeScreenshotsBeforeExecution)))
      removeScreenshotsAfterExecution.configure(asAction[RemoveScreenshotsTask] { task =>
        task.mustRunAfter(downloadScreenshots)
      })
    }
    baseTask.configure(asAction[ExecuteScreenshotTestsForEveryFlavor] { task =>
      task.dependsOn(executeScreenshot)
    })
  }

  private def configureShotTask[T <: ShotTask](
      taskProvider: TaskProvider[T],
      description: String,
      project: Project,
      flavor: Option[String],
      buildTypeName: String,
      appId: Provider[String],
      orchestrated: Boolean,
      adbPath: String
  ): Unit =
    taskProvider.configure(asAction[T] { task =>
      task.setDescription(description)
      task.flavor = flavor
      task.buildTypeName = buildTypeName
      task.appId = appId.get()
      task.orchestrated = orchestrated
      task.projectPath = project.getProjectDir.getAbsolutePath
      task.buildPath = project.getBuildDir.getAbsolutePath
      task.shotExtension = project.getExtensions.findByType(classOf[ShotExtension])
      task.directorySuffix =
        if (project.hasProperty("directorySuffix"))
          Some(project.property("directorySuffix").toString)
        else None
      task.recordScreenshots = project.hasProperty("record")
      task.printBase64 = project.hasProperty("printBase64")
      task.projectName = project.getName
      task.adbPath = adbPath
    })

  private def findAdbPath(project: Project): String =
    AdbPathExtractor.extractPath(project)

  private def addAndroidTestDependency(project: Project): Unit = {
    val configs = project.getConfigurations
    val shotConfig = configs
      .create(Config.shotConfiguration)
    shotConfig.defaultDependencies((dependencies: DependencySet) => {
      val dependencyName  = Config.androidDependency
      val dependencyToAdd = project.getDependencies.create(dependencyName)
      dependencies.add(dependencyToAdd)
    })
    configs
      .named(Config.androidDependencyMode)
      .configure { config =>
        config.extendsFrom(shotConfig)
      }
  }

  private def runInstrumentation(project: Project, extension: ShotExtension): Boolean = {
    val property = project.findProperty("runInstrumentation").asInstanceOf[String]

    if (property != null) {
      if (Try(property.toBoolean).getOrElse(null) == null) {
        throw com.karumi.shot.exceptions.ShotException(
          "runInstrumentation value must be true|false"
        )
      }

      return property.toBoolean
    }

    extension.runInstrumentation
  }

  private def isOrchestratorConnected(project: Project): Boolean = {
    val orchestrator = "ANDROIDX_TEST_ORCHESTRATOR"
    val execution =
      if (project.getPlugins.hasPlugin("com.android.application"))
        Option(
          project.getExtensions
            .getByType(classOf[ApplicationExtension])
            .getTestOptions
            .getExecution
        )
      else if (project.getPlugins.hasPlugin("com.android.library"))
        Option(
          project.getExtensions
            .getByType(classOf[LibraryExtension])
            .getTestOptions
            .getExecution
        )
      else None
    execution.exists(_.equalsIgnoreCase(orchestrator))
  }

  private def asAction[T](f: T => Unit): Action[T] =
    new Action[T] {
      override def execute(value: T): Unit = f(value)
    }

  private def nameEquals(taskName: String): Spec[Task] =
    new Spec[Task] {
      override def isSatisfiedBy(task: Task): Boolean = task.getName == taskName
    }
}
