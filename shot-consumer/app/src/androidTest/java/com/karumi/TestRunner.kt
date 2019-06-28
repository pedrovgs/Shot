package com.karumi

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.test.runner.AndroidJUnitRunner
import com.facebook.testing.screenshot.ScreenshotRunner
import com.github.tmurakami.dexopener.DexOpener

class TestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        DexOpener.install(this)
        return super.newApplication(cl, className, context)
    }

    override fun callApplicationOnCreate(app: Application) {
        app.asApp().kodein.mutable = true
        super.callApplicationOnCreate(app)
    }

    override fun onCreate(arguments: Bundle?) {
        super.onCreate(arguments)
        ScreenshotRunner.onCreate(this, arguments)
    }

    override fun finish(resultCode: Int, results: Bundle?) {
        ScreenshotRunner.onDestroy()
        super.finish(resultCode, results)
    }
}