package com.zhangke.notionlib.auth

import android.content.Intent
import android.net.Uri
import android.util.Log
import com.zhangke.framework.utils.appContext
import com.zhangke.framework.utils.toast
import com.zhangke.notionlib.NotionRepo
import com.zhangke.notionlib.data.OauthToken
import io.reactivex.rxjava3.subjects.SingleSubject
import kotlinx.coroutines.*

object NotionAuthorization {

    private const val TAG = "NotionAuthorization"

    const val REDIRECT_URL = "https://notionauth.zhangkenotion.net/auth"

    private var token: OauthToken? = null
    val readTokenSubject: SingleSubject<Boolean> = SingleSubject.create()

    fun start() {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                token = NotionRepo.getLocalOauthToken()
                readTokenSubject.onSuccess(true)
            }
        }
    }

    fun getOauthToken(): OauthToken? {
        return token
    }

    fun startAuth() {
        val url = buildAuthUrl()
        openUrlWithBrowse(url)
    }

    private fun openUrlWithBrowse(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        appContext.startActivity(intent)
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

    fun handleResultForOauth(intent: Intent): Boolean {
        val data = intent.data
        if (data == null) {
            Log.d(TAG, "data is null")
            return false
        }
        val code = data.getQueryParameter("code")
        Log.d(TAG, "code=${code}")
        if (code == null) {
            return false
        }
        GlobalScope.launch { requestOauthToken(code) }
        return true
    }

    private suspend fun requestOauthToken(code: String) {
        withContext(Dispatchers.IO) {
            val response = NotionRepo.requestOathToken(code)
            response.onSuccess {
                NotionRepo.saveOauthToken(it)
            }
            response.onError {
                toast(it.message)
            }
        }
    }
}