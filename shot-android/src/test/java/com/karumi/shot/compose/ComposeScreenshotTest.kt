package com.karumi.shot.compose

import androidx.ui.test.SemanticsNodeInteraction
import com.nhaarman.mockito_kotlin.verify
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ComposeScreenshotTest {

    companion object {
        private val anyScreenshotMetadata: ScreenshotMetadata = ScreenshotMetadata("testName")
    }

    private lateinit var composeScreenshot: ComposeScreenshot

    @Mock
    private lateinit var screenshotSaver: ScreenshotSaver

    @Mock
    private lateinit var node: SemanticsNodeInteraction

    @Before
    fun setUp() {
        composeScreenshot = ComposeScreenshot(ScreenshotTestSession.empty, screenshotSaver)
    }

    @Test
    fun savesTheNewScreenshotAsPartOfTheSessionMetadata() {
        val data = anyScreenshotMetadata

        composeScreenshot.saveScreenshot(node, data)

        val expectedSession = ScreenshotTestSession.empty.add(data)
        assertEquals(expectedSession, composeScreenshot.saveMetadata())
    }

    @Test
    fun wheneverANewScreenshotIsSavedTheNodeAssociatedIsSavedIntoTheSdCard() {
        val data = anyScreenshotMetadata

        composeScreenshot.saveScreenshot(node, data)

        verify(screenshotSaver).saveScreenshot(ScreenshotToSave(node, data))
    }

    @Test
    fun afterAddingSomeScreenshotsWhenTheSaveMetadataIsInvokedAllTheSessionIsSavedIntoTheSdCard() {
        val data = anyScreenshotMetadata

        composeScreenshot.saveScreenshot(node, data)
        composeScreenshot.saveScreenshot(node, data)
        composeScreenshot.saveScreenshot(node, data)
        composeScreenshot.saveMetadata()

        val expectedSession = ScreenshotTestSession.empty.add(data).add(data).add(data)
        verify(screenshotSaver).saveMetadata(expectedSession)
    }
}