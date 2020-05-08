package com.karumi.ui.view

import com.github.salomonbrys.kodein.Kodein
import com.karumi.R
import org.junit.Test

class HideViewsActivityTest : AcceptanceTest<HideViewsActivity>(HideViewsActivity::class.java) {

    override val ignoredViews: List<Int> = listOf(R.id.view1)

    @Test
    fun showsActivityWithViewsInside() {
        val activity = startActivity()

        compareScreenshot(activity)
    }

    override val testDependencies = Kodein.Module(allowSilentOverride = true) {

    }
}