package com.karumi.shot

import android.view.View
import android.view.ViewGroup

object ViewUtils {

    fun getFilteredChildren(view: View, filter: (View) -> Boolean): List<View> {
        val children = mutableSetOf<View>()

        if (view !is ViewGroup) {
            if (filter.invoke(view)) {
                children.add(view)
            }
        } else {
            for (i in 0 until view.childCount) {
                view.getChildAt(i).let {
                    children.addAll(getFilteredChildren(it, filter))

                    if (filter.invoke(it)) {
                        children.add(it)
                    }
                }
            }
        }

        return children.toList()
    }
}