package com.karumi.shot.compose

import com.google.gson.annotations.SerializedName

class ScreenshotTestSession {

    companion object {
        @Deprecated("ScreenshotTestSession is mutable and thus this instance cannot be guaranteed to be empty.")
        val empty: ScreenshotTestSession = ScreenshotTestSession()
    }

    private var session = ScreenshotSessionMetadata()

    fun add(data: ScreenshotMetadata): ScreenshotTestSession {
        session = session.save(data)
        return this
    }

    fun getScreenshotSessionMetadata() = session.copy()
}

data class ScreenshotSessionMetadata(@SerializedName("screenshots") val screenshotsData: List<ScreenshotMetadata> = emptyList()) {

    fun save(data: ScreenshotMetadata): ScreenshotSessionMetadata = copy(screenshotsData = screenshotsData + data)
}
