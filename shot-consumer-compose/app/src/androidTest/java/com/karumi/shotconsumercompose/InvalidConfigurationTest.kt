package com.karumi.shotconsumercompose

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.karumi.shot.ScreenshotTest
import com.karumi.shot.compose.ComposeScreenshotRunner
import org.junit.Rule
import org.junit.Test
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class InvalidConfigurationTest : ScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test(expected = IllegalStateException::class)
    fun throwsAnExceptionIfTheComposeScreenshotRunnerIsNotConfiguredProperlyCheckingRule() {
        ComposeScreenshotRunner.composeScreenshot = null

        renderComponent()

        compareScreenshot(composeRule)
    }

    @Test(expected = IllegalStateException::class)
    fun throwsAnExceptionIfTheComposeScreenshotRunnerIsNotConfiguredProperlyCheckingNode() {
        ComposeScreenshotRunner.composeScreenshot = null

        renderComponent()

        compareScreenshot(composeRule.onRoot())
    }

    private fun renderComponent() {
        composeRule.setContent {
            DefaultPreview()
        }
    }
}