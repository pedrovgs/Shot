package com.karumi.shot

import org.scalatest.{FlatSpec, Matchers}

class ShotPluginSpec extends FlatSpec with Matchers {

  "Shot plugin" should "execute just one test" in {
    1 should be (1)
  }
}
