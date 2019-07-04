package com.karumi.shot

import java.io.File

import com.karumi.shot.domain.model.FilePath
import org.apache.commons.io.FileUtils

import scala.io.Source

class Files {
  def rename(origin: FilePath, target: FilePath): Unit =
    FileUtils.moveFile(new File(origin), new File(target))

  def read(filePath: FilePath): String =
    Source.fromFile(filePath, "UTF8").getLines.mkString
}
