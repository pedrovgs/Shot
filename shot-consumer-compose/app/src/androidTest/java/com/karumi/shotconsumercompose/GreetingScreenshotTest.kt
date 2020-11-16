package com.karumi.shotconsumercompose

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.karumi.shot.ScreenshotTest
import com.karumi.shotconsumercompose.ui.ShotConsumerComposeTheme
import org.junit.Rule
import org.junit.Test

class GreetingScreenshotTest : ScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun rendersTheDefaultComponent() {
        renderComponent()
        compareScreenshot(composeRule.onRoot())
    }

    @Test
    fun rendersAGreetingWithAnEmptyText() {
        renderComponent("")
        compareScreenshot(composeRule)
    }

    @Test
    fun rendersAGreetingWithATextFullOfWhitespaces() {
        renderComponent(" ".repeat(200))
        compareScreenshot(composeRule)
    }

    @Test
    fun rendersAGreetingWithAShortText() {
        renderComponent("Hello!")
        compareScreenshot(composeRule)
    }

    @Test
    fun rendersAGreetingWithALongText() {
        renderComponent("Hello world from the compose!".repeat(20))
        compareScreenshot(composeRule)
    }

    @Test
    fun rendesAnyComponentUsingABitmapInsteadOfANode() {
        renderComponent("Hello world from the compose!")
        compareScreenshot(composeRule.onRoot().captureToImage().asAndroidBitmap())
    }

    @Composable
    private fun greetingComponent(greeting: String) {
        ShotConsumerComposeTheme {
            Surface(color = MaterialTheme.colors.background) {
                Greeting(greeting)
            }
        }
    }

    private fun renderComponent(greeting: String? = null) {
        composeRule.setContent {
            if (greeting == null) {
                DefaultPreview()
            } else {
                greetingComponent(greeting)
            }
        }
    }

}