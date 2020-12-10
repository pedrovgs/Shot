package com.karumi.shot.compose

import androidx.compose.ui.test.SemanticsNodeInteraction
import com.nhaarman.mockito_kotlin.*
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ComposeScreenshotTest {

    companion object {
        private val anyScreenshotMetadata: ScreenshotMetadata = ScreenshotMetadata("test1", "MainActivityTest", "testName1")
    }

    private lateinit var composeScreenshot: ComposeScreenshot

    @Mock
    private lateinit var screenshotSaver: ScreenshotSaver

    @Mock
    private lateinit var node: SemanticsNodeInteraction

    @Before
    fun setUp() {
        composeScreenshot = ComposeScreenshot(ScreenshotTestSession(), screenshotSaver)
    }

    @Test
    fun savesTheNewScreenshotAsPartOfTheSessionMetadata() {
        val data = anyScreenshotMetadata

        composeScreenshot.saveScreenshot(node, data)

        val expectedSessionMetadata = ScreenshotSessionMetadata(listOf(data))
        assertEquals(expectedSessionMetadata, composeScreenshot.saveMetadata().getScreenshotSessionMetadata())
    }

    @Test
    fun wheneverANewScreenshotIsSavedTheNodeAssociatedIsSavedIntoTheSdCard() {
        val data = anyScreenshotMetadata

        composeScreenshot.saveScreenshot(node, data)

        verify(screenshotSaver).saveScreenshot(ScreenshotToSave(ScreenshotSource.Node(node), data))
    }

    @Test
    fun afterAddingSomeScreenshotsWhenTheSaveMetadataIsInvokedAllTheSessionIsSavedIntoTheSdCard() {
        val data = anyScreenshotMetadata

        composeScreenshot.saveScreenshot(node, data)
        composeScreenshot.saveScreenshot(node, data)
        composeScreenshot.saveScreenshot(node, data)
        composeScreenshot.saveMetadata()

        val captor = argumentCaptor<ScreenshotTestSession>()
        verify(screenshotSaver).saveMetadata(captor.capture())

        val actualSession = captor.lastValue
        val actualSessionMetadata = actualSession.getScreenshotSessionMetadata()
        assertEquals(3, actualSessionMetadata.screenshotsData.size)
        for (i in 0 until 3) {
            assertEquals(data, actualSessionMetadata.screenshotsData[i])
        }
    }

    @Test
    fun whenNoScreenshotsWereSavedTheMetadataIsNotSaved() {
        composeScreenshot.saveMetadata()
        verify(screenshotSaver, never()).saveMetadata(any())
    }
}
