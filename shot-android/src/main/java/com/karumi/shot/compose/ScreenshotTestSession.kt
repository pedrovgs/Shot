package com.karumi.shot.compose

class ScreenshotTestSession {

    companion object {
        val empty: ScreenshotTestSession = ScreenshotTestSession()
    }

    private var session = ScreenshotSessionMetadata()

    fun add(data: ScreenshotMetadata): ScreenshotTestSession {
        session = session.save(data)
        return this
    }
}

data class ScreenshotSessionMetadata(val screenshotsData: List<ScreenshotMetadata> = emptyList()) {

    fun save(data: ScreenshotMetadata): ScreenshotSessionMetadata = copy(screenshotsData = screenshotsData + data)
}