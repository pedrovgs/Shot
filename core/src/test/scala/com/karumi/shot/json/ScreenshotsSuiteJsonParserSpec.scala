package com.karumi.shot.json

import com.karumi.shot.Resources
import com.karumi.shot.xml.ScreenshotsSuiteJsonParser.{parseScreenshotSize, parseScreenshots}
import org.scalatest.flatspec._
import org.scalatest.matchers._

class ScreenshotsSuiteJsonParserSpec extends AnyFlatSpec with should.Matchers with Resources {

  private val anyScreenshotsFolder = "/screenshots/"
  private val anyTemporalScreenshotsFolder =
    "/screenshots/screenshots-default/"
  private val anyScreenshotsTemporalBuildPath =
    "build/tmp/shot/screenshots"

  "ScreenshotsSuiteJsonParser" should "return an empty spec if there are no screenshots" in {
    val json = testResourceContent("/screenshots-metadata/empty-screenshots-metadata.json")

    val screenshots =
      parseScreenshots(
        json,
        anyScreenshotsFolder,
        anyTemporalScreenshotsFolder,
        anyScreenshotsTemporalBuildPath
      )

    screenshots shouldBe empty
  }

  it should "parse a regular metadata file" in {
    val json = testResourceContent("/screenshots-metadata/metadata.json")
    val viewHierarchyContent =
      testResourceContent("/screenshots-metadata/view-hierarchy.json")

    val screenshotsWithoutSize =
      parseScreenshots(
        json,
        anyScreenshotsFolder,
        anyTemporalScreenshotsFolder,
        anyScreenshotsTemporalBuildPath
      )
    val screenshots = screenshotsWithoutSize.map { screenshot =>
      parseScreenshotSize(screenshot, viewHierarchyContent)
    }

    screenshots.size shouldBe 11
    val firstScreenshot = screenshots.head
    firstScreenshot.name shouldBe "com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes"
    firstScreenshot.recordedScreenshotPath shouldBe "/screenshots/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes.png"
    firstScreenshot.temporalScreenshotPath shouldBe "build/tmp/shot/screenshots/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes.png"
    firstScreenshot.testClass shouldBe "com.karumi.ui.view.MainActivityTest"
    firstScreenshot.testName shouldBe "showsSuperHeroesIfThereAreSomeSuperHeroes"
    firstScreenshot.tilesDimension.width shouldBe 3
    firstScreenshot.tilesDimension.height shouldBe 5
    firstScreenshot.viewHierarchy shouldBe "com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_dump.json"
    firstScreenshot.absoluteFileNames shouldBe Seq(
      "/storage/emulated/0/Download/screenshots/com.test.application.id.test/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes.png",
      "/storage/emulated/0/Download/screenshots/com.test.application.id.test/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_0_1.png",
      "/storage/emulated/0/Download/screenshots/com.test.application.id.test/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_0_2.png",
      "/storage/emulated/0/Download/screenshots/com.test.application.id.test/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_0_3.png",
      "/storage/emulated/0/Download/screenshots/com.test.application.id.test/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_0_4.png",
      "/storage/emulated/0/Download/screenshots/com.test.application.id.test/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_1_0.png",
      "/storage/emulated/0/Download/screenshots/com.test.application.id.test/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_1_1.png",
      "/storage/emulated/0/Download/screenshots/com.test.application.id.test/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_1_2.png",
      "/storage/emulated/0/Download/screenshots/com.test.application.id.test/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_1_3.png",
      "/storage/emulated/0/Download/screenshots/com.test.application.id.test/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_1_4.png",
      "/storage/emulated/0/Download/screenshots/com.test.application.id.test/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_2_0.png",
      "/storage/emulated/0/Download/screenshots/com.test.application.id.test/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_2_1.png",
      "/storage/emulated/0/Download/screenshots/com.test.application.id.test/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_2_2.png",
      "/storage/emulated/0/Download/screenshots/com.test.application.id.test/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_2_3.png",
      "/storage/emulated/0/Download/screenshots/com.test.application.id.test/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_2_4.png"
    )
    firstScreenshot.relativeFileNames shouldBe Seq(
      "com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes.png",
      "com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_0_1.png",
      "com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_0_2.png",
      "com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_0_3.png",
      "com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_0_4.png",
      "com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_1_0.png",
      "com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_1_1.png",
      "com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_1_2.png",
      "com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_1_3.png",
      "com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_1_4.png",
      "com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_2_0.png",
      "com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_2_1.png",
      "com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_2_2.png",
      "com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_2_3.png",
      "com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_2_4.png"
    )
    firstScreenshot.recordedPartsPaths shouldBe Seq(
      "/screenshots/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes.png",
      "/screenshots/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_0_1.png",
      "/screenshots/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_0_2.png",
      "/screenshots/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_0_3.png",
      "/screenshots/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_0_4.png",
      "/screenshots/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_1_0.png",
      "/screenshots/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_1_1.png",
      "/screenshots/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_1_2.png",
      "/screenshots/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_1_3.png",
      "/screenshots/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_1_4.png",
      "/screenshots/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_2_0.png",
      "/screenshots/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_2_1.png",
      "/screenshots/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_2_2.png",
      "/screenshots/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_2_3.png",
      "/screenshots/screenshots-default/com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_2_4.png"
    )
    firstScreenshot.screenshotDimension.width shouldBe 768
    firstScreenshot.screenshotDimension.height shouldBe 400
    firstScreenshot.fileName shouldBe "com.karumi.ui.view.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes.png"
  }
}
