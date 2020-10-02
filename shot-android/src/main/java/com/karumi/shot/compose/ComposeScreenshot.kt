package com.karumi.shot.compose

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.ui.test.SemanticsNodeInteraction
import com.google.gson.annotations.SerializedName

class ComposeScreenshot(
    private val session: ScreenshotTestSession,
    private val saver: ScreenshotSaver
) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveScreenshot(node: SemanticsNodeInteraction, data: ScreenshotMetadata) {
        saver.saveScreenshot(ScreenshotToSave(node, data))
        session.add(data)
    }

    fun saveMetadata(): ScreenshotTestSession {
        saver.saveMetadata(session)
        return session
    }
}

data class ScreenshotMetadata(@SerializedName("name") val name: String)