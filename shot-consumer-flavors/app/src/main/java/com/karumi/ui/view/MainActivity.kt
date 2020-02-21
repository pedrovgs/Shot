package com.karumi.ui.view

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import com.github.salomonbrys.kodein.Kodein.Module
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.karumi.R
import com.karumi.domain.model.SuperHero
import com.karumi.domain.usecase.GetSuperHeroes
import com.karumi.ui.presenter.SuperHeroesPresenter
import com.karumi.ui.view.adapter.SuperHeroesAdapter
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : BaseActivity(), SuperHeroesPresenter.View {

    override val presenter: SuperHeroesPresenter by injector.instance()
    private lateinit var adapter: SuperHeroesAdapter
    override val layoutId: Int = R.layout.main_activity
    override val toolbarView: Toolbar
        get() = toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeAdapter()
        initializeRecyclerView()
    }

    private fun initializeAdapter() {
        adapter = SuperHeroesAdapter(presenter)
    }

    private fun initializeRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)
        recycler_view.adapter = adapter
    }

    override fun showLoading() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress_bar.visibility = View.GONE
    }

    override fun showEmptyCase() {
        tv_empty_case.visibility = View.VISIBLE
    }

    override fun showSuperHeroes(superHeroes: List<SuperHero>) {
        adapter.clear()
        adapter.addAll(superHeroes)
        adapter.notifyDataSetChanged()
    }

    override fun openDetail(name: String) {
        SuperHeroDetailActivity.open(activity = this, superHeroName = name)
    }

    override val activityModules = Module(allowSilentOverride = true) {
        bind<SuperHeroesPresenter>() with provider {
            SuperHeroesPresenter(
                this@MainActivity,
                instance()
            )
        }
        bind<GetSuperHeroes>() with provider { GetSuperHeroes(instance()) }
    }
}
