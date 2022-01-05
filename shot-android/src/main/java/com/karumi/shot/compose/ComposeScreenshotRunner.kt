package com.karumi.shot.compose

import android.app.Instrumentation
import com.karumi.shot.permissions.AndroidStoragePermissions

class ComposeScreenshotRunner {
    companion object {

        var composeScreenshot: ComposeScreenshot? = null

        fun onCreate(instrumentation: Instrumentation) {
            composeScreenshot = ComposeScreenshot(
                session = ScreenshotTestSession(),
                saver = ScreenshotSaver(instrumentation.context.packageName, SemanticsNodeBitmapGenerator()),
                permissions = AndroidStoragePermissions(instrumentation)
            )
        }

        fun onDestroy() = composeScreenshot?.saveMetadata()
    }
}
