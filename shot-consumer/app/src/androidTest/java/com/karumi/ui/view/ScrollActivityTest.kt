package com.karumi.ui.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.salomonbrys.kodein.Kodein
import com.karumi.R
import org.junit.Test

class ScrollActivityTest : AcceptanceTest<ScrollActivity>(
        ScrollActivity::class.java
) {

    @Test
    fun horizontalScrollIsNotVisibleOnScreenshot() {
        val activity = startActivity()
        onView(withId(R.id.horizontalIconsScrollView)).perform(swipeLeft())
        compareScreenshot(activity)
    }

    @Test
    fun verticalScrollIsNotVisibleOnScreenshot() {
        val activity = startActivity()
        onView(withId(R.id.verticalIconsScrollView)).perform(swipeUp())
        compareScreenshot(activity)
    }

    override val testDependencies = Kodein.Module(allowSilentOverride = true) {}
}