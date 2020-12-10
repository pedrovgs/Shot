package com.karumi.shot.compose

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.compose.ui.test.SemanticsNodeInteraction
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.lang.IllegalArgumentException

class ScreenshotSaver(private val packageName: String, private val bitmapGenerator: SemanticsNodeBitmapGenerator) {

    @SuppressLint("SdCardPath")
    private val screenshotsFolder: String = "/sdcard/screenshots/$packageName/screenshots-compose-default/"
    private val metadataFile: String = "$screenshotsFolder/metadata.json"
    private val gson: Gson = Gson()

    fun saveScreenshot(screenshot: ScreenshotToSave) {
        if (Build.VERSION.SDK_INT >= 30) {
            Log.w(
                "Shot",
                "Can't save screenshot bitmap on Android OS ${Build.VERSION.SDK_INT} on applications with Target SDK >= 30." +
                    "If your app has Target SDK <= 29, you should add \"android:requestLegacyExternalStorage=\"true\"\" on your test manifest."
            )
        }

        val bitmap = getBitmapFromScreenshotToSave(screenshot)
        createScreenshotsFolderIfDoesNotExist()
        saveScreenshotBitmap(bitmap, screenshot.data)
    }

    private fun getBitmapFromScreenshotToSave(screenshot: ScreenshotToSave) = when (screenshot.source) {
        is ScreenshotSource.Bitmap -> screenshot.source.bitmap
        is ScreenshotSource.Node ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bitmapGenerator.generateBitmap(screenshot.source)
            } else {
                throw IllegalArgumentException("Can't extract bitmap from node in a SDK version lower than Build.VERSION_CODES.O")
            }
    }

    fun saveMetadata(session: ScreenshotTestSession) {
        if (Build.VERSION.SDK_INT >= 30) {
            Log.w(
                "Shot",
                "Can't save screenshot bitmap on Android OS ${Build.VERSION.SDK_INT} on applications with Target SDK >= 30." +
                    "If your app has Target SDK <= 29, you should add \"android:requestLegacyExternalStorage=\"true\"\" on your test manifest."
            )
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
     * Creates a file at [path] using the [File] API.
     * When API 29+ is the target SDK or the Device API not all combinations all available.
     * This will work with the following combinations:
     * | APP Target SDK | Device API | requestLegacyExternalStorage (Manifest) |
     *          30     |      29     |             true                        |
     *          29     |      29     |             true                        |
     *          29     |      30     |             true                        |
     * This method can't work on apps with Target SDK >= 30 and devices with API >= 30.
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

data class ScreenshotToSave(val source: ScreenshotSource, val data: ScreenshotMetadata)

sealed class ScreenshotSource {
    data class Node(val node: SemanticsNodeInteraction) : ScreenshotSource()
    data class Bitmap(val bitmap: android.graphics.Bitmap) : ScreenshotSource()
}
