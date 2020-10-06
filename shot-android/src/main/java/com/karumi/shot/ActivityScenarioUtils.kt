package com.karumi.shot

import android.app.Activity
import androidx.test.core.app.ActivityScenario
import java.lang.IllegalStateException

/* ActivityTestRule has been deprecated and now the usage of ActivityScenario is recommended.
 * However, Shot needs to be executed from the instrumentation thread to be able to extract
 * all the test metadata needed to record and verify screenshots. That's why we've created this
 * extension to be able to get the activity instance from the instrumentation thread instead
 * of running Shot from the app target thread. I hope we can find a better solution in the future.
 */
fun <A : Activity> ActivityScenario<A>.waitForActivity(): A {
    var activity: A? = null
    onActivity {
        activity = it
    }
    return if (activity != null) {
        activity!!
    } else {
        throw IllegalStateException("The activity scenario could not be initialized.")
    }
}
