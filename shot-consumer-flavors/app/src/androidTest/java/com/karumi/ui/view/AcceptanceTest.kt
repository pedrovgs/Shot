package com.karumi.ui.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.github.salomonbrys.kodein.Kodein
import com.karumi.asApp
import com.karumi.shot.ScreenshotTest
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import com.karumi.shot.waitForActivity

@LargeTest
@RunWith(AndroidJUnit4::class)
abstract class AcceptanceTest<T : Activity>(private val clazz: Class<T>) : ScreenshotTest {
    private var scenario: ActivityScenario<T>? = null

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val app = InstrumentationRegistry.getInstrumentation().targetContext.asApp()
        app.resetInjection()
        app.overrideModule = testDependencies
        Intents.init()
    }

    @After
    fun tearDown() {
        scenario?.close()
        Intents.release()
    }

    fun startActivity(args: Bundle = Bundle()): T {
        val intent = Intent(ApplicationProvider.getApplicationContext(), clazz)
        intent.putExtras(args)
        scenario = ActivityScenario.launch(intent)
        return scenario!!.waitForActivity()
    }

    abstract val testDependencies: Kodein.Module
}
