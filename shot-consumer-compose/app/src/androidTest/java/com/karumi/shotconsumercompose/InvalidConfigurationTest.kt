package com.karumi.shotconsumercompose

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.karumi.shot.ScreenshotTest
import com.karumi.shot.compose.ComposeScreenshot
import com.karumi.shot.compose.ComposeScreenshotRunner
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class InvalidConfigurationTest : ScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var composeScreenshot: ComposeScreenshot? = null

    @Before
    fun setUp() {
        composeScreenshot = ComposeScreenshotRunner.composeScreenshot
        ComposeScreenshotRunner.composeScreenshot = null
    }

    @After
    fun tearDown() {
        ComposeScreenshotRunner.composeScreenshot = composeScreenshot
    }

    @Test(expected = IllegalStateException::class)
    fun throwsAnExceptionIfTheComposeScreenshotRunnerIsNotConfiguredProperlyCheckingRule() {


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