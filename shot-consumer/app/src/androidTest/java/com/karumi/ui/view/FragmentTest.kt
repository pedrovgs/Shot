package com.karumi.ui.view

import androidx.fragment.app.testing.launchFragmentInContainer
import com.karumi.shot.FragmentScenarioUtils.waitForFragment
import com.karumi.shot.ScreenshotTest
import org.junit.Test

class FragmentTest : ScreenshotTest {

    @Test
    fun basicScenarioWithDefaultArguments() {
        val fragment = launchFragmentInContainer<SimpleFragment>().waitForFragment()

        compareScreenshot(fragment)
    }

    @Test
    fun customHeight() {
        val fragment = launchFragmentInContainer<SimpleFragment>().waitForFragment()

        compareScreenshot(fragment, heightInPx = 200)
    }
}