package com.karumi.ui.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.ContentLoadingProgressBar
import com.github.salomonbrys.kodein.Kodein.Module
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.karumi.R
import com.karumi.domain.model.SuperHero
import com.karumi.domain.usecase.GetSuperHeroByName
import com.karumi.ui.presenter.SuperHeroDetailPresenter
import com.karumi.ui.utils.setImageBackground

class SuperHeroDetailActivity : BaseActivity(), SuperHeroDetailPresenter.View {

    companion object {
        private const val SUPER_HERO_NAME_KEY = "super_hero_name_key"

        fun open(activity: Activity, superHeroName: String) {
            val intent = Intent(activity, SuperHeroDetailActivity::class.java)
            intent.putExtra(SUPER_HERO_NAME_KEY, superHeroName)
            activity.startActivity(intent)
        }
    }

    private val progressBar: ContentLoadingProgressBar by lazy { findViewById(R.id.progress_bar) }
    private val tvSuperHeroDescription: TextView
        by lazy { findViewById(R.id.tv_super_hero_description) }
    private val tvSuperHeroName: TextView
            by lazy { findViewById(R.id.tv_super_hero_name) }
    private val ivSuperHeroPhoto: ImageView
            by lazy { findViewById(R.id.iv_super_hero_photo) }
    private val ivAvengersBadge: ImageView
            by lazy { findViewById(R.id.iv_avengers_badge) }

    override val presenter: SuperHeroDetailPresenter by injector.instance()

    override val layoutId: Int = R.layout.super_hero_detail_activity
    override val toolbarView: Toolbar by lazy { findViewById(R.id.toolbar) }

    override fun preparePresenter(intent: Intent?) {
        val superHeroName = intent?.extras?.getString(SUPER_HERO_NAME_KEY)
        title = superHeroName
        presenter.preparePresenter(superHeroName)
    }

    override fun close() = finish()

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    override fun showSuperHero(superHero: SuperHero) {
        tvSuperHeroName.text = superHero.name
        tvSuperHeroDescription.text = superHero.description
        ivAvengersBadge.visibility =
                if (superHero.isAvenger) View.VISIBLE else View.GONE
        ivSuperHeroPhoto.setImageBackground(superHero.photo)
    }

    override val activityModules = Module(allowSilentOverride = true) {
        bind<SuperHeroDetailPresenter>() with provider {
            SuperHeroDetailPresenter(this@SuperHeroDetailActivity, instance())
        }
        bind<GetSuperHeroByName>() with provider { GetSuperHeroByName(instance()) }
    }
}