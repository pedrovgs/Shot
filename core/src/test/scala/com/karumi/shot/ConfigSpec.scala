package com.karumi.shot

import com.karumi.shot.domain.Config
import org.scalatest.{FlatSpec, Matchers}

class ConfigSpec extends FlatSpec with Matchers {

  "Config" should "use the screenshot tests library implemented by Facebook" in {
    Config.androidDependency shouldBe "com.facebook.testing.screenshot:core:0.4.2"
  }

  it should "add the dependency using the androidTestCompile mode" in {
    Config.androidDependencyMode shouldBe "androidTestCompile"
  }

  it should "save the screenshots into the screenshots folder" in {
    Config.screenshotsFolderName shouldBe "/screenshots"
  }

}
