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
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.facebook.testing.screenshot.Screenshot
import com.facebook.testing.screenshot.Screenshot.snapActivity
import com.facebook.testing.screenshot.ViewHelpers
import com.facebook.testing.screenshot.internal.TestNameDetector

interface ScreenshotTest {

    private val context: Context get() = getInstrumentation().targetContext

    /**
     * Function designed to be executed right before the screenshot is taken. Override it
     * when needed to disable any view or cancel any animation before Shot takes the screenshot.
     * You can use ``childrenViews`` extension methods in order to perform any task. Remember you might need to invoke
     * it from the UI thread.
     */
    fun prepareUIForScreenshot() {
    }

    fun compareScreenshot(
        activity: Activity,
        heightInPx: Int? = null,
        widthInPx: Int? = null,
        backgroundColor: Int = android.R.color.white
    ) {
        val view = activity.findViewById<View>(android.R.id.content)

        if (heightInPx == null && widthInPx == null) {
            disableFlakyComponentsAndWaitForIdle(view)
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
        disableFlakyComponentsAndWaitForIdle(view)

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

    fun disableFlakyComponentsAndWaitForIdle(view: View) {
        prepareUIForScreenshot()
        disableAnimatedComponents(view)
        waitForAnimationsToFinish()
    }

    fun waitForAnimationsToFinish() {
        getInstrumentation().waitForIdleSync()
        Espresso.onIdle()
    }

    fun runOnUi(block: () -> Unit) {
        getInstrumentation().runOnMainSync { block() }
    }

    private fun disableAnimatedComponents(view: View) {
        runOnUi {
            hideEditTextCursors(view)
        }
    }

    private fun hideEditTextCursors(view: View) {
        view.childrenViews<EditText>().forEach {
            it.isCursorVisible = false
        }
    }
}