package com.karumi.shot.domain

import org.scalatest.flatspec._
import org.scalatest.matchers._

class ConfigSpec extends AnyFlatSpec with should.Matchers {

  "Config" should "use the screenshot tests library implemented by Facebook" in {
    Config.androidDependency shouldBe "com.karumi:shot-android:5.14.1"
  }

  it should "add the dependency using the androidTestImplementation mode" in {
    Config.androidDependencyMode shouldBe "androidTestImplementation"
  }

  it should "have instrumentation test task with flavor" in {
    Config.defaultInstrumentationTestTask(
      Some("green"),
      "debug"
    ) shouldBe "connectedGreenDebugAndroidTest"
    Config.composerInstrumentationTestTask(Some("green"), "debug") shouldBe "testGreenDebugComposer"
  }

  it should "have instrumentation test task without flavor" in {
    Config.defaultInstrumentationTestTask(None, "debug") shouldBe "connectedDebugAndroidTest"
    Config.composerInstrumentationTestTask(None, "debug") shouldBe "testDebugComposer"
  }

}
