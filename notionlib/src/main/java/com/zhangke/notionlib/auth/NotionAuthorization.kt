package com.zhangke.notionlib.auth

import android.content.Intent
import android.net.Uri
import com.zhangke.architect.coroutines.ApplicationScope
import com.zhangke.architect.datastore.dataStore
import com.zhangke.architect.datastore.getString
import com.zhangke.architect.datastore.putString
import com.zhangke.architect.datastore.removeString
import com.zhangke.framework.utils.appContext
import com.zhangke.framework.utils.sharedGson
import com.zhangke.framework.utils.toast
import com.zhangke.notionlib.BuildConfig
import com.zhangke.notionlib.NotionRepo
import com.zhangke.notionlib.data.OauthToken
import io.reactivex.rxjava3.subjects.SingleSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object NotionAuthorization {

    private const val TAG = "NotionAuthorization"

    const val REDIRECT_URL = "https://0xzhangke.github.io/notion_light_oauth_result.html"

    private var token: OauthToken? = null

    val readTokenSubject: SingleSubject<Boolean> = SingleSubject.create()

    val loginStateFlow = MutableSharedFlow<Boolean>()

    var onNeedShowAuthPage: (() -> Unit)? = null

    private const val OAUTH_TOKEN_KEY = "oauth_token"

    fun start() {
        ApplicationScope.launch(Dispatchers.IO) {
            val oauthTokenJson = appContext.dataStore.getString(OAUTH_TOKEN_KEY)
            if (!oauthTokenJson.isNullOrEmpty()) {
                token = sharedGson.fromJson(oauthTokenJson, OauthToken::class.java)
            }
            readTokenSubject.onSuccess(true)
        }
    }

    fun getOauthToken(): OauthToken? {
        return token
    }

    fun logout() {
        this.token = null
        runBlocking {
            appContext.dataStore.removeString(OAUTH_TOKEN_KEY)
            loginStateFlow.emit(false)
        }
    }

    private suspend fun saveOauthToken(token: OauthToken) {
        this.token = token
        val json = sharedGson.toJson(token)
        appContext.dataStore.putString(OAUTH_TOKEN_KEY, json)
        loginStateFlow.emit(true)
    }

    fun showAuthPage() {
        onNeedShowAuthPage?.invoke()
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
        authUrlBuilder.append("&client_id=${BuildConfig.CLIENT_ID}")
        authUrlBuilder.append("&response_type=code")
        authUrlBuilder.append("&redirect_uri=$REDIRECT_URL")
        return authUrlBuilder.toString()
    }

    suspend fun startRequestOauthToken(code: String, oauthResult: (String?) -> Unit) {
        withContext(Dispatchers.IO) {
            val response = NotionRepo.requestOathToken(code)
            response.onSuccess {
                saveOauthToken(it)
                withContext(Dispatchers.Main) {
                    oauthResult(null)
                }
            }

            response.onError {
                withContext(Dispatchers.Main) {
                    toast(it.message)
                    oauthResult(it.message)
                }
            }
        }
    }
}