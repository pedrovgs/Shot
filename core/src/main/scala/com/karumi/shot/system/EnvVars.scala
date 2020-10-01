package com.karumi.shot.system

class EnvVars {
  def androidSerial: Option[String] =
    try {
      Option(sys.env("ANDROID_SERIAL")).filter(!_.isEmpty)
    } catch {
      case _: Throwable => None
    }
}
