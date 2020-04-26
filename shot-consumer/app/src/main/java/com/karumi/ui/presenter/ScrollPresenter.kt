package com.karumi.ui.presenter

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import com.karumi.common.weak

class ScrollPresenter(view: View) : LifecycleObserver {

    private val view: View? by weak(view)

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun update() {
    }

    interface View {}
}