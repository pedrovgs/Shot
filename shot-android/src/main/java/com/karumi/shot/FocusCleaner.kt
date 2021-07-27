package com.karumi.shot

import android.view.View

interface FocusCleaner {

    var focusTargetView: View?

    fun setFocusOnTargetView(view: View) {
        focusTargetView?.run {
            view.filterChildrenViews { child -> child.isFocusable }.map { it.clearFocus() }
            requestFocusFromTouch()
        }
    }
}