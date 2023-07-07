package com.karumi.ui.view

import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.EditText
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.karumi.R
import com.karumi.ui.presenter.CursorPresenter

class CursorActivity : BaseActivity(), CursorPresenter.View {

    private val etCursor: EditText by lazy { findViewById(R.id.et_cursor) }
    override val layoutId: Int = R.layout.cursor_activity
    override val presenter: CursorPresenter by injector.instance()
    override val toolbarView: Toolbar by lazy { findViewById(R.id.toolbar)  }

    override val activityModules = Kodein.Module(allowSilentOverride = true) {
        bind<CursorPresenter>() with provider {
            CursorPresenter(this@CursorActivity)
        }
    }

    override fun showEditText() {
        etCursor.visibility = View.VISIBLE
    }
}