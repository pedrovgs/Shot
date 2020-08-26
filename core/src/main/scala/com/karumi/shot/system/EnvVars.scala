package com.karumi.shot.system

class EnvVars {
  def androidSerial: Option[String] =
    Option(sys.env("ANDROID_SERIAL")).filter(_.isEmpty)
}
