package com.karumi.shot

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.WindowManager
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.facebook.testing.screenshot.Screenshot
import com.facebook.testing.screenshot.ViewHelpers
import com.facebook.testing.screenshot.internal.RecordBuilderImpl
import com.facebook.testing.screenshot.internal.TestNameDetector
import com.karumi.shot.compose.ComposeScreenshotRunner
import com.karumi.shot.compose.ScreenshotMetadata
import java.lang.IllegalStateException

interface ScreenshotTest {

    private val context: Context get() = getInstrumentation().targetContext

    val ignoredViews: List<Int>
        get() = emptyList()

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
        name: String? = null,
        backgroundColor: Int = android.R.color.white,
        maxPixels: Long = RecordBuilderImpl.DEFAULT_MAX_PIXELS,
    ) {
        val view = activity.findViewById<View>(android.R.id.content)

        if (heightInPx == null && widthInPx == null) {
            disableFlakyComponentsAndWaitForIdle(view)
            takeActivitySnapshot(activity, name, maxPixels)
        } else {
            runOnUi {
                view.setBackgroundResource(backgroundColor)
            }
            compareScreenshot(view = view!!, heightInPx = heightInPx, widthInPx = widthInPx, name = name, maxPixels = maxPixels)
        }
    }

    fun compareScreenshot(
        fragment: Fragment,
        heightInPx: Int? = null,
        widthInPx: Int? = null,
        name: String? = null,
        maxPixels: Long = RecordBuilderImpl.DEFAULT_MAX_PIXELS,
    ) = compareScreenshot(view = fragment.requireView(), heightInPx = heightInPx, widthInPx = widthInPx, name = name, maxPixels = maxPixels)

    fun compareScreenshot(
        dialog: Dialog,
        heightInPx: Int? = null,
        widthInPx: Int? = null,
        name: String? = null,
        maxPixels: Long = RecordBuilderImpl.DEFAULT_MAX_PIXELS,
    ) {
        val window = dialog.window
        if (window != null) {
            compareScreenshot(view = window.decorView, heightInPx = heightInPx, widthInPx = widthInPx, name = name, maxPixels = maxPixels)
        }
    }

    fun compareScreenshot(
        holder: RecyclerView.ViewHolder,
        heightInPx: Int,
        widthInPx: Int? = null,
        name: String? = null,
        maxPixels: Long = RecordBuilderImpl.DEFAULT_MAX_PIXELS,
    ) = compareScreenshot(view = holder.itemView, heightInPx = heightInPx, widthInPx = widthInPx, name = name, maxPixels = maxPixels)

    fun compareScreenshot(
        view: View,
        heightInPx: Int? = null,
        widthInPx: Int? = null,
        name: String? = null,
        maxPixels: Long = RecordBuilderImpl.DEFAULT_MAX_PIXELS,
    ) {
        disableFlakyComponentsAndWaitForIdle(view)

        val context = getInstrumentation().targetContext
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)

        val height = heightInPx ?: metrics.heightPixels
        val width = widthInPx ?: metrics.widthPixels
        runOnUi {
            ViewHelpers.setupView(view)
                .setExactHeightPx(height)
                .setExactWidthPx(width)
                .layout()
        }
        takeViewSnapshot(name, view, maxPixels)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun compareScreenshot(
        rule: ComposeTestRule,
        name: String? = null,
    ) {
        rule.waitForIdle()
        compareScreenshot(rule.onRoot(), name)
    }

    fun compareScreenshot(bitmap: Bitmap, name: String? = null) {
        disableFlakyComponentsAndWaitForIdle()
        val rawTestName = TestNameDetector.getTestName()
        val testName = name ?: rawTestName
        val testClassName = TestNameDetector.getTestClass()
        val screenshotName = "${testClassName}_$testName"
        val data = ScreenshotMetadata(name = screenshotName, testClassName = testClassName, testName = testName)
        val composeScreenshot = ComposeScreenshotRunner.composeScreenshot
        if (composeScreenshot != null) {
            composeScreenshot.saveScreenshot(bitmap, data)
        } else {
            throw IllegalStateException("Shot couldn't take the screenshot. Ensure your project uses ShotTestRunner as your instrumentation test runner.")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun compareScreenshot(node: SemanticsNodeInteraction, name: String? = null) {
        disableFlakyComponentsAndWaitForIdle()
        val rawTestName = TestNameDetector.getTestName()
        val testName = name ?: rawTestName
        val testClassName = TestNameDetector.getTestClass()
        val screenshotName = "${testClassName}_$testName"
        val data = ScreenshotMetadata(name = screenshotName, testClassName = testClassName, testName = testName)
        val composeScreenshot = ComposeScreenshotRunner.composeScreenshot
        if (composeScreenshot != null) {
            composeScreenshot.saveScreenshot(node, data)
        } else {
            throw IllegalStateException("Shot couldn't take the screenshot. Ensure your project uses ShotTestRunner as your instrumentation test runner.")
        }
    }

    fun disableFlakyComponentsAndWaitForIdle(view: View? = null) {
        prepareUIForScreenshot()
        if (view != null) {
            disableAnimatedComponents(view)
            hideIgnoredViews(view)
        }
        if (notInAppMainThread()) {
            waitForAnimationsToFinish()
        }
    }

    private fun hideIgnoredViews(view: View) = runOnUi {
        view.filterChildrenViews { children -> children.id in ignoredViews }.forEach { viewToIgnore ->
            viewToIgnore.visibility = INVISIBLE
        }
    }

    fun waitForAnimationsToFinish() {
        getInstrumentation().waitForIdleSync()
        Espresso.onIdle()
    }

    fun runOnUi(block: () -> Unit) {
        if (notInAppMainThread()) {
            getInstrumentation().runOnMainSync { block() }
        } else {
            block()
        }
    }

    private fun notInAppMainThread() = Looper.myLooper() != Looper.getMainLooper()

    private fun takeViewSnapshot(name: String?, view: View, maxPixels: Long) {
        val testName = name ?: TestNameDetector.getTestName()
        val snapshotName = "${TestNameDetector.getTestClass()}_$testName"
        try {
            Screenshot
                .snap(view)
                .setIncludeAccessibilityInfo(false)
                .setName(snapshotName)
                .setMaxPixels(maxPixels)
                .record()
        } catch (t: Throwable) {
            Log.e("Shot", "Exception captured while taking screenshot for snapshot with name $snapshotName", t)
            throw IllegalStateException("Exception occurred while taking screenshot for snapshot with name $snapshotName", t)
        }
    }

    private fun takeActivitySnapshot(activity: Activity, name: String?, maxPixels: Long) {
        val testName = name ?: TestNameDetector.getTestName()
        val snapshotName = "${TestNameDetector.getTestClass()}_$testName"
        try {
            Screenshot
                .snapActivity(activity)
                .setIncludeAccessibilityInfo(false)
                .setName(snapshotName)
                .setMaxPixels(maxPixels)
                .record()
        } catch (t: Throwable) {
            Log.e("Shot", "Exception captured while taking screenshot for snapshot with name $snapshotName", t)
            throw IllegalStateException("Exception occurred while taking screenshot for snapshot with name $snapshotName", t)
        }
    }

    private fun disableAnimatedComponents(view: View) {
        runOnUi {
            hideEditTextCursors(view)
            hideScrollViewBars(view)
        }
    }

    private fun hideScrollViewBars(view: View) {
        view.childrenViews<ScrollView>().forEach {
            hideViewBars(it)
        }

        view.childrenViews<HorizontalScrollView>().forEach {
            hideViewBars(it)
        }
    }

    private fun hideViewBars(it: View) {
        it.isHorizontalScrollBarEnabled = false
        it.isVerticalScrollBarEnabled = false
        it.overScrollMode = View.OVER_SCROLL_NEVER
    }

    private fun hideEditTextCursors(view: View) {
        view.childrenViews<EditText>().forEach {
            it.isCursorVisible = false
        }
    }
}
