package com.karumi.shot.tasks

import com.android.builder.model.BuildType
import com.karumi.shot.android.Adb
import com.karumi.shot.base64.Base64Encoder
import com.karumi.shot.domain.ShotFolder
import com.karumi.shot.reports.{ConsoleReporter, ExecutionReporter}
import com.karumi.shot.screenshots.{
  ScreenshotsComparator,
  ScreenshotsDiffGenerator,
  ScreenshotsSaver
}
import com.karumi.shot.system.EnvVars
import com.karumi.shot.ui.Console
import com.karumi.shot.{Files, Shot, ShotExtension}
import org.gradle.api.tasks.{Input, TaskAction}
import org.gradle.api.{DefaultTask, GradleException}

import java.io.File

abstract class ShotTask extends DefaultTask {
  @Input var appId: String                   = _
  @Input var flavor: Option[String]          = _
  @Input var buildTypeName: String           = _
  @Input var orchestrated: Boolean           = false
  @Input var projectPath: String             = _
  @Input var buildPath: String               = _
  @Input var shotExtension: ShotExtension    = _
  @Input var directorySuffix: Option[String] = _
  @Input var recordScreenshots: Boolean      = _
  @Input var printBase64: Boolean            = _
  @Input var projectName: String             = _
  @Input var adbPath: String                 = _
  protected def shot: Shot = {
    val console = new Console
    new Shot(
      new Adb(adbPath),
      new Files,
      new ScreenshotsComparator,
      new ScreenshotsDiffGenerator(new Base64Encoder),
      new ScreenshotsSaver,
      console,
      new ExecutionReporter,
      new ConsoleReporter(console),
      new EnvVars()
    )
  }

  protected def shotFolder: ShotFolder = {
    ShotFolder(
      projectPath,
      buildPath,
      buildTypeName,
      flavor,
      directorySuffix,
      File.separator,
      orchestrated
    )
  }

  setGroup("shot")

}

object ShotTask {
  def prefixName(flavor: Option[String], buildType: BuildType) =
    s"${flavor.fold(buildType.getName) { s =>
      s"$s${buildType.getName.capitalize}"
    }}"
}

object ExecuteScreenshotTests {
  def name(flavor: Option[String], buildType: BuildType) =
    s"${ShotTask.prefixName(flavor, buildType)}ExecuteScreenshotTests"

  def description(flavor: Option[String], buildType: BuildType) =
    s"Checks the user interface screenshot tests . If you execute this task using -Precord param the screenshot will be regenerated for the build " +
      s"${ShotTask.prefixName(flavor, buildType)}"
}

class ExecuteScreenshotTests extends ShotTask {

  @TaskAction
  def executeScreenshotTests(): Unit = {
    val tolerance                     = shotExtension.tolerance
    val showOnlyFailingTestsInReports = shotExtension.showOnlyFailingTestsInReports
    if (recordScreenshots) {
      shot.recordScreenshots(appId, shotFolder, orchestrated)
    } else {
      val result = shot.verifyScreenshots(
        appId,
        shotFolder,
        projectName,
        printBase64,
        tolerance,
        showOnlyFailingTestsInReports,
        orchestrated
      )
      if (result.hasErrors) {
        throw new GradleException(
          "Screenshots comparision fail. Review the execution report to see what's broken your build."
        )
      }
    }
  }
}

object DownloadScreenshotsTask {
  def name(flavor: Option[String], buildType: BuildType) =
    s"${ShotTask.prefixName(flavor, buildType)}DownloadScreenshots"

  def description(flavor: Option[String], buildType: BuildType) =
    s"Retrieves the screenshots stored into the Android device where the tests were executed for the build " +
      s"${ShotTask.prefixName(flavor, buildType)}"
}

class DownloadScreenshotsTask extends ShotTask {
  @TaskAction
  def downloadScreenshots(): Unit = {
    shot.downloadScreenshots(appId, shotFolder, orchestrated)
  }
}

object RemoveScreenshotsTask {
  def name(flavor: Option[String], buildType: BuildType, beforeExecution: Boolean) =
    s"${ShotTask.prefixName(flavor, buildType)}RemoveScreenshots" +
      s"${if (beforeExecution) "Before" else "After"}"

  def description(flavor: Option[String], buildType: BuildType) =
    s"Removes the screenshots recorded during the tests execution from the Android device where the tests were executed for the build " +
      s"${ShotTask.prefixName(flavor, buildType)}"
}

class RemoveScreenshotsTask extends ShotTask {
  @TaskAction
  def clearScreenshots(): Unit =
    shot.removeScreenshots(appId, orchestrated)
}
object ExecuteScreenshotTestsForEveryFlavor {
  val name: String = "executeScreenshotTests"
}
class ExecuteScreenshotTestsForEveryFlavor extends ShotTask {
  setDescription(
    "Checks the user interface screenshot tests. If you execute this task using -Precord param the screenshot will be regenerated."
  )
}
