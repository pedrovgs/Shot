package com.karumi.shot.compose

import android.app.Instrumentation

class ComposeScreenshotRunner {
    companion object {

        lateinit var composeScreenshot: ComposeScreenshot

        fun onCreate(instrumentation: Instrumentation) {
            composeScreenshot = ComposeScreenshot(
                    session = ScreenshotTestSession(),
                    saver = ScreenshotSaver(instrumentation.context.packageName, SemanticsNodeBitmapGenerator())
            )
        }

        fun onDestroy() {
            composeScreenshot.saveMetadata()
        }
    }
}