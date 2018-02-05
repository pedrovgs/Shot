package com.karumi.shot.base64

import com.karumi.shot.Resources
import org.scalatest.{FlatSpec, Matchers}

class Base64Spec extends FlatSpec with Matchers with Resources {
  it should "returns the base64 from an input file" in {
    val expectedBase64 = testResourceContent("/base64/base64Image.txt")
    val inputFilePath = getClass.getResource("/base64/imageInput.png").getPath

    val base64 = Base64Encoder.base64FromFile(inputFilePath)

    base64 shouldBe expectedBase64
  }
}
