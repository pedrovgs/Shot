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
}
