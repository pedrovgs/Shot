package com.karumi.ui.view

import android.os.Bundle
import com.github.salomonbrys.kodein.Kodein.Module
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.karumi.data.repository.SuperHeroRepository
import com.karumi.domain.model.SuperHero
import org.junit.Test
import org.mockito.Mock
import org.mockito.kotlin.whenever

class SuperHeroDetailActivityTest : AcceptanceTest<SuperHeroDetailActivity>(
    SuperHeroDetailActivity::class.java
) {

    @Mock
    private lateinit var repository: SuperHeroRepository

    @Test
    fun showsAvengersBadgeIfSuperHeroIsPartOfTheAvengersTeam() {
        val superHero = givenThereIsASuperHero(isAvenger = true)

        val activity = startActivity(superHero)

        compareScreenshot(activity)
    }

    @Test
    fun doesNotShowAvengersBadgeIfSuperHeroIsNotPartOfTheAvengersTeam() {
        val superHero = givenThereIsASuperHero(isAvenger = false)

        val activity = startActivity(superHero)

        compareScreenshot(activity)
    }

    private fun givenThereIsASuperHero(isAvenger: Boolean): SuperHero {
        val superHeroName = "SuperHero"
        val superHeroDescription = "Super Hero Description"
        val superHero = SuperHero(superHeroName, null, isAvenger, superHeroDescription)
        whenever(repository.getByName(superHeroName)).thenReturn(superHero)
        return superHero
    }

    private fun startActivity(superHero: SuperHero): SuperHeroDetailActivity {
        val args = Bundle()
        args.putString("super_hero_name_key", superHero.name)
        return startActivity(args)
    }

    override val testDependencies = Module(allowSilentOverride = true) {
        bind<SuperHeroRepository>() with instance(repository)
    }
}