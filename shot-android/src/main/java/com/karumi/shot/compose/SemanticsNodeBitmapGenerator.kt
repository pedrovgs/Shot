package com.karumi.shot.compose

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.ui.test.captureToBitmap

class SemanticsNodeBitmapGenerator {

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateBitmap(screenshot: ScreenshotSource.Node): Bitmap = screenshot.node.captureToBitmap()
}