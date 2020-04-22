package com.karumi.shot

import android.os.Bundle
import androidx.test.runner.AndroidJUnitRunner
import com.facebook.testing.screenshot.ScreenshotRunner

open class ShotTestRunner : AndroidJUnitRunner() {

    override fun onCreate(args: Bundle) {
        super.onCreate(args)
        ScreenshotRunner.onCreate(this, args)
    }

    override fun finish(resultCode: Int, results: Bundle?) {
        ScreenshotRunner.onDestroy()
        super.finish(resultCode, results)
    }
}
