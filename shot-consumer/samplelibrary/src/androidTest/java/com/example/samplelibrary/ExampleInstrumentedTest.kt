package com.example.samplelibrary

import android.view.LayoutInflater
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import com.karumi.shot.ScreenshotTest

class ExampleInstrumentedTest : ScreenshotTest {
    @Test
    fun takeAScreenshotOfAnInflatedLayout() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val view = LayoutInflater.from(context).inflate(R.layout.sample_layout, null, false)

        compareScreenshot(view = view, heightInPx = 400, widthInPx = 800)
    }
}
