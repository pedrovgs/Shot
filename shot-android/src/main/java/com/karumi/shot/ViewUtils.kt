package com.karumi.shot

import android.view.View
import android.view.ViewGroup

@Suppress("UNCHECKED_CAST")
inline fun <reified T : View> View.childrenViews(): List<T> = filterChildrenViews {
    it is T
} as List<T>

fun View.filterChildrenViews(filter: (View) -> Boolean): List<View> {
    val children = mutableSetOf<View>()
    val view = this
    if (view !is ViewGroup) {
        if (filter.invoke(view)) {
            children.add(view)
        }
    } else {
        for (i in 0 until view.childCount) {
            view.getChildAt(i).let {
                children.addAll(it.filterChildrenViews(filter))
                if (filter.invoke(it)) {
                    children.add(it)
                }
            }
        }
    }

    return children.toList()
}