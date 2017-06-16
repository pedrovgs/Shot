package com.karumi.shot

class Shot(val adb: Adb) {

  def configureAdbPath(adbPath: String) {
    Adb.adbBinaryPath = adbPath
  }

}
