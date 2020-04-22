package com.karumi.ui.presenter

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import com.karumi.common.weak

class CursorPresenter(view: View) : LifecycleObserver {

    private val view: View? by weak(view)

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun update() {
        view?.showEditText()
    }

    interface View {
        fun showEditText()
    }
}