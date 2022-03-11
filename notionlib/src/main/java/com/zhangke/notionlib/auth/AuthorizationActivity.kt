package com.zhangke.notionlib.auth

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class AuthorizationActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val webView = createWebView()
        setContentView(webView)
        webView.loadData(buildContent(), null, null)
    }

    private fun buildContent(): String{
        return """<a href="${buildAuthUrl()}">Add to Notion</a>"""
    }

    /**
     * <a href="https://api.notion.com/v1/oauth/authorize?owner=user&client_id=463558a3-725e-4f37-b6d3-0889894f68de
     * &redirect_uri=https%3A%2F%2Fexample.com%2Fauth%2Fnotion%2Fcallback
     * &response_type=code">Add to Notion</a>
     */
    private fun buildAuthUrl(): String{
        val authUrlBuilder = StringBuilder()
        authUrlBuilder.append("https://api.notion.com/v1/oauth/authorize")
        authUrlBuilder.append("?owner=user")
        authUrlBuilder.append("&client_id=200bca3a-ff25-437e-82dc-2dfd7fc1f1f3")
        authUrlBuilder.append("&response_type=code")
        authUrlBuilder.append("&redirect_uri=https%3A%2F%2F0xzhangke.github.io%2F")
        return authUrlBuilder.toString()
    }

    private fun createWebView(): WebView{
        val webView = WebView(this)
        return webView
    }
}