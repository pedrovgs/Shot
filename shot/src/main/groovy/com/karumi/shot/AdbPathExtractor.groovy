package com.karumi.shot

import org.gradle.api.Project

class AdbPathExtractor {

    static String extracPath(Project project) {
        project.android.getAdbExe().toString()
    }
}
