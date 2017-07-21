package com.karumi.shot

import scala.io.Source

trait Resources {

  def testResourceContent(path: String): String = {
    val fileStream = getClass.getResourceAsStream(path)
    Source.fromInputStream(fileStream).getLines().mkString
  }
}
