package com.karumi.shot

import android.os.Bundle
import androidx.test.runner.AndroidJUnitRunner
import com.facebook.testing.screenshot.ScreenshotRunner
import com.karumi.shot.compose.ComposeScreenshotRunner

open class ShotTestRunner : AndroidJUnitRunner() {

    override fun onCreate(args: Bundle) {
        super.onCreate(args)
        ScreenshotRunner.onCreate(this, args)
        ComposeScreenshotRunner.onCreate(this)
    }

    override fun finish(resultCode: Int, results: Bundle?) {
        ScreenshotRunner.onDestroy()
        ComposeScreenshotRunner.onDestroy()
        super.finish(resultCode, results)
    }
}
