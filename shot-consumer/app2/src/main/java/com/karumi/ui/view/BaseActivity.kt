package com.karumi.ui.view

import android.arch.lifecycle.LifecycleObserver
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.github.salomonbrys.kodein.Kodein.Module
import com.github.salomonbrys.kodein.android.KodeinAppCompatActivity
import com.karumi.asApp

abstract class BaseActivity : KodeinAppCompatActivity() {

    abstract val layoutId: Int
    abstract val presenter: LifecycleObserver
    abstract val toolbarView: Toolbar
    abstract val activityModules: Module

    override fun onCreate(savedInstanceState: Bundle?) {
        applicationContext.asApp().addModule(activityModules)
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        setSupportActionBar(toolbarView)
        lifecycle.addObserver(presenter)
        preparePresenter(intent)
    }

    open fun preparePresenter(intent: Intent?) {}
}