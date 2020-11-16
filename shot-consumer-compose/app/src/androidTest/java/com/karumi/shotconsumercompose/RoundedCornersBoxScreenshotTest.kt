package com.karumi.shotconsumercompose

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.karumi.shot.ScreenshotTest
import com.karumi.shotconsumercompose.ui.ShotConsumerComposeTheme
import org.junit.Rule
import org.junit.Test

class RoundedCornersBoxScreenshotTest : ScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun rendersTheDefaultComponent() {
        renderComponent()
        compareScreenshot(composeRule.onRoot())
    }

    private fun renderComponent(greeting: String? = null) {
        composeRule.setContent {
            ShotConsumerComposeTheme {
                RoundedCornersBox()
            }
        }
    }

}