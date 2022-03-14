package com.zhangke.notionlib

import com.zhangke.architect.network.newRetrofit
import com.zhangke.framework.utils.*
import com.zhangke.notionlib.auth.NotionAuthorization
import com.zhangke.notionlib.data.OauthToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

object NotionRepo {

    private const val OAUTH_TOKEN_KEY = "oauth_token"

    private val notionApi: NotionApi by lazy {
        newRetrofit("https://api.notion.com").create(
            NotionApi::class.java
        )
    }

    suspend fun getLocalOauthToken(): OauthToken? {
        val oauthTokenJson = appContext.dataStore.getString(OAUTH_TOKEN_KEY) ?: return null
        return sharedGson.fromJson(oauthTokenJson, OauthToken::class.java)
    }

    suspend fun saveOauthToken(token: OauthToken) {
        val json = sharedGson.toJson(token)
        appContext.dataStore.putString(OAUTH_TOKEN_KEY, json)
    }

    suspend fun requestOathToken(code: String): OauthToken {
        val json = json {
            "grant_type" kv "authorization_code"
            "code" kv code
            "redirect_uri" kv NotionAuthorization.REDIRECT_URL
        }.toString()
        val body = json.toRequestBody("application/json".toMediaType())
        return notionApi.getOauthToken(body)
    }
}