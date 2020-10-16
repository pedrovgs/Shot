package com.karumi.shot.compose

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.ui.test.SemanticsNodeInteraction
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream

class ScreenshotSaver(private val packageName: String, private val bitmapGenerator: SemanticsNodeBitmapGenerator) {

    @SuppressLint("SdCardPath")
    private val screenshotsFolder: String = "/sdcard/screenshots/$packageName/screenshots-compose-default/"
    private val metadataFile: String = "$screenshotsFolder/metadata.json"
    private val gson: Gson = Gson()

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveScreenshot(screenshot: ScreenshotToSave) {
        if (Build.VERSION.SDK_INT >= 29) {
            Log.w("Shot", "Can't save screenshot bitmap on Android OS ${Build.VERSION.SDK_INT}")
            return
        }

        val bitmap = bitmapGenerator.generateBitmap(screenshot)
        createScreenshotsFolderIfDoesNotExist()
        saveScreenshotBitmap(bitmap, screenshot.data)
    }

    fun saveMetadata(session: ScreenshotTestSession) {
        if (Build.VERSION.SDK_INT >= 29) {
            Log.w("Shot", "Can't save metadata file on Android OS ${Build.VERSION.SDK_INT}")
            return
        }

        createScreenshotsFolderIfDoesNotExist()
        val metadata = session.getScreenshotSessionMetadata()
        val serializedMetadata = gson.toJson(metadata)
        saveSerializedMetadata(serializedMetadata)
    }

    private fun createScreenshotsFolderIfDoesNotExist() {
        val folder = File(screenshotsFolder)
        if (!folder.exists()) {
            folder.mkdirs()
        }
    }

    private fun saveSerializedMetadata(serializedMetadata: String) {
        deleteFileIfExists(metadataFile)
        val file = createFileIfNotExists(metadataFile)
        val printWriter = file.printWriter()
        printWriter.print(serializedMetadata)
        printWriter.close()
    }

    private fun saveScreenshotBitmap(bitmap: Bitmap, data: ScreenshotMetadata) {
        val screenshotPath = getScreenshotSdCardPath(data)
        deleteFileIfExists(screenshotPath)
        createFileIfNotExists(screenshotPath)
        val fileOutputStream = FileOutputStream(screenshotPath)
        fileOutputStream.use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        fileOutputStream.close()
    }

    private fun deleteFileIfExists(path: String) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }

    /**
     * Creates a file at [path] using the [File] API (does not work on API 29+).
     */
    private fun createFileIfNotExists(path: String): File {
        val file = File(path)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    private fun getScreenshotSdCardPath(data: ScreenshotMetadata): String = "$screenshotsFolder${data.name}.png"
}

data class ScreenshotToSave(val node: SemanticsNodeInteraction, val data: ScreenshotMetadata)
