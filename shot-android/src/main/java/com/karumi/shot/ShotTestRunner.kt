package com.karumi.shot

import android.os.Bundle
import android.system.Os
import androidx.test.runner.AndroidJUnitRunner
import com.facebook.testing.screenshot.ScreenshotRunner
import com.karumi.shot.compose.ComposeScreenshotRunner
import java.io.File

open class ShotTestRunner : AndroidJUnitRunner() {

    override fun onCreate(args: Bundle) {
        super.onCreate(args)
        configureFacebookLibFolder()
        ScreenshotRunner.onCreate(this, args)
        ComposeScreenshotRunner.onCreate(this)
    }

    override fun finish(resultCode: Int, results: Bundle?) {
        ScreenshotRunner.onDestroy()
        ComposeScreenshotRunner.onDestroy()

        val orchestratedSuffix = "-orchestrated"

        val packageName = ComposeScreenshotRunner.packageName.orEmpty()
        val screenshotsFolder: String = "${AndroidStorageInfo.storageBaseUrl}/screenshots/$packageName/screenshots-default/"
        val orchestratedFolder: String = "${AndroidStorageInfo.storageBaseUrl}/screenshots/$packageName/screenshots-default$orchestratedSuffix/"

        File(orchestratedFolder).mkdirs()
        val file = File(screenshotsFolder)

        file.listFiles()?.forEach { file ->
            var i = 0
            var target: File
            do {
                var end = if (i == 0) "" else "_$i"
                target = File(orchestratedFolder + file.name + end)
                i = i.inc()
            } while (target.exists())

            file.copyTo(target, overwrite = true)
        }

        super.finish(resultCode, results)
    }

    // This env var configuration is needed to make facebook's library
    // point at the folder we can use in API 29+ to save screenshots.
    // Without this hack, their library would only work on API 28-.
    private fun configureFacebookLibFolder() {
        Os.setenv("EXTERNAL_STORAGE", AndroidStorageInfo.storageBaseUrl, true)
    }
}
