package com.karumi.shot

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Project

class AdbPathExtractor {

    // AGP 9 removed the legacy `android.getAdbExe()` accessor. Resolve adb through the new
    // AndroidComponents `SdkComponents.adb` provider instead.
    static String extractPath(Project project) {
        def androidComponents = project.extensions.findByType(AndroidComponentsExtension)
        if (androidComponents != null) {
            return androidComponents.sdkComponents.adb.get().asFile.absolutePath
        }
        return null
    }
}
