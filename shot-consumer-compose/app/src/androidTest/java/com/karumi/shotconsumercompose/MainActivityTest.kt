package com.karumi.shotconsumercompose

import android.util.Log
import com.karumi.shot.ScreenshotTest
import org.junit.Test
import androidx.test.ext.junit.rules.activityScenarioRule
import org.junit.Rule

class MainActivityTest : ScreenshotTest {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()

    @Test
    fun activityTest() {
        val activity = launchActivity()

        compareScreenshot(activity)
    }

    // Hack needed until we fully support Activity Scenarios
    private fun launchActivity(): MainActivity {
        var activity: MainActivity? = null
        activityScenarioRule.scenario.onActivity {
            activity = it
        }
        while (activity == null) {
            Log.d("MainActivityTest", "Waiting for activity to be initialized")
        }
        return activity!!
    }
}