package com.karumi.shot.domain

import com.karumi.shot.domain.model.FilePath

case class ShotFolder(
    private val projectFolderPath: FilePath,
    private val buildFolderPath: FilePath,
    private val buildType: String,
    private val flavor: Option[String],
    private val directorySuffix: Option[String],
    private val separator: String,
    private val orchestrated: Boolean
) {

  private val orchestratedSuffix = if (orchestrated) "-orchestrated" else ""

  private def pathSuffix(): String = {
    s"${flavor.fold("") { s => s"$s$separator" }}" +
      s"$buildType$separator" +
      s"${directorySuffix.fold("") { s => s"$s$separator" }}"
  }

  def screenshotsFolder(): FilePath = {
    s"${projectFolderPath}${separator}screenshots$separator" + pathSuffix()
  }

  def pulledScreenshotsFolder(): FilePath = {
    s"${screenshotsFolder()}screenshots-default$orchestratedSuffix$separator"
  }

  def pulledComposeScreenshotsFolder(): FilePath = {
    s"${screenshotsFolder()}screenshots-compose-default$separator"
  }

  def pulledComposeOrchestratedScreenshotsFolder(): FilePath = {
    s"${screenshotsFolder()}screenshots-compose-default$orchestratedSuffix$separator"
  }

  def metadataFile(): FilePath = {
    pulledScreenshotsFolder() + s"metadata.json"
  }

  def metadataFileName(): FilePath = {
    "metadata.json"
  }

  def composeMetadataFile(): FilePath = {
    pulledComposeScreenshotsFolder() + composeMetadataFileName()
  }

  def composeMetadataFileName(): FilePath = {
    "metadata_compose.json"
  }

  def reportFolder(): FilePath = {
    s"${buildFolderPath}${separator}reports${separator}shot$separator${pathSuffix()}"
  }

  def verificationReportFolder(): String = {
    s"${reportFolder()}verification$separator"
  }

  def recordingReportFolder(): String = {
    s"${reportFolder()}record$separator"
  }

  def screenshotsTemporalBuildPath(): FilePath =
    s"$buildFolderPath${separator}tmp${separator}shot${separator}screenshot$separator"
}
