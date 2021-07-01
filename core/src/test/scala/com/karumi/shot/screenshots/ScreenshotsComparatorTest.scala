package com.karumi.shot.screenshots

import com.karumi.shot.Resources
import com.karumi.shot.domain.{Dimension, Screenshot}
import com.karumi.shot.domain.model.ScreenshotsSuite
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class ScreenshotsComparatorTest extends AnyFlatSpec with should.Matchers with Resources {

  private val sampleImage = getClass.getClassLoader.getResource("images/sample.png").getPath
  private val sampleImageScrambled =
    getClass.getClassLoader.getResource("images/sample-scrambled.png").getPath

  it should "not match image with same pixels but different order" in {
    val comparator = new ScreenshotsComparator()
    val screenshot = Screenshot(
      "test",
      sampleImage,
      sampleImageScrambled,
      "SomeClass",
      "ShoudFail",
      Dimension(768, 1280),
      null,
      null,
      null,
      List(sampleImageScrambled),
      Dimension(768, 1280)
    )
    val suite: ScreenshotsSuite = List(screenshot)

    val result = comparator.compare(suite, 0.01)

    result.hasErrors shouldBe true
  }
}
