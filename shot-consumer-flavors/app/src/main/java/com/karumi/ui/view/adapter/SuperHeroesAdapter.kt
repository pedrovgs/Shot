package com.karumi.ui.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.karumi.R
import com.karumi.domain.model.SuperHero
import com.karumi.ui.presenter.SuperHeroesPresenter

internal class SuperHeroesAdapter(
    private val presenter: SuperHeroesPresenter
) : RecyclerView.Adapter<SuperHeroViewHolder>() {
    private val superHeroes: MutableList<SuperHero> = ArrayList()

    fun addAll(collection: Collection<SuperHero>) {
        superHeroes.addAll(collection)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuperHeroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.super_hero_row, parent,
            false
        )
        return SuperHeroViewHolder(view, presenter)
    }

    override fun onBindViewHolder(holder: SuperHeroViewHolder, position: Int) {
        val superHero = superHeroes[position]
        holder.render(superHero)
    }

    override fun getItemCount(): Int {
        return superHeroes.size
    }

    fun clear() {
        superHeroes.clear()
    }
}