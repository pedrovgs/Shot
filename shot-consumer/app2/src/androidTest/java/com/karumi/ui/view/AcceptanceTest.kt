package com.karumi.ui.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.github.salomonbrys.kodein.Kodein
import com.karumi.asApp
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations

@LargeTest
@RunWith(AndroidJUnit4::class)
abstract class AcceptanceTest<T : Activity>(clazz: Class<T>) : ScreenshotTest {

    @Rule
    @JvmField
    val testRule: IntentsTestRule<T> = IntentsTestRule(clazz, true, false)

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val app = InstrumentationRegistry.getInstrumentation().targetContext.asApp()
        app.resetInjection()
        app.overrideModule = testDependencies
    }

    fun startActivity(args: Bundle = Bundle()): T {
        val intent = Intent()
        intent.putExtras(args)
        return testRule.launchActivity(intent)
    }

    abstract val testDependencies: Kodein.Module
}