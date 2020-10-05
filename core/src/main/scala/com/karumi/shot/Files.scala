package com.karumi.shot

import java.io.File
import java.util

import com.karumi.shot.domain.model.FilePath
import org.apache.commons.io.FileUtils

import scala.io.Source

class Files {
  def listFilesInFolder(folder: FilePath): util.Collection[File] =
    FileUtils.listFiles(new File(folder), null, false)

  def rename(origin: FilePath, target: FilePath): Unit = {
    val originFile = new File(origin)
    if (originFile.exists()) {
      FileUtils.moveFile(originFile, new File(target))
    }
  }

  def read(filePath: FilePath): String =
    Source.fromFile(filePath, "UTF8").getLines.mkString
}
