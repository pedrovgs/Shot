package com.karumi.ui.view

import androidx.appcompat.widget.Toolbar
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.karumi.R
import com.karumi.ui.presenter.SimplePresenter
import kotlinx.android.synthetic.main.simple_activity.*

class SimpleActivity : BaseActivity(), SimplePresenter.View {

    override val layoutId: Int = R.layout.simple_activity
    override val presenter: SimplePresenter by injector.instance()
    override val toolbarView: Toolbar
        get() = toolbar

    override val activityModules = Kodein.Module(allowSilentOverride = true) {
        bind<SimplePresenter>() with provider {
            SimplePresenter()
        }
    }
}