package com.karumi.ui.view

import android.view.LayoutInflater
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.karumi.R
import com.karumi.domain.model.SuperHero
import com.karumi.ui.presenter.SuperHeroesPresenter
import com.karumi.ui.view.adapter.SuperHeroViewHolder
import org.junit.Test
import org.mockito.Mockito.mock

class SuperHeroViewHolderTest : ScreenshotTest {

    @Test
    fun showsAnySuperHero() {
        val superHero = givenASuperHero()
        val holder = givenASuperHeroViewHolder()

        holder.render(superHero)

        compareScreenshot(holder, R.dimen.super_hero_row_height)
    }

    @Test
    fun showsSuperHeroesWithLongNames() {
        val superHero = givenASuperHeroWithALongName()
        val holder = givenASuperHeroViewHolder()

        holder.render(superHero)

        compareScreenshot(holder, R.dimen.super_hero_row_height)
    }

    @Test
    fun showsSuperHeroesWithLongDescriptions() {
        val superHero = givenASuperHeroWithALongDescription()
        val holder = givenASuperHeroViewHolder()

        holder.render(superHero)

        compareScreenshot(holder, R.dimen.super_hero_row_height)
    }

    @Test
    fun showsAvengersBadge() {
        val superHero = givenASuperHero(isAvenger = true)
        val holder = givenASuperHeroViewHolder()

        holder.render(superHero)

        compareScreenshot(holder, R.dimen.super_hero_row_height)
    }

    private fun givenASuperHeroViewHolder(): SuperHeroViewHolder {
        val context = getInstrumentation().targetContext
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.super_hero_row, null, false)
        return SuperHeroViewHolder(
            view,
            mock<SuperHeroesPresenter>(SuperHeroesPresenter::class.java)
        )
    }

    private fun givenASuperHeroWithALongDescription(): SuperHero {
        val superHeroName = "Super Hero Name"
        val superHeroDescription = """
            |Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor
            |incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation
            |ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in
            |voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non
            |proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
            |""".trimMargin()
        val isAvenger = false
        return givenASuperHero(superHeroName, superHeroDescription, isAvenger)
    }

    private fun givenASuperHeroWithALongName(): SuperHero {
        val superHeroName = """
            |Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor
            |incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation
            |ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in
            |voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non
            |proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
            |""".trimMargin()
        val superHeroDescription = "Description Super Hero"
        val isAvenger = false
        return givenASuperHero(superHeroName, superHeroDescription, isAvenger)
    }

    private fun givenASuperHero(
        superHeroName: String = "Super Hero Name",
        superHeroDescription: String = "Super Hero Description",
        isAvenger: Boolean = false
    ): SuperHero = SuperHero(superHeroName, null, isAvenger, superHeroDescription)
}