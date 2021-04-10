package com.karumi.shot

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import kotlin.math.absoluteValue

@RequiresApi(Build.VERSION_CODES.CUPCAKE)
interface KeyboardCloser {

    fun closeKeyboardAndWaitUntilIsNotVisible(activity: Activity) {
        recursiveCloseKeyboardAndWaitUntilIsNotVisible(activity = activity)
    }

    fun closeKeyboard(activity: Activity) {
        val focusedView = activity.currentFocus
        if (focusedView != null) {
            activity.runOnUiThread {
                val imm: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
            }
        }
    }

    private fun recursiveCloseKeyboardAndWaitUntilIsNotVisible(activity: Activity, retriesLeft: Int = 5) {
        if (retriesLeft == 0) {
            throw IllegalStateException("We couldn't close they keyboard after 5 retries.")
        }
        closeKeyboard(activity)
        val rootView = activity.findViewById<View>(android.R.id.content).rootView
        val measuredRect = Rect()
        rootView.rootView.getWindowVisibleDisplayFrame(measuredRect)
        val rootViewHeight = measuredRect.bottom - measuredRect.top
        val screenHeight = rootView.rootView.height
        val heightDiff: Int = (screenHeight - rootViewHeight).absoluteValue
        val isKeyboardClosed = heightDiff < (screenHeight * 0.05)
        if (!isKeyboardClosed) {
            sleep(500)
            recursiveCloseKeyboardAndWaitUntilIsNotVisible(activity, retriesLeft - 1)
        }
    }

    private fun sleep(millis: Long) {
        Thread.sleep(millis)
    }
}
