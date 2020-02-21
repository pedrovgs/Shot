package com.karumi.ui.view

import com.github.salomonbrys.kodein.Kodein.Module
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.karumi.data.repository.SuperHeroRepository
import com.karumi.domain.model.SuperHero
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.mockito.Mock

class MainActivityTest : AcceptanceTest<MainActivity>(MainActivity::class.java) {

    companion object {
        private const val ANY_NUMBER_OF_SUPER_HEROES = 100
    }

    @Mock
    private lateinit var repository: SuperHeroRepository

    @Test
    fun showsEmptyCaseIfThereAreNoSuperHeroes() {
        givenThereAreNoSuperHeroes()

        val activity = startActivity()

        compareScreenshot(activity)
    }

    @Test
    fun showsJustOneSuperHero() {
        givenThereAreSomeSuperHeroes(1)

        val activity = startActivity()

        compareScreenshot(activity)
    }

    @Test
    fun showsSuperHeroesIfThereAreSomeSuperHeroes() {
        givenThereAreSomeSuperHeroes(ANY_NUMBER_OF_SUPER_HEROES)

        val activity = startActivity()

        compareScreenshot(activity)
    }

    @Test
    fun showsAvengersBadgeIfASuperHeroIsPartOfTheAvengersTeam() {
        givenThereAreSomeAvengers(ANY_NUMBER_OF_SUPER_HEROES)

        val activity = startActivity()

        compareScreenshot(activity)
    }

    @Test
    fun doesNotShowAvengersBadgeIfASuperHeroIsNotPartOfTheAvengersTeam() {
        givenThereAreSomeSuperHeroes(ANY_NUMBER_OF_SUPER_HEROES)

        val activity = startActivity()

        compareScreenshot(activity)
    }

    private fun givenThereAreSomeAvengers(numberOfAvengers: Int): List<SuperHero> =
        givenThereAreSomeSuperHeroes(numberOfAvengers, avengers = true)

    private fun givenThereAreSomeSuperHeroes(
        numberOfSuperHeroes: Int = 1,
        avengers: Boolean = false
    ): List<SuperHero> {
        val superHeroes = IntRange(0, numberOfSuperHeroes - 1).map { id ->
            val superHeroName = "SuperHero - $id"
            val superHeroDescription = "Description Super Hero - $id"
            SuperHero(
                superHeroName, null, avengers,
                superHeroDescription
            )
        }

        whenever(repository.getAllSuperHeroes()).thenReturn(superHeroes)
        return superHeroes
    }

    private fun givenThereAreNoSuperHeroes() {
        whenever(repository.getAllSuperHeroes()).thenReturn(emptyList())
    }

    override val testDependencies = Module(allowSilentOverride = true) {
        bind<SuperHeroRepository>() with instance(repository)
    }
}