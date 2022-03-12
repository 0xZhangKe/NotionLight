package com.zhangke.notionlib.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri

object NotionAuthorization {

    private const val REDIRECT_URL = "https://notionauth.zhangkenotion.net/auth"

    fun startAuth(activity: Activity) {
        val url = buildAuthUrl()
        openUrlWithBrowse(url, activity)
    }

    private fun openUrlWithBrowse(url: String, activity: Activity) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        activity.startActivity(intent)
    }

    private fun buildAuthUrl(): String {
        val authUrlBuilder = StringBuilder()
        authUrlBuilder.append("https://api.notion.com/v1/oauth/authorize")
        authUrlBuilder.append("?owner=user")
        authUrlBuilder.append("&client_id=200bca3a-ff25-437e-82dc-2dfd7fc1f1f3")
        authUrlBuilder.append("&response_type=code")
        authUrlBuilder.append("&redirect_uri=$REDIRECT_URL")
        return authUrlBuilder.toString()
    }
}