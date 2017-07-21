package com.karumi.shot.xml

import com.karumi.shot.Resources
import org.scalatest.{FlatSpec, Matchers}

class ScreenshotsSuiteXmlParserSpec
    extends FlatSpec
    with Matchers
    with Resources {

  private val parser = new ScreenshotsSuiteXmlParser()

  "ScreenshotsSuiteXmlParser" should "return an empty spec if there are no screenshots" in {
    val xml = testResourceContent(
      "/screenshots-metadata/empty-screenshots-metadata.xml")

    val screenshots = parser.parseScreenshots(xml)

    screenshots shouldBe empty
  }

  it should "parse a regular metadata file" in {
    val xml = testResourceContent("/screenshots-metadata/metadata.xml")

    val screenshots = parser.parseScreenshots(xml)

    screenshots.size shouldBe 11
    val firstScreenshot = screenshots.head
    firstScreenshot.name shouldBe "com.karumi.screenshot.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes"
    firstScreenshot.testClass shouldBe "com.karumi.screenshot.MainActivityTest"
    firstScreenshot.testName shouldBe "showsSuperHeroesIfThereAreSomeSuperHeroes"
    firstScreenshot.tileWidth shouldBe 2
    firstScreenshot.tileHeight shouldBe 3
    firstScreenshot.viewHierarchy shouldBe "com.karumi.screenshot.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_dump.xml"
    firstScreenshot.absoluteFileNames shouldBe Seq(
      "/mnt/sdcard/screenshots/com.karumi.screenshot.test/screenshots-default/com.karumi.screenshot.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes.png",
      "/mnt/sdcard/screenshots/com.karumi.screenshot.test/screenshots-default/com.karumi.screenshot.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_0_1.png",
      "/mnt/sdcard/screenshots/com.karumi.screenshot.test/screenshots-default/com.karumi.screenshot.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_0_2.png",
      "/mnt/sdcard/screenshots/com.karumi.screenshot.test/screenshots-default/com.karumi.screenshot.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_1_0.png",
      "/mnt/sdcard/screenshots/com.karumi.screenshot.test/screenshots-default/com.karumi.screenshot.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_1_1.png",
      "/mnt/sdcard/screenshots/com.karumi.screenshot.test/screenshots-default/com.karumi.screenshot.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_1_2.png"
    )
    firstScreenshot.relativeFileNames shouldBe Seq(
      "com.karumi.screenshot.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes.png",
      "com.karumi.screenshot.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_0_1.png",
      "com.karumi.screenshot.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_0_2.png",
      "com.karumi.screenshot.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_1_0.png",
      "com.karumi.screenshot.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_1_1.png",
      "com.karumi.screenshot.MainActivityTest_showsSuperHeroesIfThereAreSomeSuperHeroes_1_2.png"
    )
  }
}
