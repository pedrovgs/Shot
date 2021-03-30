package com.karumi.ui.view

import androidx.appcompat.widget.Toolbar
import android.view.View
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.karumi.R
import com.karumi.ui.presenter.CursorPresenter
import kotlinx.android.synthetic.main.cursor_activity.*
import kotlinx.android.synthetic.main.main_activity.toolbar

class CursorActivity : BaseActivity(), CursorPresenter.View {

    override val layoutId: Int = R.layout.cursor_activity
    override val presenter: CursorPresenter by injector.instance()
    override val toolbarView: Toolbar
        get() = toolbar

    override val activityModules = Kodein.Module(allowSilentOverride = true) {
        bind<CursorPresenter>() with provider {
            CursorPresenter(this@CursorActivity)
        }
    }

    override fun showEditText() {
        et_cursor.visibility = View.VISIBLE
    }
}