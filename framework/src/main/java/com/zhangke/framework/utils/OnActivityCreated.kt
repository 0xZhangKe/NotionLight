package com.zhangke.framework.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle

@Suppress("FunctionName")
fun OnActivityCreated(block: (activity: Activity) -> Unit): Application.ActivityLifecycleCallbacks {
    return object : Application.ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            block(activity)
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
        }

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }
    }
}