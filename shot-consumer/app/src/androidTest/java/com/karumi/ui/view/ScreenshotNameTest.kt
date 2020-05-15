package com.karumi.ui.view

import com.github.salomonbrys.kodein.Kodein
import org.junit.Test


class ScreenshotNameTest : AcceptanceTest<SimpleActivity>(SimpleActivity::class.java) {

    @Test
    fun screenshotHasACustomName() {
        val activity = startActivity()
        compareScreenshot(activity, name = "aCustomName")
    }

    override val testDependencies = Kodein.Module(allowSilentOverride = true) {}
}