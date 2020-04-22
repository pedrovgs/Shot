package com.karumi.ui.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.salomonbrys.kodein.Kodein
import com.karumi.R
import org.junit.Test

class CursorActivityTest : AcceptanceTest<CursorActivity>(
        CursorActivity::class.java
) {

    @Test
    fun cursorIsNotVisibleOnScreenshot() {
        val activity = startActivity()
        onView(withId(R.id.et_cursor)).perform(click())
        compareScreenshot(activity)
    }

    override val testDependencies = Kodein.Module(allowSilentOverride = true) {}
}