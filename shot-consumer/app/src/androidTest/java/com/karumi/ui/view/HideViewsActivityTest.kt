package com.karumi.ui.view

import com.github.salomonbrys.kodein.Kodein
import org.junit.Test

class HideViewsActivityTest : AcceptanceTest<HideViewsActivity>(HideViewsActivity::class.java) {

    @Test
    fun showsActivityWithViewsInside() {
        val activity = startActivity()

        compareScreenshot(activity)
    }

    override val testDependencies = Kodein.Module(allowSilentOverride = true) {

    }
}