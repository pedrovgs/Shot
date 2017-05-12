package com.karumi.shot

import com.karumi.shot.free.domain.Config
import org.gradle.api.{Plugin, Project}

class ShotPlugin extends Plugin[Project] {

  override def apply(project: Project): Unit = {
    addAndroidTestDependency(project)
  }

  private def addAndroidTestDependency(project: Project): Unit = {
    val dependencyMode = Config.androidDependencyMode
    val dependencyName = Config.androidDependency
    val dependency = project.getDependencies.create(dependencyName)
    project.getDependencies.add(dependencyMode, dependency)
  }

}
