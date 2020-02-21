package com.karumi.ui.view

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.facebook.testing.screenshot.Screenshot
import com.facebook.testing.screenshot.ViewHelpers

interface ScreenshotTest {
    fun compareScreenshot(activity: Activity) {
        Screenshot.snapActivity(activity).record()
    }

    fun compareScreenshot(holder: RecyclerView.ViewHolder, height: Int) {
        compareScreenshot(holder.itemView, height)
    }

    fun compareScreenshot(view: View, height: Int) {
        val context = getInstrumentation().targetContext
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        ViewHelpers.setupView(view)
            .setExactHeightPx(context.resources.getDimensionPixelSize(height))
            .setExactWidthPx(metrics.widthPixels)
            .layout()
        Screenshot.snap(view).record()
    }
}