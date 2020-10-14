package com.karumi.shot

import scala.beans.BeanProperty

object ShotExtension {
  val name = "shot"
}

class ShotExtension(@BeanProperty var runInstrumentation: Boolean,
                    @BeanProperty var useComposer: Boolean,
                    @BeanProperty var tolerance: Int) {

  def this() = this(true, false, 100)

}
