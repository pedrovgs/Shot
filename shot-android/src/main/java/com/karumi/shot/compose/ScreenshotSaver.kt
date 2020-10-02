package com.karumi.shot.compose

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.ui.test.SemanticsNodeInteraction
import java.io.File
import java.io.FileOutputStream

class ScreenshotSaver(private val packageName: String, private val bitmapGenerator: SemanticsNodeBitmapGenerator) {

    @SuppressLint("SdCardPath")
    private val screenshotsFolder: String = "/sdcard/screenshots/$packageName/screenshots-default/"

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveScreenshot(screenshot: ScreenshotToSave) {
        val bitmap = bitmapGenerator.generateBitmap(screenshot)
        createScreenshotsFolderIfDoesNotExist()
        saveScreenshotBitmap(bitmap, screenshot.data)
        bitmap.recycle()
    }

    private fun createScreenshotsFolderIfDoesNotExist() {
        val folder = File(screenshotsFolder)
        if (!folder.exists()) {
            folder.mkdirs()
        }
    }

    fun saveMetadata(session: ScreenshotTestSession) {
        // TODO: Persist json metadata here with all the session information
    }

    private fun saveScreenshotBitmap(bitmap: Bitmap, data: ScreenshotMetadata) {
        val screenshotPath = getScreenshotSdCardPath(data)
        deletePreviousScreenshotIfExists(screenshotPath)
        FileOutputStream(screenshotPath).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }

    private fun deletePreviousScreenshotIfExists(screenshotPath: String) {
        val currentScreenshotFile = File(screenshotPath)
        if (currentScreenshotFile.exists()) {
            currentScreenshotFile.delete()
        }
    }

    private fun getScreenshotSdCardPath(data: ScreenshotMetadata): String = "$screenshotsFolder${data.name}.png"
}

data class ScreenshotToSave(val node: SemanticsNodeInteraction, val data: ScreenshotMetadata)