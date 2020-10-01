package com.karumi.shotconsumercompose

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.ui.test.createComposeRule
import com.karumi.shot.ScreenshotTest
import com.karumi.shotconsumercompose.ui.ShotConsumerComposeTheme
import org.junit.Rule
import org.junit.Test

class GreetingScreenshotTest : ScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun rendersTheDefaultComponent() {
        composeRule.setContent {
            DefaultPreview()
        }
    }

    @Test
    fun rendersAGreetingWithAShortText() {
        val greeting = "Hello!"
        composeRule.setContent {
            greetingComponent(greeting)
        }
    }

    @Test
    fun rendersAGreetingWithALongText() {
        val greeting = "Hello world from the compose!".repeat(20)
        composeRule.setContent {
            greetingComponent(greeting)
        }
    }

    @Composable
    private fun greetingComponent(greeting: String) {
        ShotConsumerComposeTheme {
            Surface(color = MaterialTheme.colors.background) {
                Greeting(greeting)
            }
        }
    }
}