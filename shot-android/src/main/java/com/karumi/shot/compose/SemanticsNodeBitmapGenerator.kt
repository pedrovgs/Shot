package com.karumi.shot.compose

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage

class SemanticsNodeBitmapGenerator {

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateBitmap(screenshot: ScreenshotSource.Node): Bitmap = screenshot.node.captureToImage().asAndroidBitmap()
}
