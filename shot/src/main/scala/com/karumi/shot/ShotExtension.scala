package com.karumi.shot

import com.karumi.shot.domain.Config
import scala.beans.BeanProperty

object ShotExtension {
  val name = "shot"
}

class ShotExtension(
    @BeanProperty var runInstrumentation: Boolean,
    @BeanProperty var useComposer: Boolean,
    @BeanProperty var tolerance: Double,
    @BeanProperty var colorTolerance: Int,
    @BeanProperty var showOnlyFailingTestsInReports: Boolean,
    @BeanProperty var applicationId: String
) {

  def this() = this(true, false, Config.defaultTolerance, Config.defaultColorTolerance, false, "")

}
