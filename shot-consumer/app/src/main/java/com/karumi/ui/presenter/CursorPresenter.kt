package com.karumi.ui.presenter

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
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