package com.karumi.shot.compose

import android.app.Instrumentation
import com.karumi.shot.permissions.AndroidStoragePermissions

class ComposeScreenshotRunner {
    companion object {

        var composeScreenshot: ComposeScreenshot? = null
        var packageName: String? = null

        fun onCreate(instrumentation: Instrumentation) {
            packageName = instrumentation.context.packageName
            composeScreenshot = ComposeScreenshot(
                session = ScreenshotTestSession(),
                saver = ScreenshotSaver(packageName!!, SemanticsNodeBitmapGenerator()),
                permissions = AndroidStoragePermissions(instrumentation)
            )
        }

        fun onDestroy() = composeScreenshot?.saveMetadata()
    }
}
