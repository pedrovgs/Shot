package com.karumi.shot

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.facebook.testing.screenshot.Screenshot
import com.facebook.testing.screenshot.Screenshot.snapActivity
import com.facebook.testing.screenshot.ViewHelpers
import com.facebook.testing.screenshot.internal.TestNameDetector

interface ScreenshotTest {

    private val context: Context get() = getInstrumentation().targetContext

    fun compareScreenshot(
        activity: Activity,
        heightInPx: Int? = null,
        widthInPx: Int? = null,
        backgroundColor: Int = android.R.color.white
    ) {
        val view = activity.findViewById<View>(android.R.id.content)
        disableFlakyComponents(view)

        if (heightInPx == null && widthInPx == null) {
            waitForAnimationsToFinish()
            snapActivity(activity).record()
        } else {
            runOnUi {
                view.setBackgroundResource(backgroundColor)
            }
            compareScreenshot(view = view!!, heightInPx = heightInPx, widthInPx = widthInPx)
        }
    }

    fun compareScreenshot(
        fragment: Fragment,
        heightInPx: Int? = null,
        widthInPx: Int? = null
    ) = compareScreenshot(fragment.view!!, heightInPx)

    fun compareScreenshot(
        dialog: Dialog,
        heightInPx: Int? = null,
        widthInPx: Int? = null
    ) {
        val window = dialog.window
        if (window != null) {
            compareScreenshot(window.decorView, heightInPx, widthInPx)
        }
    }

    fun compareScreenshot(holder: RecyclerView.ViewHolder, heightInPx: Int, widthInPx: Int? = null) =
        compareScreenshot(view = holder.itemView, heightInPx = heightInPx, widthInPx = widthInPx)

    fun compareScreenshot(view: View, heightInPx: Int? = null, widthInPx: Int? = null, name: String? = null) {
        disableFlakyComponents(view)
        waitForAnimationsToFinish()

        val context = getInstrumentation().targetContext
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)

        val height = heightInPx ?: metrics.heightPixels
        val width = widthInPx ?: metrics.widthPixels

        val heightInDp = heightInPx ?: height
        runOnUi {
            ViewHelpers.setupView(view)
                    .setExactHeightPx(heightInDp)
                    .setExactWidthPx(width)
                    .layout()
        }
        val testName = name ?: TestNameDetector.getTestName()
        Screenshot
                .snap(view)
                .setName("${TestNameDetector.getTestClass()}_$testName")
                .record()
    }

    fun waitForAnimationsToFinish() {
        getInstrumentation().waitForIdleSync()
    }

    fun runOnUi(block: () -> Unit) {
        getInstrumentation().runOnMainSync { block() }
    }

    fun disableFlakyComponents(view: View) {
        ViewUtils.getFilteredChildren(view) {
            it is EditText
        }.forEach {
            if (it is EditText) {
                runOnUi { it.isCursorVisible = false }
            }
        }
    }
}