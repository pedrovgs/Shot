package com.karumi.shot

import scala.beans.BeanProperty

object ShotExtension {
  val name = "shot"
}

class ShotExtension(@BeanProperty var appId: String) {

  def this() = this(null)

  def getOptionAppId: Option[String] = Option(getAppId)

}
