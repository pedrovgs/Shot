package com.karumi.shot.mothers

import com.karumi.shot.domain.ShotFolder

object ProjectFolderMother {

  val anyProjectFolder   = "/User/pedro/projects/KarumiApp/app"
  val anyBuildFolder     = "/User/pedro/projects/KarumiApp/app/build"
  val anyBuildType       = "debug"
  val anyFlavor          = "green"

  val anyShotFolder: ShotFolder = ShotFolder(anyProjectFolder,
                                             anyBuildFolder,
                                             anyBuildType,
                                             Some(anyFlavor))

}
