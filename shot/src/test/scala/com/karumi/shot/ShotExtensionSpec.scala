package com.karumi.shot

import org.scalatest.{FlatSpec, Matchers}

object ShotExtensionSpec {
  private val anyAppId = "com.karumi.shot"
}
class ShotExtensionSpec extends FlatSpec with Matchers {
  import ShotExtensionSpec._

  private val extension = new ShotExtension()

  "ShotExtensionSpec" should "use none as app name" in {
    extension.getAppId shouldBe null
  }

  it should "return None if the app id is null when created" in {
    extension.getOptionAppId shouldBe None
  }

  it should "return Some if the app id is not null when created" in {
    extension.setAppId(anyAppId)
    extension.getOptionAppId shouldBe Some(anyAppId)
  }

}
