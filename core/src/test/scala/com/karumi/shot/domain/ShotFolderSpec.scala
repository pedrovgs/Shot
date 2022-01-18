package com.karumi.shot.domain

import org.scalatest.funspec._
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class ShotFolderSpec extends AnyFunSpec {

  describe("Shot folder") {
    val shotFolder = ShotFolder("shot", "shot/build", "debug", None, None, "/", false)

    it("should have screenshots folder") {
      shotFolder.screenshotsFolder() shouldBe s"shot/screenshots/debug/"
      shotFolder.pulledScreenshotsFolder() shouldBe s"shot/screenshots/debug/screenshots-default/"
      shotFolder
        .pulledComposeScreenshotsFolder() shouldBe s"shot/screenshots/debug/screenshots-compose-default/"
      shotFolder
        .pulledComposeOrchestratedScreenshotsFolder() shouldBe s"shot/screenshots/debug/screenshots-compose-default/"
    }

    it("should have metadata file") {
      shotFolder.metadataFile() shouldBe s"shot/screenshots/debug/screenshots-default/metadata.json"
      shotFolder
        .composeMetadataFile() shouldBe s"shot/screenshots/debug/screenshots-compose-default/metadata_compose.json"
      shotFolder.composeMetadataFileName() shouldBe "metadata_compose.json"
      shotFolder.metadataFileName() shouldBe "metadata.json"
    }

    it("should have a report folder") {
      shotFolder.reportFolder() shouldBe s"shot/build/reports/shot/debug/"
      shotFolder.verificationReportFolder() shouldBe s"shot/build/reports/shot/debug/verification/"
      shotFolder.recordingReportFolder() shouldBe s"shot/build/reports/shot/debug/record/"
    }

    it("should have a temporary folder path") {
      shotFolder.screenshotsTemporalBuildPath() shouldBe "shot/build/tmp/shot/screenshot/"
    }
  }

  describe("Product Flavor") {
    val shotFolder = ShotFolder("shot", "shot/build", "debug", Some("green"), None, "/", false)

    it("should have screenshots folder") {
      shotFolder.screenshotsFolder() shouldBe s"shot/screenshots/green/debug/"
      shotFolder
        .pulledScreenshotsFolder() shouldBe s"shot/screenshots/green/debug/screenshots-default/"
      shotFolder
        .pulledComposeScreenshotsFolder() shouldBe s"shot/screenshots/green/debug/screenshots-compose-default/"
    }

    it("should have metadata file") {
      shotFolder
        .metadataFile() shouldBe s"shot/screenshots/green/debug/screenshots-default/metadata.json"
      shotFolder
        .composeMetadataFile() shouldBe s"shot/screenshots/green/debug/screenshots-compose-default/metadata_compose.json"
    }

    it("should have a report folder") {
      shotFolder.reportFolder() shouldBe s"shot/build/reports/shot/green/debug/"
      shotFolder
        .verificationReportFolder() shouldBe s"shot/build/reports/shot/green/debug/verification/"
      shotFolder.recordingReportFolder() shouldBe s"shot/build/reports/shot/green/debug/record/"
    }
  }

  describe("Directory Suffix") {
    val shotFolder = ShotFolder("shot", "shot/build", "debug", None, Some("Api26"), "/", false)

    it("should have screenshots folder") {
      shotFolder.screenshotsFolder() shouldBe s"shot/screenshots/debug/Api26/"
      shotFolder
        .pulledScreenshotsFolder() shouldBe s"shot/screenshots/debug/Api26/screenshots-default/"
      shotFolder
        .pulledComposeScreenshotsFolder() shouldBe s"shot/screenshots/debug/Api26/screenshots-compose-default/"
    }

    it("should have metadata file") {
      shotFolder
        .metadataFile() shouldBe s"shot/screenshots/debug/Api26/screenshots-default/metadata.json"
      shotFolder
        .composeMetadataFile() shouldBe s"shot/screenshots/debug/Api26/screenshots-compose-default/metadata_compose.json"
    }

    it("should have a report folder") {
      shotFolder.reportFolder() shouldBe s"shot/build/reports/shot/debug/Api26/"
      shotFolder
        .verificationReportFolder() shouldBe s"shot/build/reports/shot/debug/Api26/verification/"
      shotFolder.recordingReportFolder() shouldBe s"shot/build/reports/shot/debug/Api26/record/"
    }
  }

  describe("Product Flavor & Directory Suffix") {
    val shotFolder =
      ShotFolder("shot", "shot/build", "debug", Some("green"), Some("Api26"), "/", false)

    it("should have screenshots folder") {
      shotFolder.screenshotsFolder() shouldBe s"shot/screenshots/green/debug/Api26/"
      shotFolder
        .pulledScreenshotsFolder() shouldBe s"shot/screenshots/green/debug/Api26/screenshots-default/"
      shotFolder
        .pulledComposeScreenshotsFolder() shouldBe s"shot/screenshots/green/debug/Api26/screenshots-compose-default/"
    }

    it("should have metadata file") {
      shotFolder
        .metadataFile() shouldBe s"shot/screenshots/green/debug/Api26/screenshots-default/metadata.json"
      shotFolder
        .composeMetadataFile() shouldBe s"shot/screenshots/green/debug/Api26/screenshots-compose-default/metadata_compose.json"
    }

    it("should have a report folder") {
      shotFolder.reportFolder() shouldBe s"shot/build/reports/shot/green/debug/Api26/"
      shotFolder
        .verificationReportFolder() shouldBe s"shot/build/reports/shot/green/debug/Api26/verification/"
      shotFolder
        .recordingReportFolder() shouldBe s"shot/build/reports/shot/green/debug/Api26/record/"
    }
  }

  describe("Shot folder orchestrated") {
    val shotFolder = ShotFolder("shot", "shot/build", "debug", None, None, "/", true)

    it("should have screenshots folder") {
      shotFolder
        .pulledScreenshotsFolder() shouldBe s"shot/screenshots/debug/screenshots-default-orchestrated/"
      shotFolder
        .pulledComposeScreenshotsFolder() shouldBe s"shot/screenshots/debug/screenshots-compose-default/"
      shotFolder
        .pulledComposeOrchestratedScreenshotsFolder() shouldBe s"shot/screenshots/debug/screenshots-compose-default-orchestrated/"
    }

    it("should have metadata file") {
      shotFolder
        .metadataFile() shouldBe s"shot/screenshots/debug/screenshots-default-orchestrated/metadata.json"
      shotFolder
        .composeMetadataFile() shouldBe s"shot/screenshots/debug/screenshots-compose-default/metadata_compose.json"
      shotFolder.composeMetadataFileName() shouldBe "metadata_compose.json"
      shotFolder.metadataFileName() shouldBe "metadata.json"
    }
  }
}
