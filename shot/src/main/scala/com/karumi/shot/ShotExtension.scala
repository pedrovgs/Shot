package com.karumi.shot

import scala.beans.BeanProperty

object ShotExtension {
  val name = "shot"
}

class ShotExtension(@BeanProperty var runInstrumentation: Boolean,
                    @BeanProperty var useComposer: Boolean) {

  def this() = this(true, false)

}
