package com.karumi.shot.compose

import android.graphics.Bitmap
import androidx.compose.ui.test.SemanticsNodeInteraction
import com.google.gson.annotations.SerializedName
import com.karumi.shot.permissions.AndroidStoragePermissions

class ComposeScreenshot(
    private val session: ScreenshotTestSession,
    private val saver: ScreenshotSaver,
    private val permissions: AndroidStoragePermissions
) {

    fun saveScreenshot(bitmap: Bitmap, data: ScreenshotMetadata) {
        permissions.checkPermissions()
        saver.saveScreenshot(ScreenshotToSave(ScreenshotSource.Bitmap(bitmap), data))
        session.add(data)
    }

    fun saveScreenshot(node: SemanticsNodeInteraction, data: ScreenshotMetadata) {
        permissions.checkPermissions()
        saver.saveScreenshot(ScreenshotToSave(ScreenshotSource.Node(node), data))
        session.add(data)
    }

    fun saveMetadata(): ScreenshotTestSession {
        // If there's nothing to save, don't attempt to at all:
        if (session.getScreenshotSessionMetadata().screenshotsData.isNotEmpty()) {
            saver.saveMetadata(session)
        }
        return session
    }
}

data class ScreenshotMetadata(@SerializedName("name") val name: String, @SerializedName("testClassName") val testClassName: String, @SerializedName("testName") val testName: String)
