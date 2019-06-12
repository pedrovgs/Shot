package com.karumi.ui.presenter

import android.arch.lifecycle.Lifecycle.Event.ON_RESUME
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import com.karumi.common.async
import com.karumi.common.weak
import com.karumi.domain.model.SuperHero
import com.karumi.domain.usecase.GetSuperHeroByName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class SuperHeroDetailPresenter(
    view: View,
    private val getSuperHeroByName: GetSuperHeroByName
) : LifecycleObserver, CoroutineScope by MainScope() {

    private val view: View? by weak(view)

    private lateinit var name: String

    fun preparePresenter(name: String?) {
        if (name != null) {
            this.name = name
        } else {
            view?.close()
        }
    }

    @OnLifecycleEvent(ON_RESUME)
    fun update() {
        view?.showLoading()
        refreshSuperHeroes()
    }

    private fun refreshSuperHeroes() = launch {
        val result = async { getSuperHeroByName(name) }
        view?.hideLoading()
        view?.showSuperHero(result)
    }

    interface View {
        fun close()
        fun showLoading()
        fun hideLoading()
        fun showSuperHero(superHero: SuperHero)
    }
}