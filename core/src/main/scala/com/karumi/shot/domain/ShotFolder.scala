package com.karumi.shot.domain

import com.karumi.shot.domain.model.FilePath

case class ShotFolder(
    private val projectFolderPath: FilePath,
    private val buildFolderPath: FilePath,
    private val buildType: String,
    private val flavor: Option[String],
    private val directorySuffix: Option[String]
) {

  private def pathSuffix(): String = {
    s"${flavor.fold("") { s => s"$s/" }}" +
      s"$buildType/" +
      s"${directorySuffix.fold("") { s => s"$s/" }}"
  }

  def screenshotsFolder(): FilePath = {
    s"${projectFolderPath}/screenshots/" + pathSuffix()
  }

  def pulledScreenshotsFolder(): FilePath = {
    s"${screenshotsFolder()}screenshots-default/"
  }

  def pulledComposeScreenshotsFolder(): FilePath = {
    s"${screenshotsFolder()}screenshots-compose-default/"
  }

  def metadataFile(): FilePath = {
    pulledScreenshotsFolder() + s"metadata.xml"
  }

  def composeMetadataFile(): FilePath = {
    pulledComposeScreenshotsFolder() + s"metadata.json"
  }

  def reportFolder(): FilePath = {
    s"${buildFolderPath}/reports/shot/${pathSuffix()}"
  }

  def verificationReportFolder(): String = {
    s"${reportFolder()}verification/"
  }

  def recordingReportFolder(): String = {
    s"${reportFolder()}record/"
  }

  def screenshotsTemporalBuildPath(): FilePath = s"$buildFolderPath/tmp/shot/screenshot/"
}
