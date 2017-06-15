package com.karumi.shot

import org.scalatest.{FlatSpec, Matchers}

class ShotSpec extends FlatSpec with Matchers {
  "Shot" should "execute a dummy test" in {
    1 should be(1)
  }
}
