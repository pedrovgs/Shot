package com.karumi.shot

import android.view.View

interface FocusCleaner {

    sealed class FocusViewIdentifier {
        class FocusById(val id: Int?) : FocusViewIdentifier()
        class FocusByTag(val tag: Any?) : FocusViewIdentifier()
    }

    private fun View.meetsFocusCondition(focusViewIdentifier: FocusViewIdentifier): Boolean =
        when (focusViewIdentifier) {
            is FocusViewIdentifier.FocusById -> focusViewIdentifier.id == id
            is FocusViewIdentifier.FocusByTag -> focusViewIdentifier.tag == tag
        }

    val focusTargetView: FocusViewIdentifier?
        get() = null

    fun setFocusOnTargetView(view: View) {
        focusTargetView?.run {
            view.filterChildrenViews { child -> child.isFocusable }.forEach { it.clearFocus() }

            val focusedView =
                view.filterChildrenViews { child -> child.meetsFocusCondition(this) }
                    .firstOrNull()

            focusedView?.requestFocusFromTouch()
        }
    }
}