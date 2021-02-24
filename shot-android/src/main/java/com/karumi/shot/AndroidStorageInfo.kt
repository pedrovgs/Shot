package com.karumi.shot

import android.os.Environment

object AndroidStorageInfo {
    // This usually points at "/storage/emulated/0/Download" which is the only folder
    // in the device we can use without handling the scoped storage API. Before using
    // this folder we used "/sdcard
    val storageBaseUrl: String by lazy {
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
    }
}
