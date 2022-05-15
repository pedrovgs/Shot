package com.karumi.shot

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.{AppExtension, LibraryExtension}
import com.android.builder.model.{BuildType, ProductFlavor}
import com.karumi.shot.domain.Config
import com.karumi.shot.exceptions.ShotException
import com.karumi.shot.tasks.{
  DownloadScreenshotsTask,
  ExecuteScreenshotTests,
  ExecuteScreenshotTestsForEveryFlavor,
  RemoveScreenshotsTask
}
import com.karumi.shot.ui.Console
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.{Plugin, Project}

import scala.util.Try
class ShotPlugin extends Plugin[Project] {

  private val console = new Console

  override def apply(project: Project): Unit = {
    addExtensions(project)
    addAndroidTestDependency(project)
    project.afterEvaluate { project =>
      {
        addTasks(project)
      }
    }
  }

  private def configureAdb(project: Project): Unit = {
    val adbPath = AdbPathExtractor.extractPath(project)
  }

  private def findAdbPath(project: Project): String = {
    AdbPathExtractor.extractPath(project)
  }

  private def addTasks(project: Project): Unit = {
    if (isAnAndroidProject(project)) {
      addTasksToAppModule(project)
    } else if (isAnAndroidLibrary(project)) {
      addTasksToLibraryModule(project)
    }
  }

  private def addTasksToLibraryModule(project: Project) = {
    val libraryExtension =
      getAndroidLibraryExtension(project)
    val baseTask =
      project.getTasks
        .register(Config.defaultTaskName, classOf[ExecuteScreenshotTestsForEveryFlavor])
    libraryExtension.getLibraryVariants.all { variant =>
      addTaskToVariant(project, baseTask, variant)
    }
  }

  private def addTasksToAppModule(project: Project) = {
    val appExtension =
      getAndroidAppExtension(project)
    val baseTask =
      project.getTasks
        .register(Config.defaultTaskName, classOf[ExecuteScreenshotTestsForEveryFlavor])
    appExtension.getApplicationVariants.all { variant =>
      addTaskToVariant(project, baseTask, variant)
    }
  }

  private def addTaskToVariant(
      project: Project,
      baseTask: TaskProvider[ExecuteScreenshotTestsForEveryFlavor],
      variant: BaseVariant
  ) = {
    val flavor = variant.getMergedFlavor
    checkIfApplicationIdIsConfigured(project, flavor)
    val completeAppId = composeCompleteAppId(project, variant)
    val appTestId     = Option(flavor.getTestApplicationId).getOrElse(completeAppId)
    val flavorName    = if (variant.getFlavorName.nonEmpty) Some(variant.getFlavorName) else None
    val orchestrated  = isOrchestratorConnected(project)

    addTasksFor(project, flavorName, variant.getBuildType, appTestId, orchestrated, baseTask)
  }

  private def composeCompleteAppId(project: Project, variant: BaseVariant): String = {
    val appId =
      try {
        variant.getApplicationId
      } catch {
        case _: Throwable =>
          console.showWarning(
            "Error found trying to get applicationId from library module. We will use the extension applicationId param as a workaround."
          )
          console.showWarning(
            "More information about this AGP7.0.1 bug can be found here: https://github.com/Karumi/Shot/issues/247"
          )
          val extension           = project.getExtensions.getByType[ShotExtension](classOf[ShotExtension])
          val extensionAppIdValue = extension.applicationId
          console.showWarning(s"Extension applicationId value read = $extensionAppIdValue")
          extensionAppIdValue
      }
    appId + ".test"
  }

  private def checkIfApplicationIdIsConfigured(project: Project, flavor: ProductFlavor) =
    if (isAnAndroidLibrary(project) && flavor.getTestApplicationId == null) {
      throw ShotException(
        "Your Android library needs to be configured using an testApplicationId in your build.gradle defaultConfig block."
      )
    }

  private def addExtensions(project: Project): Unit = {
    val name = ShotExtension.name
    project.getExtensions.add(name, new ShotExtension())
  }

  private def addTasksFor(
      project: Project,
      flavor: Option[String],
      buildType: BuildType,
      appId: String,
      orchestrated: Boolean,
      baseTask: TaskProvider[ExecuteScreenshotTestsForEveryFlavor]
  ): Unit = {
    val extension = project.getExtensions.getByType[ShotExtension](classOf[ShotExtension])
    val instrumentationTaskName = if (extension.useComposer) {
      Config.composerInstrumentationTestTask(flavor, buildType.getName)
    } else {
      Config.defaultInstrumentationTestTask(flavor, buildType.getName)
    }
    val tasks = project.getTasks
    // Some projects configure different build types and only one of them is allowed to run instrumentation tasks
    // Based on this, we need to first check if the instrumentation task is available or not. This let us use Shot
    // for different build types even if it is not the default one
    val instrumentationTaskProvider =
      try {
        tasks.named(instrumentationTaskName)
      } catch {
        case e: Throwable => return
      }

    val removeScreenshotsAfterExecution = tasks
      .register(
        RemoveScreenshotsTask.name(flavor, buildType, beforeExecution = false),
        classOf[RemoveScreenshotsTask]
      )
    val removeScreenshotsBeforeExecution = tasks
      .register(
        RemoveScreenshotsTask.name(flavor, buildType, beforeExecution = true),
        classOf[RemoveScreenshotsTask]
      )
    val adbPath = findAdbPath(project)
    removeScreenshotsAfterExecution.configure { task =>
      task.setDescription(RemoveScreenshotsTask.description(flavor, buildType))
      task.flavor = flavor
      task.buildTypeName = buildType.getName
      task.appId = appId
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
    }
    removeScreenshotsBeforeExecution.configure { task =>
      task.setDescription(RemoveScreenshotsTask.description(flavor, buildType))
      task.flavor = flavor
      task.buildTypeName = buildType.getName
      task.appId = appId
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
    }

    val downloadScreenshots = tasks
      .register(DownloadScreenshotsTask.name(flavor, buildType), classOf[DownloadScreenshotsTask])
    downloadScreenshots.configure { task =>
      task.setDescription(DownloadScreenshotsTask.description(flavor, buildType))
      task.flavor = flavor
      task.buildTypeName = buildType.getName
      task.appId = appId
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
    }
    val executeScreenshot = tasks
      .register(ExecuteScreenshotTests.name(flavor, buildType), classOf[ExecuteScreenshotTests])
    executeScreenshot.configure { task =>
      task.setDescription(ExecuteScreenshotTests.description(flavor, buildType))
      task.flavor = flavor
      task.buildTypeName = buildType.getName
      task.appId = appId
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
    }

    if (runInstrumentation(project, extension)) {
      executeScreenshot.configure { task =>
        task.dependsOn(instrumentationTaskProvider)
        task.dependsOn(downloadScreenshots)
        task.dependsOn(removeScreenshotsAfterExecution)
      }

      downloadScreenshots.configure { task =>
        task.mustRunAfter(instrumentationTaskProvider)
      }
      instrumentationTaskProvider.configure { task =>
        task.dependsOn(removeScreenshotsBeforeExecution)
      }
      removeScreenshotsAfterExecution.configure { task =>
        task.mustRunAfter(downloadScreenshots)
      }
    }
    baseTask.configure { task =>
      task.dependsOn(executeScreenshot)
    }
  }

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
        throw ShotException("runInstrumentation value must be true|false")
      }

      return property.toBoolean
    }

    extension.runInstrumentation
  }

  private def isAnAndroidLibrary(project: Project): Boolean =
    try {
      getAndroidLibraryExtension(project)
      true
    } catch {
      case _: Throwable => false
    }

  private def isAnAndroidProject(project: Project): Boolean =
    try {
      getAndroidAppExtension(project)
      true
    } catch {
      case _: Throwable => false
    }

  private def getAndroidLibraryExtension(project: Project) = {
    project.getExtensions
      .getByType[LibraryExtension](classOf[LibraryExtension])
  }

  private def getAndroidAppExtension(project: Project) = {
    project.getExtensions.getByType[AppExtension](classOf[AppExtension])
  }

  private def isOrchestratorConnected(project: Project) = {
    val orchestrator = "ANDROIDX_TEST_ORCHESTRATOR"
    if (isAnAndroidProject(project)) {
      getAndroidAppExtension(project).getTestOptions.getExecution.equalsIgnoreCase(orchestrator)
    } else if (isAnAndroidLibrary(project)) {
      getAndroidLibraryExtension(project).getTestOptions.getExecution.equalsIgnoreCase(orchestrator)
    } else {
      false
    }
  }
}
