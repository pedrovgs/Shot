package com.karumi.shot

import com.karumi.shot.domain.model.FilePath

import scala.io.Source

class Files {

  def read(filePath: FilePath): String =
    Source.fromFile(filePath, "UTF8").getLines.mkString
}
