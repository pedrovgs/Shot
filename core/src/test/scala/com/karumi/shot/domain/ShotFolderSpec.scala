package com.karumi.shot.domain

import org.scalatest.funspec._
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class ShotFolderSpec extends AnyFunSpec {

  describe("Shot folder") {
    val shotFolder = ShotFolder("shot", "shot/build", "debug", None)

    it("should have screenshots folder"){
      shotFolder.screenshotsFolder() shouldBe s"shot/screenshots/debug/"
      shotFolder.pulledScreenshotsFolder() shouldBe s"shot/screenshots/debug/screenshots-default/"
      shotFolder.pulledComposeScreenshotsFolder() shouldBe s"shot/screenshots/debug/screenshots-compose-default/"
    }

    it("should have metadata file"){
      shotFolder.metadataFile() shouldBe s"shot/screenshots/debug/screenshots-default/metadata.xml"
      shotFolder.composeMetadataFile() shouldBe s"shot/screenshots/debug/screenshots-compose-default/metadata.json"
    }

    it("should have a report folder") {
      shotFolder.reportFolder() shouldBe s"shot/build/reports/shot/debug/"
      shotFolder.verificationReportFolder() shouldBe s"shot/build/reports/shot/debug/verification/"
      shotFolder.recordingReportFolder() shouldBe s"shot/build/reports/shot/debug/record/"
    }
  }

  describe("Product Flavor") {
    val shotFolder = ShotFolder("shot", "shot/build", "debug", Some("green"))

    it("should have screenshots folder"){
      shotFolder.screenshotsFolder() shouldBe s"shot/screenshots/green/debug/"
      shotFolder.pulledScreenshotsFolder() shouldBe s"shot/screenshots/green/debug/screenshots-default/"
      shotFolder.pulledComposeScreenshotsFolder() shouldBe s"shot/screenshots/green/debug/screenshots-compose-default/"
    }

    it("should have metadata file"){
      shotFolder.metadataFile() shouldBe s"shot/screenshots/green/debug/screenshots-default/metadata.xml"
      shotFolder.composeMetadataFile() shouldBe s"shot/screenshots/green/debug/screenshots-compose-default/metadata.json"
    }

    it("should have a report folder") {
      shotFolder.reportFolder() shouldBe s"shot/build/reports/shot/green/debug/"
      shotFolder.verificationReportFolder() shouldBe s"shot/build/reports/shot/green/debug/verification/"
      shotFolder.recordingReportFolder() shouldBe s"shot/build/reports/shot/green/debug/record/"
    }
  }

}
