package com.karumi.ui.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.salomonbrys.kodein.Kodein
import com.karumi.R
import com.karumi.shot.KeyboardCloser
import org.junit.Test

class CursorActivityTest : AcceptanceTest<CursorActivity>(
        CursorActivity::class.java
), KeyboardCloser {

    @Test
    fun cursorIsNotVisibleOnScreenshot() {
        val activity = startActivity()
        onView(withId(R.id.et_cursor)).perform(click())
        closeKeyboardAndWaitUntilIsNotVisible(activity)
        compareScreenshot(activity)
    }

    override val testDependencies = Kodein.Module(allowSilentOverride = true) {}
}