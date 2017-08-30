package com.karumi.shot

import scala.beans.BeanProperty

object ShotExtension {
  val name = "shot"
}

class ShotExtension(@BeanProperty var appId: String,
                    @BeanProperty var instrumentationTestTask: String,
                    @BeanProperty var packageTestApkTask: String) {

  def this() = this(null, null, null)

  def getOptionAppId: Option[String] = Option(getAppId)

  def getOptionInstrumentationTestTask: Option[String] =
    Option(getInstrumentationTestTask)

  def getOptionPackageTestApkTask: Option[String] =
    Option(getPackageTestApkTask)

}
