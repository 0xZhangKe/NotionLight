package com.zhangke.notionlight.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import com.zhangke.framework.utils.toast
import com.zhangke.notionlight.R

object IntentUtils {

    fun openAppMarketForSelf(activity: Activity) {
        try {
            openExternalPage(activity, "market://details?id=${activity.packageName}")
        } catch (e: ActivityNotFoundException) {
            toast(R.string.error_app_market_not_found)
        }
    }

    fun openExternalPage(activity: Activity, uriStr: String) {
        val uri = Uri.parse(uriStr)
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activity.startActivity(intent)
    }
}