package com.karumi.ui.view

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.TextView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.github.salomonbrys.kodein.Kodein.Module
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.karumi.R
import com.karumi.domain.model.SuperHero
import com.karumi.domain.usecase.GetSuperHeroes
import com.karumi.ui.presenter.SuperHeroesPresenter
import com.karumi.ui.view.adapter.SuperHeroesAdapter

class MainActivity : BaseActivity(), SuperHeroesPresenter.View {

    private val recyclerView: RecyclerView by lazy { findViewById(R.id.recycler_view) }
    private val progressBar: ContentLoadingProgressBar by lazy { findViewById(R.id.progress_bar) }
    private val tvEmptyCase: TextView by lazy { findViewById(R.id.tv_empty_case) }

    override val presenter: SuperHeroesPresenter by injector.instance()
    private lateinit var adapter: SuperHeroesAdapter
    override val layoutId: Int = R.layout.main_activity
    override val toolbarView: Toolbar by lazy { findViewById(R.id.toolbar) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeAdapter()
        initializeRecyclerView()
    }

    private fun initializeAdapter() {
        adapter = SuperHeroesAdapter(presenter)
    }

    private fun initializeRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    override fun showEmptyCase() {
        tvEmptyCase.visibility = View.VISIBLE
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
