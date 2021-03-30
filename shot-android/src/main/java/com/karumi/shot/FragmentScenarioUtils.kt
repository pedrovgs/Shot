package com.karumi.shot

import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario

/**
 * Shot needs to be executed from the instrumentation thread to be able to extract
 * all the test metadata needed to record and verify screenshots. That's why we've created this
 * extension to be able to get the fragment instance from the instrumentation thread instead
 * of running Shot from the app target thread. I hope we can find a better solution in the future.
 *
 * Implementation is analogous to [ActivityScenarioUtils.waitForActivity].
 */
object FragmentScenarioUtils {
    fun <F : Fragment> FragmentScenario<F>.waitForFragment(): F {
        var fragment: F? = null
        onFragment {
            fragment = it
        }
        return if (fragment != null) {
            fragment!!
        } else {
            throw IllegalStateException("The fragment scenario could not be initialized.")
        }
    }
}
