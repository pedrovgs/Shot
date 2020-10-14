package com.karumi.shotconsumercompose

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.ui.test.createComposeRule
import com.karumi.shot.ScreenshotTest
import com.karumi.shotconsumercompose.ui.ShotConsumerComposeTheme
import org.junit.Rule
import androidx.ui.test.onRoot
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