package com.karumi.shot.screenshots

import java.io.File

import com.karumi.shot.Resources
import com.karumi.shot.domain.{Dimension, Screenshot}
import com.karumi.shot.domain.model.ScreenshotsSuite
import com.sksamuel.scrimage.ImmutableImage
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

  it should "consider two nearly identical images equal with colorTolerance 2.0 and tolerance 0.1%" in {
    val baselinePath =
      getClass.getClassLoader.getResource("images/validation_baseline.png").getPath
    val actualPath =
      getClass.getClassLoader.getResource("images/validation_actual.png").getPath
    val dim = ImmutableImage.loader().fromFile(new File(baselinePath))
    val screenshot = Screenshot(
      "validation",
      baselinePath,
      actualPath,
      "TestClass",
      "validationTest",
      Dimension(1, 1),
      null,
      null,
      null,
      List(actualPath),
      Dimension(dim.width, dim.height)
    )
    val suite: ScreenshotsSuite = List(screenshot)
    val comparator = new ScreenshotsComparator()

    val result = comparator.compare(suite, tolerance = 0.1, colorTolerance = 2.0)

    result.hasErrors shouldBe false
  }

  it should "consider the same two nearly identical images different with colorTolerance 0" in {
    val baselinePath =
      getClass.getClassLoader.getResource("images/validation_baseline.png").getPath
    val actualPath =
      getClass.getClassLoader.getResource("images/validation_actual.png").getPath
    val dim = ImmutableImage.loader().fromFile(new File(baselinePath))
    val screenshot = Screenshot(
      "validation",
      baselinePath,
      actualPath,
      "TestClass",
      "validationTest",
      Dimension(1, 1),
      null,
      null,
      null,
      List(actualPath),
      Dimension(dim.width, dim.height)
    )
    val suite: ScreenshotsSuite = List(screenshot)
    val comparator = new ScreenshotsComparator()

    val result = comparator.compare(suite, tolerance = 0.1, colorTolerance = 0.0)

    result.hasErrors shouldBe true
  }
}
