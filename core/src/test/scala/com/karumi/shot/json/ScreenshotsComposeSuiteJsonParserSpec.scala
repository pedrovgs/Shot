package com.karumi.shot.json

import com.karumi.shot.Resources
import com.karumi.shot.domain.{Dimension, Screenshot}
import com.karumi.shot.json.ScreenshotsComposeSuiteJsonParser.{
  parseScreenshotSuite,
  parseScreenshots
}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.Seq

class ScreenshotsComposeSuiteJsonParserSpec
    extends AnyFlatSpec
    with should.Matchers
    with Resources {
  private val anyScreenshotsFolder = "/screenshots/"
  private val anyTemporalScreenshotsFolder =
    "/screenshots/screenshots-default/"
  private val anyScreenshotsTemporalBuildPath =
    "/build/tmp/shot/screenshots"

  private val testClassName = "com.example.test.ButtonTest"
  private val testName1     = "test1"
  private val testName2     = "test2"
  private val testName3     = "test3"
  private val name1         = s"${testClassName}_${testName1}"
  private val name2         = s"${testClassName}_${testName2}"
  private val name3         = s"${testClassName}_${testName3}"

  private val expectedScreenshots = Seq(
    Screenshot(
      name = name1,
      recordedScreenshotPath = s"$anyScreenshotsFolder$name1.png",
      temporalScreenshotPath = s"$anyScreenshotsTemporalBuildPath/$name1.png",
      testClass = testClassName,
      testName = testName1,
      tilesDimension = Dimension(0, 0),
      viewHierarchy = "",
      absoluteFileNames = Seq(),
      relativeFileNames = Seq(),
      recordedPartsPaths = Seq(s"$anyTemporalScreenshotsFolder/$name1.png"),
      screenshotDimension = Dimension(0, 0)
    ),
    Screenshot(
      name = name2,
      recordedScreenshotPath = s"$anyScreenshotsFolder$name2.png",
      temporalScreenshotPath = s"$anyScreenshotsTemporalBuildPath/$name2.png",
      testClass = testClassName,
      testName = testName2,
      tilesDimension = Dimension(0, 0),
      viewHierarchy = "",
      absoluteFileNames = Seq(),
      relativeFileNames = Seq(),
      recordedPartsPaths = Seq(s"$anyTemporalScreenshotsFolder/$name2.png"),
      screenshotDimension = Dimension(0, 0)
    ),
    Screenshot(
      name = name3,
      recordedScreenshotPath = s"$anyScreenshotsFolder$name3.png",
      temporalScreenshotPath = s"$anyScreenshotsTemporalBuildPath/$name3.png",
      testClass = testClassName,
      testName = testName3,
      tilesDimension = Dimension(0, 0),
      viewHierarchy = "",
      absoluteFileNames = Seq(),
      relativeFileNames = Seq(),
      recordedPartsPaths = Seq(s"$anyTemporalScreenshotsFolder/$name3.png"),
      screenshotDimension = Dimension(0, 0)
    )
  )

  "ScreenshotsComposeSuiteJsonParser" should "return an empty spec if there are no screenshots" in {
    val json = testResourceContent("/screenshots-metadata/empty-compose-screenshots-metadata.json")

    val screenshots =
      parseScreenshotSuite(
        json,
        anyScreenshotsFolder,
        anyTemporalScreenshotsFolder,
        anyScreenshotsTemporalBuildPath
      )

    screenshots shouldBe empty
  }

  it should "parse a regular metadata file" in {
    val json = testResourceContent("/screenshots-metadata/compose-metadata.json")

    val screenshots =
      parseScreenshotSuite(
        json,
        anyScreenshotsFolder,
        anyTemporalScreenshotsFolder,
        anyScreenshotsTemporalBuildPath
      )

    screenshots.size shouldBe 3

    screenshots should be(expectedScreenshots)
  }
}
